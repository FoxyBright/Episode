package ru.rikmasters.gilty.profile.presentation.ui.user

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.internetCheck
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting.MyMeetingScreen
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.observers.ObserversBs
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.responds.RespondsBs
import ru.rikmasters.gilty.profile.presentation.ui.user.gallerey.HiddenBsScreen
import ru.rikmasters.gilty.profile.viewmodel.*
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun UserProfileScreen(vm: UserProfileViewModel) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    val navBar by vm.navBar.collectAsState()
    val alert by vm.complaintsAlert.collectAsState()
    
    val menuState by vm.menu.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val meets by vm.meets.collectAsState()
    val meetsHistory by vm.meetsHistory.collectAsState()
    val history by vm.history.collectAsState()
    
    val profile by vm.profile.collectAsState()
    val username by vm.username.collectAsState()
    val avatar by vm.avatar.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val description by vm.description.collectAsState()
    val rating by vm.rating.collectAsState()
    val emoji by vm.emoji.collectAsState()
    val observers by vm.observers.collectAsState()
    val observed by vm.observed.collectAsState()
    val occupied by vm.occupied.collectAsState()
    
    val errorState by vm.errorConnection.collectAsState()
    
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if(internetCheck(context)) {
            vm.errorConnection(false)
            vm.setUserDate()
        } else vm.errorConnection(true)
    }
    
    val state = UserProfileState(
        ProfileState(
            name = username,
            profilePhoto = avatar,
            hiddenPhoto = hidden,
            description = description,
            rating = rating,
            observers = observers,
            observed = observed,
            emoji = emoji,
            profileType = USERPROFILE,
            enabled = true,
            occupiedName = occupied
        ), meets, meetsHistory,
        lastRespond,
        history, navBar, alert,
        menuState, listState, errorState
    )
    
    UserProfile(state, Modifier,
        object: UserProfileCallback {
            
            override fun menu(state: Boolean) {
                nav.navigate("settings")
            }
            
            override fun onNameChange(text: String) {
                scope.launch { vm.changeUsername(text) }
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.changeDescription(text) }
            }
            
            override fun closeAlert() {
                scope.launch { vm.setComplaintAlertState(false) }
            }
            
            override fun profileImage() {
                scope.launch { vm.menuDispose(true) }
            }
            
            override fun onMenuClick(it: Boolean) {
                scope.launch { vm.menuDispose(it) }
            }
            
            override fun onHistoryShow() {
                scope.launch {
                    vm.showHistory()
                    listState.animateScrollToItem(5)
                }
            }
            
            override fun onNavBarSelect(point: Int) {
                scope.launch {
                    nav.navigateAbsolute(
                        vm.navBarNavigate(point)
                    )
                }
            }
            
            override fun onMenuItemClick(point: Int) {
                when(point) {
                    0 -> nav.navigate("avatar?image=$avatar")
                    else -> nav.navigate("gallery?multi=false")
                }
            }
            
            override fun onHistoryClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        profile?.let {
                            MyMeetingScreen(
                                it, meet,
                                asm, scope
                            )
                        }
                    }
                }
            }
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        profile?.let {
                            MyMeetingScreen(it, meet, asm, scope)
                        }
                    }
                }
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
            
            override fun onObserveClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<ObserverBsViewModel>(vm.scope) {
                            ObserversBs(it, username)
                        }
                    }
                }
            }
            
            override fun onRespondsClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<RespondsBsViewModel>(vm.scope) {
                            RespondsBs(it)
                        }
                    }
                }
            }
        }
    )
}