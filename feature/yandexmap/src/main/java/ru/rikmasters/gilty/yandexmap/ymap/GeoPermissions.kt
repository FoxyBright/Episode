package ru.rikmasters.gilty.yandexmap.ymap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions

fun Context.requestGeoPermissions() {
    this as Activity
    
    val coarse = ACCESS_COARSE_LOCATION
    val fine = ACCESS_FINE_LOCATION
    
    if(!checkPerm(fine) && !checkPerm(coarse))
        requestPermissions((this), arrayOf(fine, coarse), (0))
}

private fun Context.checkPerm(
    perm: String,
) = checkSelfPermission(
    (this), perm
) == PERMISSION_GRANTED