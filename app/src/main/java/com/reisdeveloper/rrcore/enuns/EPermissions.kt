package com.reisdeveloper.rrcore.enuns

import android.Manifest
import com.reisdeveloper.rrcore.permission.PermissionDetails

enum class EPermissions(
    val mens: String,
    val requestCode: Int,
    vararg val PermissionDetails: PermissionDetails
) {
    PERMISSION_FOTOS_ARQUIVOS(
        "Acesse fotos, mídia e arquivos do seu aparelho.",
        101,
        PermissionDetails(
            0,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ),
    PERMISSION_CAMERA(
        "Acesse sua câmera.",
        102,
        PermissionDetails(
            0,
            Manifest.permission.CAMERA
        )
    ),
    PERMISSION_CONTATOS(
        "Acesse os seus contatos.",
        103,
        PermissionDetails(0, Manifest.permission.READ_CONTACTS)
    ),
    PERMISSION_GPS_FOREGROUND(
        "Acesse sua localização enquanto ao aplicativo está em uso.",
        104,
        PermissionDetails(
            0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ),
    PERMISSION_GPS_BACKGROUND(
        "Acesse sua localização em segundo plano.",
        104,
        PermissionDetails(
            0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        PermissionDetails(
            29,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    ),
    PERMISSION_TELEFONE(
        "Gerencie informações sobre o telefone.",
        105,
        PermissionDetails(0, Manifest.permission.READ_PHONE_STATE)
    );
}