package com.reisdeveloper.rrcore.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.reisdeveloper.rrcore.R
import com.reisdeveloper.rrcore.enuns.EPermissions
import com.reisdeveloper.rrcore.enuns.RRAlertType
import com.reisdeveloper.rrcore.style.RRDialog

/**
 * Created by Raphael Rodrigues
 * Updated by Rodrigo Reis
 */

class RRPermission private constructor() {

    private var appName: String? = null
    private var mainMessage: String? = null
    private var fragment: Fragment? = null
    private var activity: Activity? = null
    private lateinit var ctxt: Context

    private val permissionsList = mutableListOf<Permission>()

    private val enumSolitedPermissions: MutableList<EPermissions> by lazy {
        mutableListOf<EPermissions>()
    }

    private val auxilarList: MutableList<EPermissions> by lazy {
        mutableListOf<EPermissions>()
    }

    constructor(context: Any, appName: String, mainMessage: String) : this() {
        when (context) {
            is Fragment -> this.fragment = context
            is Activity -> this.activity = context
            else -> this.ctxt = context as Context
        }
        this.appName = appName
        this.mainMessage = mainMessage
    }

    private val context: Context?
        get() = fragment?.context ?: activity ?: ctxt

    private var callback: OnRequestPermission? = null

    fun onRequestPermissionCallBack(callback: OnRequestPermission) {
        this.callback = callback
    }

    fun getPermission(permissions: MutableList<String>) {
        if (fragment != null)
            fragment?.requestPermissions(permissions.toTypedArray(), 1)

        activity?.let {
            ActivityCompat.requestPermissions(it, permissions.toTypedArray(), 1)
        }
    }

    fun init() {
        enumSolitedPermissions.clear()
        enumSolitedPermissions.addAll(auxilarList)
        auxilarList.clear()

        val mensArray = mutableListOf<String>()
        val permissions = mutableListOf<String>()
        val enums = mutableListOf<EPermissions?>()

        if (permissionsList.isNotEmpty()) {
            permissionsList.forEach { permission ->
                if (!isAllowed(permission.permissions)) {

                    permission.permissions.forEach {
                        permissions.add(it)
                    }
                    mensArray.add(permission.message)
                } else {
                    enums.add(getEnumsByStringPermission(permission.permissions))
                }
            }

            if (enums.isNotEmpty()) {
                callback?.allowed(enums)
            }

            if (permissions.size > 0) {
                chamarExplicacao(gerarMensagem(this.appName, mainMessage), permissions, mensArray)
            }
        }
        permissionsList.clear()
    }

    /**
     * Verify that all permissions that have been added have been accepted.
     *
     * @return `true` case all permissions are garanted
     */
    fun isAllowed(): Boolean {
        val lista = mutableListOf<Boolean>()
        enumSolitedPermissions.forEach {
            it.PermissionDetails.filterPermissionBySDK().forEach { d ->
                lista.add(isAllowed(d.permission))
            }
        }

        lista.forEach {
            if (!it)
                return false
        }

        return true
    }

    /**
     * Check if a certain permission that was requested was accepted by the user
     *
     * @return `true` case the permission are garanted
     */
    fun isAllowed(enum: EPermissions): Boolean {
        val lista = mutableListOf<Boolean>()
        enum.PermissionDetails.filterPermissionBySDK().forEach { d ->
            lista.add(isAllowed(d.permission))
        }

        lista.forEach {
            if (!it)
                return false
        }

        return true
    }

    private fun isAllowed(permissions: Array<out String>): Boolean {
        val lista = mutableListOf<Boolean>()
        context?.let { context ->
            for (permission in permissions)
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    lista.add(false)
                }
        }

        if (lista.size > 0)
            return false

        return true
    }

    private fun gerarMensagem(appName: String?, mensagem: String? = null): String {
        return if (mensagem.isNullOrEmpty()) {
            String.format(
                "%s %s ",
                context?.getString(R.string.for_a_better_experience_accept_the_permission),
                appName
            )
        } else {
            mensagem
        }
    }

    private fun chamarExplicacao(
        title: String,
        permissoes: MutableList<String>,
        messages: List<String>
    ) {
        context?.let { c ->
            RRDialog(c).showCustomDialog(
                title = title,
                message = messages.joinToString("\n- "),
                alertType = RRAlertType.PERMISSION_TYPE,
                confirmText = context?.getString(R.string.ok),
                negativeText = context?.getString(R.string.cancel)
            ) {  typeReturn, _ ->
                if(typeReturn == context?.getString(R.string.ok))
                    callback?.continuePermission(permissoes)
                else
                    callback?.notNow()
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        val listaPermissoesAceitas = mutableListOf<String>()
        val listaPermissoesNegadas = mutableListOf<String>()
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                listaPermissoesAceitas.add(permissions[i])
            } else {
                listaPermissoesNegadas.add(permissions[i])
            }
        }

        val listaEnumAceito = getListEnumByPermissionList(listaPermissoesAceitas)
        val listaEnumNegado = getListEnumByPermissionList(listaPermissoesNegadas)

        if (listaEnumAceito.isNotEmpty())
            callback?.permissionGaranted(listaEnumAceito)

        if (listaEnumNegado.isNotEmpty())
            callback?.permissionDanied(listaEnumNegado)
    }

    fun addPermission(message: String? = null, enume: EPermissions) {
        auxilarList.add(enume)
        val ret = enume.PermissionDetails
            .filterPermissionBySDK()
            .flatMap { m -> m.permission.toList() }

        permissionsList.add(
            Permission(enume.requestCode, message ?: enume.mens, ret.toTypedArray())
        )
    }

    private fun getListEnumByPermissionList(lista: MutableList<String>): List<EPermissions> {
        val listaEnum = mutableListOf<EPermissions>()
        enumSolitedPermissions.filter { l ->
            l.PermissionDetails
                .filterPermissionBySDK()
                .flatMap { d -> d.permission.toList() }
                .any { s -> lista.contains(s) }
        }.forEach {
            listaEnum.add(it)
        }

        return listaEnum.toList()
    }

    private fun getEnumsByStringPermission(permissao: Array<String>): EPermissions? {
        var enum: EPermissions? = null

        enumSolitedPermissions.filter { l ->
            l.PermissionDetails
                .filterPermissionBySDK()
                .flatMap { d -> d.permission.toList() }
                .any { s -> permissao.contains(s) }
        }.forEach {
            enum = it
        }

        return enum
    }

    interface OnRequestPermission {
        fun notNow()
        fun continuePermission(permissoes: MutableList<String>)
        fun permissionDanied(enums: List<EPermissions>)
        fun permissionGaranted(enums: List<EPermissions>)
        fun allowed(enums: List<EPermissions?>)
    }

    private fun Array<out PermissionDetails>.filterPermissionBySDK(): List<PermissionDetails> {
        return this.filter { p -> p.vMin <= Build.VERSION.SDK_INT }
    }

}