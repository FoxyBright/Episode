package ru.rikmasters.gilty.login.presentation.ui.permissions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.PermissionViewModel

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionsScreen(vm: PermissionViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val locationPermissionState = rememberPermissionState(
        ACCESS_FINE_LOCATION
    )
    
    val notification by vm.notification.collectAsState()
    
    PermissionsContent(
        PermissionsState(
            locationPermissionState.hasPermission,
            notification
        ), Modifier, object: PermissionsCallback {
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun requestPermission() {
                locationPermissionState.launchPermissionRequest()
            }
            
            override fun notificationChange() {
                scope.launch {
                    vm.setNotificationPermission()
                }
            }
            
            override fun onNext() {
                nav.navigateAbsolute("main/meetings")
            }
        })
}