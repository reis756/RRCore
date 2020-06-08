package com.reisdeveloper.rrcore.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.reisdeveloper.rrcore.R
import com.reisdeveloper.rrcore.style.RRAlertType
import com.reisdeveloper.rrcore.style.RRDialog

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

    constructor(context: Any, appName: String, mainMessage: String? = null) : this() {
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

    fun init(imageTitle: Drawable? = null) {
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
                dialogPrePermissionMessage(prePermissionMessage(mainMessage), permissions, mensArray, imageTitle)
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
            it.permissionDetails.filterPermissionBySDK().forEach { d ->
                lista.add(isAllowed(d.stringPermission))
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
        enum.permissionDetails.filterPermissionBySDK().forEach { d ->
            lista.add(isAllowed(d.stringPermission))
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

    private fun prePermissionMessage(mensagem: String? = null): String {
        return if (mensagem.isNullOrEmpty()) {
            String.format("%s", context?.getString(R.string.need_permission))
        } else {
            mensagem
        }
    }

    private fun dialogPrePermissionMessage(
        title: String,
        permissoes: MutableList<String>,
        messages: List<String>,
        imageTitle: Drawable? = null
    ) {
        context?.let { c ->
            if(imageTitle != null) {
                RRDialog(c).showCustomDialog(
                    title = title,
                    imageTitle = imageTitle,
                    message = messages.joinToString("\n- "),
                    confirmText = context?.getString(R.string.ok),
                    negativeText = context?.getString(R.string.cancel)
                ) { typeReturn, _ ->
                    if (typeReturn == context?.getString(R.string.ok))
                        callback?.continuePermission(permissoes)
                    else
                        callback?.notNow()
                }
            }else{
                RRDialog(c).showCustomDialog(
                    title = title,
                    message = messages.joinToString("\n- "),
                    confirmText = context?.getString(R.string.ok),
                    negativeText = context?.getString(R.string.cancel)
                ) { typeReturn, _ ->
                    if (typeReturn == context?.getString(R.string.ok))
                        callback?.continuePermission(permissoes)
                    else
                        callback?.notNow()
                }
            }

        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        val listPermissionsGranted = mutableListOf<String>()
        val listPermissionsDanied = mutableListOf<String>()
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                listPermissionsGranted.add(permissions[i])
            } else {
                listPermissionsDanied.add(permissions[i])
            }
        }

        val listEnumAcept = getListEnumByPermissionList(listPermissionsGranted)
        val listEnumDanied = getListEnumByPermissionList(listPermissionsDanied)

        if (listEnumAcept.isNotEmpty())
            callback?.permissionGranted(listEnumAcept)

        if (listEnumDanied.isNotEmpty())
            callback?.permissionDanied(listEnumDanied)
    }

    fun addPermission(message: String, enume: EPermissions) {
        auxilarList.add(enume)
        val ret = enume.permissionDetails
            .filterPermissionBySDK()
            .flatMap { m -> m.stringPermission.toList() }

        permissionsList.add(
            Permission(enume.requestCode, message, ret.toTypedArray())
        )
    }

    private fun getListEnumByPermissionList(lista: MutableList<String>): List<EPermissions> {
        val listaEnum = mutableListOf<EPermissions>()
        enumSolitedPermissions.filter { l ->
            l.permissionDetails
                .filterPermissionBySDK()
                .flatMap { d -> d.stringPermission.toList() }
                .any { s -> lista.contains(s) }
        }.forEach {
            listaEnum.add(it)
        }

        return listaEnum.toList()
    }

    private fun getEnumsByStringPermission(permissao: Array<String>): EPermissions? {
        var enum: EPermissions? = null

        enumSolitedPermissions.filter { l ->
            l.permissionDetails
                .filterPermissionBySDK()
                .flatMap { d -> d.stringPermission.toList() }
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
        fun permissionGranted(enums: List<EPermissions>)
        fun allowed(enums: List<EPermissions?>)
    }

    private fun Array<out PermissionDetails>.filterPermissionBySDK(): List<PermissionDetails> {
        return this.filter { p -> p.vMin <= Build.VERSION.SDK_INT }
    }

    class PermissionDetails(val vMin: Int, vararg val stringPermission: String)

    inner class Permission(var requestCode: Int, var message: String, var permissions: Array<String>)
}