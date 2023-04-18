package ru.rikmasters.gilty.core.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private const val Coarse = ACCESS_COARSE_LOCATION
private const val Fine = ACCESS_FINE_LOCATION

object LocationService {
    
    private val location =
        MutableStateFlow<Pair<Double, Double>?>(null)
    
    private val scope = CoroutineScope(Default)
    
    suspend fun getLocation(context: Context) =
        context.checkLocationPermissions {
            context listen {
                location.emit(
                    value = it.latitude to it.longitude
                )
            }
        }.let { location.value }
    
    @SuppressLint("MissingPermission")
    private suspend infix fun Context.listen(
        block: suspend (Location) -> Unit,
    ) = getFusedLocationProviderClient(this)
        .lastLocation
        .addOnSuccessListener {
            it?.let { scope.launch { block(it) } }
        }
    
    private infix fun Context.checkLocationPermissions(
        block: suspend () -> Unit,
    ) {
        if(
            this checkGranted Coarse
            && this checkGranted Fine
        ) {
            requestPermissions(
                this as Activity,
                arrayOf(Fine, Coarse),
                (101)
            )
        } else scope.launch { block() }
    }
    
    private infix fun Context.checkGranted(
        permission: String,
    ) = checkSelfPermission(
        this, permission
    ) != PERMISSION_GRANTED
}
