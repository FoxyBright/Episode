package ru.rikmasters.gilty.profile.presentation.ui.user

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.OBSERVERS
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.RESPONDS
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.data.source.SharedPrefListener.Companion.listenPreference
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun UserProfileScreen(vm: UserProfileViewModel) {
    
    val listState = rememberLazyListScrollState("profile")
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val meetsHistory = vm.historyMeetsTest.collectAsLazyPagingItems()
    val meets = vm.meetsTest.collectAsLazyPagingItems()
    val photoAlertState by vm.photoAlertState.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val profile by vm.profile.collectAsState()
    val username by vm.username.collectAsState()
    val occupied by vm.occupied.collectAsState()
    val history by vm.history.collectAsState()
    val menuState by vm.menu.collectAsState()
    val alert by vm.alert.collectAsState()
    
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val photoViewState by vm.photoViewState.collectAsState()
    
    val unreadMessages by vm.unreadMessages.collectAsState()
    val navBar = remember {
        mutableListOf(
            INACTIVE, INACTIVE, INACTIVE,
            unreadMessages, ACTIVE
        )
    }
    
    val regexError = username.contains(Regex("[^A-Za-z\\d]"))
    val shortUserNameError = username.length in 1 until 4
    val longUserNameError = username.length > 20
    
    val regexString =
        stringResource(R.string.profile_username_error_regex)
    val longUsernameString =
        stringResource(R.string.profile_long_username)
    val shortUsernameString =
        stringResource(R.string.profile_short_username)
    val usernameOccupiedString =
        stringResource(R.string.profile_user_name_is_occupied)
    
    val errorText by remember(username, occupied) {
        mutableStateOf(
            when {
                regexError -> regexString
                longUserNameError -> longUsernameString
                shortUserNameError -> shortUsernameString
                occupied -> usernameOccupiedString
                else -> ""
            }
        )
    }
    
    LaunchedEffect(Unit) {
        vm.setUserDate()
        context.listenPreference("unread_messages", 0) {
            scope.launch { vm.setUnreadMessages(it > 0) }
        }
        // TODO для DeepLink при нажатии на пуш с блокированным фото пользователя
        //        profile?.avatar?.blockedAt?.let{
        //            vm.photoAlertDismiss(true)
        //        }
    }
    
    ProfileContent(
        UserProfileState(
            profileState = ProfileState(
                profile = profile?.copy(
                    username = username
                ),
                profileType = USERPROFILE,
                observeState = false,
                errorText = errorText
            ),
            currentMeetings = meets,
            meetingsHistory = meetsHistory,
            lastRespond = lastRespond,
            historyState = history,
            stateList = navBar,
            alert = alert,
            menuState = menuState,
            listState = listState,
            photoAlertState = photoAlertState,
            photoViewState = photoViewState,
            viewerImages = viewerImages,
            viewerSelectImage = viewerSelectImage,
        ), Modifier, object: UserProfileCallback {
            
            override fun onPhotoViewDismiss(state: Boolean) {
                scope.launch { vm.changePhotoViewState(state) }
            }
            
            override fun onHistoryClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, MEET, meet.id)
                    }
                }
            }
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, MEET, meet.id)
                    }
                }
            }
            
            override fun hiddenImages() {
                scope.launch {
                    nav.navigate("hidden")
                }
            }
            
            override fun onObserveClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(
                            vm.scope, OBSERVERS,
                            username = profile?.username
                        )
                    }
                }
            }
            
            override fun onRespondsClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(
                            vm.scope,
                            RESPONDS,
                            fullResponds = true
                        )
                    }
                }
            }
            
            override fun onHistoryShow() {
                scope.launch {
                    vm.showHistory()
                    listState.animateScrollToItem(5)
                }
            }
            
            override fun onNavBarSelect(point: Int) {
                if(point == 4) return
                scope.launch {
                    nav.navigateAbsolute(
                        vm.navBarNavigate(point)
                    )
                }
            }
            
            override fun onMenuItemClick(point: Int) {
                when(point) {
                    0 -> {
                        scope.launch {
                            vm.setPhotoViewSelected(profile?.avatar)
                            vm.setPhotoViewImages(listOf(profile?.avatar))
                            vm.changePhotoViewState(true)
                        }
                    }
                    else -> nav.navigate("gallery?multi=false")
                }
            }
            
            override fun closePhotoAlert() {
                scope.launch { vm.photoAlertDismiss(false) }
            }
            
            override fun closeAlert() {
                scope.launch { vm.alertDismiss(false) }
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.changeDescription(text) }
            }
            
            override fun profileImage() {
                scope.launch { vm.menuDispose(true) }
            }
            
            override fun onNameChange(text: String) {
                scope.launch { vm.changeUsername(text) }
            }
            
            override fun onSaveUserName() {
                scope.launch { vm.updateUsername() }
            }
            
            override fun onSaveDescription() {
                scope.launch { vm.updateDescription() }
            }
            
            override fun onMenuClick(it: Boolean) {
                scope.launch { vm.menuDispose(it) }
            }
            
            override fun menu(state: Boolean) {
                nav.navigate("settings")
            }
        }
    )
}
