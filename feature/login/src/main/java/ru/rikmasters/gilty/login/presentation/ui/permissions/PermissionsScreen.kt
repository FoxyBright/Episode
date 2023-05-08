package ru.rikmasters.gilty.login.presentation.ui.permissions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.NotificationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.PermissionViewModel
import ru.rikmasters.gilty.shared.common.extentions.Permissions.Companion.openNotificationSettings

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionsScreen(vm: PermissionViewModel) {
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val notification by vm.notification.collectAsState()
    val locationPermissionState =
        rememberPermissionState(ACCESS_FINE_LOCATION)
    
    val nm = context.getSystemService(
        FirebaseMessagingService.NOTIFICATION_SERVICE
    ) as NotificationManager
    
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            openNotificationSettings(context)
            scope.launch {
                vm.setNotificationPermission(
                    nm.areNotificationsEnabled()
                )
            }
        }
    
    LaunchedEffect(Unit) {
        vm.setNotificationPermission(
            nm.areNotificationsEnabled()
        )
    }
    
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
                    launcher.launch(openNotificationSettings(context))
                }
            }
            
            override fun onComplete() {
                nav.navigateAbsolute("main/meetings")
            }
        })
}