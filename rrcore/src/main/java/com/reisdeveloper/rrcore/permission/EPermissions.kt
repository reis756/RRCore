package com.reisdeveloper.rrcore.permission

import android.Manifest

enum class EPermissions(
    val requestCode: Int,
    vararg val permissionDetails: RRPermission.PermissionDetails
) {
    PERMISSION_READ_WRITE_STORAGE(
    101,
        RRPermission.PermissionDetails(
            0,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ),
    PERMISSION_CAMERA(
    102,
        RRPermission.PermissionDetails(
            0,
            Manifest.permission.CAMERA
        )
    ),
    PERMISSION_CONTACTS(
    103,
        RRPermission.PermissionDetails(0, Manifest.permission.READ_CONTACTS)
    ),
    PERMISSION_GPS_FOREGROUND(
    104,
        RRPermission.PermissionDetails(
            0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ),
    PERMISSION_GPS_BACKGROUND(
    104,
        RRPermission.PermissionDetails(
            0,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        RRPermission.PermissionDetails(
            29,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    ),
    PERMISSION_PHONE_STATE(
    105,
        RRPermission.PermissionDetails(0, Manifest.permission.READ_PHONE_STATE)
    );
}