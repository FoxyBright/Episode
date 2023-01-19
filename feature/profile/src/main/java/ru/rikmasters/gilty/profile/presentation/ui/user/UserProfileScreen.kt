package ru.rikmasters.gilty.profile.presentation.ui.user

import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting.MyMeetingScreen
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.observers.ObserversBs
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.responds.RespondsList
import ru.rikmasters.gilty.profile.presentation.ui.user.gallerey.HiddenBsScreen
import ru.rikmasters.gilty.profile.viewmodel.HiddenBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.ObserverBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel

@Composable
fun UserProfileScreen(vm: UserProfileViewModel) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    
    val navBar by vm.navBar.collectAsState()
    val alert by vm.complaintsAlert.collectAsState()
    
    val menuState by vm.menu.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val meets by vm.meets.collectAsState()
    val history by vm.history.collectAsState()
    
    val respondsSelectTab by vm.respondsSelectTab.collectAsState()
    val observeGroupStates by vm.observeGroupStates.collectAsState()
    
    val profile by vm.profile.collectAsState()
    val profileState by vm.profileState.collectAsState()
    
    LaunchedEffect(Unit) { vm.drawProfile() }
    
    val respondsList =
        remember {
            mutableStateListOf(
                DemoReceivedRespondsModel,
                DemoReceivedRespondsModel,
                DemoSendRespondsModel,
                DemoSendRespondsModel,
                DemoSendRespondsModel
            )
        }
    
    val pairRespondsList =
        remember { mutableStateOf(Pair(DemoMeetingModel, respondsList)) }
    
    val state = UserProfileState(
        profileState, meets, meets,
        lastRespond, (respondsList.size),
        history, navBar, alert,
        menuState, listState
    )
    
    UserProfile(state, Modifier, object: UserProfileCallback {
        
        override fun menu(state: Boolean) {
            nav.navigate("settings")
        }
        
        override fun onNameChange(text: String) {
            scope.launch { vm.changeUsername(text) }
        }
        
        override fun onDescriptionChange(text: String) {
            scope.launch { vm.changeDescription(text) }
        }
        
        override fun onObserveClick() {
            scope.launch {
                asm.bottomSheet.expand {
                    Connector<ObserverBsViewModel>(vm.scope) {
                        ObserversBs(it, profileState.name)
                    }
                }
            }
        }
        
        override fun closeAlert() {
            scope.launch {
                vm.setComplaintAlertState(false)
            }
        }
        
        override fun onNavBarSelect(point: Int) {
            scope.launch {
                nav.navigateAbsolute(
                    vm.navBarNavigate(point)
                )
            }
        }
        
        override fun onMenuClick(it: Boolean) {
            scope.launch { vm.menuDispose(it) }
        }
        
        override fun onMenuItemClick(point: Int) {
            val image = profileState.profilePhoto
            when(point) {
                0 -> nav.navigate("avatar?image=$image")
                
                else -> nav.navigate(
                    "gallery?multi=false"
                )
            }
        }
        
        override fun onHistoryClick(meet: MeetingModel) {
            scope.launch {
                asm.bottomSheet.expand {
                    MyMeetingScreen(
                        profile, meet,
                        asm, scope
                    )
                }
            }
        }
        
        override fun onMeetingClick(meet: MeetingModel) {
            scope.launch {
                asm.bottomSheet.expand {
                    MyMeetingScreen(
                        profile, meet,
                        asm, scope
                    )
                }
            }
        }
        
        override fun profileImage() {
            scope.launch { vm.menuDispose(true) }
        }
        
        override fun hiddenImages() {
            scope.launch {
                asm.bottomSheet.expand {
                    Connector<HiddenBsViewModel>(vm.scope) {
                        HiddenBsScreen(it)
                    }
                }
            }
        }
        
        override fun onHistoryShow() {
            scope.launch {
                vm.showHistory()
                listState.animateScrollToItem(5)
            }
        }
        
        override fun onRespondsClick() {
            scope.launch {
                asm.bottomSheet.expand {
                    RespondsList(
                        listOf(pairRespondsList.value),
                        respondsSelectTab, observeGroupStates,
                        Modifier, object: RespondCallback {
                            override fun onCancelClick(respond: RespondModel) {
                                respondsList.remove(respond)
                                Toast.makeText(
                                    context,
                                    "Вы отказались от встречи",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            
                            override fun onRespondClick(meet: MeetingModel) {
                                Toast.makeText(
                                    context,
                                    "Вы нажали на встречу",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            
                            override fun onAcceptClick(respond: RespondModel) {
                                respondsList.remove(respond)
                                Toast.makeText(
                                    context,
                                    "Вы приняли встречу",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            
                            override fun onImageClick(image: HiddenPhotoModel) {
                                Toast.makeText(
                                    context,
                                    "Фото смотреть нельзя",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            
                            override fun onTabChange(tab: Int) {
                                scope.launch {
                                    vm.changeObserveGroupStates(tab)
                                }
                            }
                            
                            override fun onBack() {
                                scope.launch { asm.bottomSheet.collapse() }
                            }
                            
                            override fun onArrowClick(index: Int) {
                                scope.launch { vm.changeRespondsTab(index) }
                            }
                        })
                }
            }
        }
    }
    )
}