package ru.rikmasters.gilty.profile.presentation.ui.settings

import android.app.NotificationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.age.AgeBs
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.icons.IconsBs
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.information.InformationBs
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.selector.GenderBs
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.selector.OrientationsBs
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.*
import ru.rikmasters.gilty.shared.common.extentions.Permissions.Companion.openNotificationSettings


@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val interests by vm.selected.collectAsState()
    val orientation by vm.orientation.collectAsState()
    val orientationList by vm.orientations.collectAsState()
    val notification by vm.notifications.collectAsState()
    val deleteAlert by vm.deleteAlert.collectAsState()
    val exitAlert by vm.exitAlert.collectAsState()
    val gender by vm.gender.collectAsState()
    val phone by vm.phone.collectAsState()
    val age by vm.age.collectAsState()
    var sleep by remember{ mutableStateOf(false) } // TODO Костыль для обновления баблов

    val nm = context.getSystemService(
        FirebaseMessagingService.NOTIFICATION_SERVICE
    ) as NotificationManager
    
    fun checkNotification() {
        scope.launch { vm.setNotification(nm.areNotificationsEnabled()) }
    }
    
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            openNotificationSettings(context)
            checkNotification()
        }
    
    LaunchedEffect(Unit) {
        checkNotification()
        vm.getUserData()
        vm.getOrientations()
        vm.getInterest()
        sleep = true
    }
    
    Use<SettingsViewModel>(LoadingTrait) {
        SettingsContent(
            SettingsState(
                gender, age, orientation,
                phone, notification,
                exitAlert, deleteAlert, interests, sleep
            ), Modifier, object: SettingsCallback {
                
                override fun onGenderClick() {
                    vm.scope.openBS<GenderBsViewModel>(scope) {
                        GenderBs(it)
                    }
                }
                
                override fun onOrientationClick() {
                    vm.scope.openBS<OrientationBsViewModel>(scope) {
                        OrientationsBs(
                            vm = it,
                            orientation = orientation,
                            orientationList = orientationList
                        )
                    }
                }
                
                override fun onAgeClick() {
                    vm.scope.openBS<AgeBsViewModel>(scope) {
                        AgeBs(it)
                    }
                }
                
                override fun onAboutAppClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            InformationBs()
                        }
                    }
                }
                
                override fun onIconAppClick() {
                    vm.scope.openBS<IconsBsViewModel>(scope) {
                        IconsBs(it)
                    }
                }
                
                override fun onExitSuccess() {
                    scope.launch {
                        vm.exitAlertDismiss(false)
                        vm.logout()
                        nav.clearStackNavigation("login")
                        nav.clearNavigationOptions()
                    }
                }
                
                override fun onDeleteSuccess() {
                    scope.launch {
                        vm.deleteAlertDismiss(false)
                        vm.deleteAccount()
                        nav.clearStackNavigation("login")
                    }
                }
                
                override fun onNotificationChange(it: Boolean) {
                    launcher.launch(openNotificationSettings(context))
                }
                
                override fun onDeleteDismiss() {
                    scope.launch { vm.deleteAlertDismiss(false) }
                }
                
                override fun onDelete() {
                    scope.launch { vm.deleteAlertDismiss(true) }
                }
                
                override fun onExitDismiss() {
                    scope.launch { vm.exitAlertDismiss(false) }
                }
                
                override fun onExit() {
                    scope.launch { vm.exitAlertDismiss(true) }
                }
                
                override fun onPhoneClick() {
                    // клик по телефону
                }
                
                override fun editCategories() {
                    nav.navigate("categories")
                }
                
                override fun onBack() {
                    nav.navigationBack()
                }
            }
        )
    }
}