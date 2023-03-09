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
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.Use
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
    
    val orientation by vm.orientation.collectAsState()
    val orientationList by vm.orientations.collectAsState()
    val notification by vm.notifications.collectAsState()
    val deleteAlert by vm.deleteAlert.collectAsState()
    val exitAlert by vm.exitAlert.collectAsState()
    val gender by vm.gender.collectAsState()
    val phone by vm.phone.collectAsState()
    val age by vm.age.collectAsState()
    
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
    }
    
    Use<SettingsViewModel>(LoadingTrait) {
        SettingsContent(
            SettingsState(
                gender, age, orientation,
                phone, notification,
                exitAlert, deleteAlert
            ), Modifier, object: SettingsCallback {
                
                override fun onGenderClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<GenderBsViewModel>(vm.scope) {
                                GenderBs(it)
                            }
                        }
                    }
                }
                
                override fun onOrientationClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<OrientationBsViewModel>(vm.scope) {
                                OrientationsBs(
                                    it, orientation,
                                    orientationList
                                )
                            }
                        }
                    }
                }
                
                override fun onAgeClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<AgeBsViewModel>(vm.scope) {
                                AgeBs(it)
                            }
                        }
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
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<IconsBsViewModel>(vm.scope) {
                                IconsBs(it)
                            }
                        }
                    }
                }
                
                override fun onExitSuccess() {
                    scope.launch {
                        vm.exitAlertDismiss(false)
                        vm.logout()
                        nav.navigateAbsolute("login")
                    }
                }
                
                override fun onDeleteSuccess() {
                    scope.launch {
                        vm.deleteAlertDismiss(false)
                        vm.deleteAccount()
                        nav.navigateAbsolute("login")
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
                    // TODO функционал пока не существует
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