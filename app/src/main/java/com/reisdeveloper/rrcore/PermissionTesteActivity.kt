package com.reisdeveloper.rrcore

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.reisdeveloper.rrcore.permission.EPermissions
import com.reisdeveloper.rrcore.permission.RRPermission
import kotlinx.android.synthetic.main.activity_permission_teste.*

class PermissionTesteActivity: AppCompatActivity() {

    companion object {
        private const val TAG: String = "TESTEPERMISSAO"
    }

    private val permission by lazy {
        RRPermission(
            this@PermissionTesteActivity,
            getString(R.string.app_name)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_teste)

        permission_btCamera.setOnClickListener {
            permission.addPermission("- Teste de mensagem personalizada", EPermissions.PERMISSION_PHONE_STATE)
            permission.addPermission(getString(R.string.app_name), EPermissions.PERMISSION_READ_WRITE_STORAGE)
            permission.init()
        }


        permission.onRequestPermissionCallBack(object : RRPermission.OnRequestPermission {
            override fun notNow() {
                Log.v(TAG, "agoraNao")
            }

            override fun continuePermission(permissoes: MutableList<String>) {
                Log.v(TAG, "continuar")
                permission.getPermission(permissoes)
            }

            override fun permissionDanied(enums: List<EPermissions>) {
                Log.v(TAG, "permissaoNegada $enums")
            }

            override fun permissionGranted(enums: List<EPermissions>) {
                Log.v(TAG, "permissaoAceita $enums")
                Log.v(TAG, "permissaoAceita ${permission.isAllowed()}")
            }

            override fun allowed(enums: List<EPermissions?>) {
                Log.v(TAG, "permitido enum = $enums")

                permission.isAllowed(EPermissions.PERMISSION_GPS_FOREGROUND)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}