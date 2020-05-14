package com.reisdeveloper.rrcore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.reisdeveloper.rrcore.enuns.EPermissions
import com.reisdeveloper.rrcore.permission.RRPermission
import kotlinx.android.synthetic.main.activity_permission_teste.*

class PermissionTesteActivity: AppCompatActivity() {

    companion object {
        private const val TAG: String = "TESTEPERMISSAO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_teste)

        val permissions = RRPermission(
            this@PermissionTesteActivity,
            getString(R.string.app_name),
            "Teste com mensagem"
        )

        permission_btCamera.setOnClickListener {
            permissions.addPermission("- Teste de mensagem personalizada", EPermissions.PERMISSION_CAMERA)
            permissions.addPermission(null, EPermissions.PERMISSION_CONTATOS)
            permissions.init()
        }


        permissions.onRequestPermissionCallBack(object : RRPermission.OnRequestPermission {
            override fun notNow() {
                Log.v(TAG, "agoraNao")
            }

            override fun continuePermission(permissoes: MutableList<String>) {
                Log.v(TAG, "continuar")
                permissions.init()
                permissions.getPermission(permissoes)
            }

            override fun permissionDanied(enums: List<EPermissions>) {
                Log.v(TAG, "permissaoNegada $enums")
            }

            override fun permissionGaranted(enums: List<EPermissions>) {
                Log.v(TAG, "permissaoAceita $enums")
                Log.v(TAG, "permissaoAceita ${permissions.isAllowed()}")
            }

            override fun allowed(enums: List<EPermissions?>) {
                Log.v(TAG, "permitido enum = $enums")

                permissions.isAllowed(EPermissions.PERMISSION_GPS_FOREGROUND)
            }
        })
    }
}