@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.core.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.*

private const val Coarse = ACCESS_COARSE_LOCATION
private const val Fine = ACCESS_FINE_LOCATION

fun Activity.checkGPS() = (getSystemService(
    LOCATION_SERVICE
) as LocationManager).let {
    it.isProviderEnabled(
        GPS_PROVIDER
    ) && it.isProviderEnabled(
        NETWORK_PROVIDER
    )
}


fun Activity.checkLocationPermissions() = if(
    this checkGranted Coarse
    && this checkGranted Fine
) {
    requestPermissions(
        this,
        arrayOf(Fine, Coarse),
        (101)
    )
    false
} else true

private infix fun Context.checkGranted(
    permission: String,
) = checkSelfPermission(
    this, permission
) != PERMISSION_GRANTED