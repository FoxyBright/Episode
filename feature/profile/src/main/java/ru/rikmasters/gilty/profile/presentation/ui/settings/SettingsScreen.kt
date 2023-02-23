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
fun SettingsScreen(
    vm: SettingsViewModel,
    userGender: String,
    userAge: String,
    userOrientation: Pair<String, String>,
    userPhone: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val deleteAlert by vm.deleteAlert.collectAsState()
    val exitAlert by vm.exitAlert.collectAsState()
    
    val orientation by vm.orientation.collectAsState()
    val orientationList by vm.orientations.collectAsState()
    val notification by vm.notifications.collectAsState()
    val gender by vm.gender.collectAsState()
    val phone by vm.phone.collectAsState()
    val age by vm.age.collectAsState()
    
    val nm = context.getSystemService(
        FirebaseMessagingService.NOTIFICATION_SERVICE
    ) as NotificationManager
    
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            openNotificationSettings(context)
            scope.launch {
                vm.setNotification(
                    nm.areNotificationsEnabled()
                )
            }
        }
    
    LaunchedEffect(Unit) {
        vm.setNotification(nm.areNotificationsEnabled())
        vm.getUserData(
            userGender, userAge,
            userOrientation, userPhone
        )
        vm.getOrientations()
    }
    
    val state = SettingsState(
        gender, age, orientation,
        phone, notification,
        exitAlert, deleteAlert
    )
    
    Use<SettingsViewModel>(LoadingTrait) {
        SettingsContent(
            state, Modifier, object: SettingsCallback {
                
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
                            Connector<InformationBsViewModel>(vm.scope) {
                                InformationBs(it)
                            }
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
                
                override fun onExit() {
                    scope.launch { vm.exitAlertDismiss(true) }
                }
                
                override fun onDelete() {
                    scope.launch { vm.deleteAlertDismiss(true) }
                }
                
                override fun onExitDismiss() {
                    scope.launch { vm.exitAlertDismiss(false) }
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
                
                override fun onPhoneClick() {
                    scope.launch { vm.changePhone() }
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