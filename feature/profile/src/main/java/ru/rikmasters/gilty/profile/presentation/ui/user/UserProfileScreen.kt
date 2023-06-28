package ru.rikmasters.gilty.profile.presentation.ui.user

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.OBSERVERS
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.RESPONDS
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.gallery.checkStoragePermission
import ru.rikmasters.gilty.gallery.permissionState
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
fun UserProfileScreen(vm: UserProfileViewModel, update: Boolean, closePopUp: Boolean) {
    val listState = rememberLazyListScrollState("profile")
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()

    val meetsHistory = vm.historyMeetsTest.collectAsLazyPagingItems()
    val meets = vm.meetsTest.collectAsLazyPagingItems()
    val unreadNotification by vm.unreadNotification.collectAsState()
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val viewerMenuState by vm.viewerMenuState.collectAsState()
    val unreadMessages by vm.unreadMessages.collectAsState()
    val photoAlertState by vm.photoAlertState.collectAsState()
    val photoViewState by vm.photoViewState.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val activeAlbumId by vm.activeAlbumId.collectAsState()
    val history by vm.historyState.collectAsState()
    val profile by vm.profile.collectAsState()
    val username by vm.username.collectAsState()
    val description by vm.description.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val alert by vm.alert.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    val navBar = remember(
        unreadNotification, unreadMessages
    ) {
        mutableListOf(
            INACTIVE, unreadNotification,
            INACTIVE, unreadMessages, ACTIVE
        )
    }

    val errorText by remember(
        errorMessage
    ) {
        mutableStateOf(errorMessage)
    }

    val back = colorScheme.primaryContainer

    LaunchedEffect(Unit) {
        asm.systemUi.setNavigationBarColor(back)

        if(closePopUp){
            vm.changePhotoViewState(false)
            vm.setViewerMenuState(false)
        }

        vm.setUserDate(update)
        vm.getUnread()
        val pref = context
            .getSharedPreferences("sharedPref", MODE_PRIVATE)
        pref.getInt("unread_messages", 0).let {
            vm.setUnreadMessages(it > 0)
        }
        pref.getInt("unread_notification", 0).let {
            vm.setUnreadNotifications(it > 0)
        }
        // TODO для DeepLink при нажатии на пуш с блокированным фото пользователя
        //        profile?.avatar?.blockedAt?.let{
        //            vm.photoAlertDismiss(true)
        //        }
    }

    val callback = object : UserProfileCallback {

        override fun updateProfile() {

        }

        override fun onProfileImageRefresh() {
            scope.launch { vm.updateUserData() }
        }

        override fun onAlbumClick(id: Int) {
            scope.launch { nav.navigate("album?id=${id}") }
        }

        override fun onAlbumLongClick(id: Int?) {
            scope.launch { vm.changeActiveAlbumId(id) }
        }

        override fun onPhotoViewDismiss(state: Boolean) {
            scope.launch { vm.changePhotoViewState(state) }
        }

        override fun onHistoryClick(meet: MeetingModel) {
            scope.launch {
                asm.bottomSheet.expand {
                    BottomSheet(
                        scope = vm.scope,
                        type = MEET,
                        meetId = meet.id
                    )
                }
            }
        }

        override fun onMeetingClick(meet: MeetingModel) {
            scope.launch {
                asm.bottomSheet.expand {
                    BottomSheet(
                        scope = vm.scope,
                        type = MEET,
                        meetId = meet.id
                    )
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
                        scope = vm.scope,
                        type = OBSERVERS,
                        user = profile?.map()
                    )
                }
            }
        }

        override fun onRespondsClick() {
            scope.launch {
                asm.bottomSheet.expand {
                    BottomSheet(
                        scope = vm.scope,
                        type = RESPONDS,
                        fullResponds = true
                    )
                }
            }
        }

        override fun onHistoryShow() {
            scope.launch {
                vm.showHistory()
            }
        }

        override fun onNavBarSelect(point: Int) {
            if (point == 4) return
            scope.launch {
                nav.navigateAbsolute(
                    vm.navBarNavigate(point)
                )
            }
        }

        override fun onMenuItemClick(point: Int) {
            scope.launch {
                vm.menuDispose(false)
                when (point) {
                    0 -> {
                        vm.setPhotoViewSelected(profile?.avatar)
                        vm.setPhotoViewImages(listOf(profile?.avatar))
                        vm.changePhotoViewState(true)
                    }

                    else -> context.checkStoragePermission(
                        storagePermissions, scope, asm,
                    ) { nav.navigate("gallery?multi=false") }
                }
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

        override fun profileImage(menuItem: Int) {
            scope.launch { onMenuItemClick(menuItem) }
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

        override fun onPhotoViewChangeMenuState(state: Boolean) {
            scope.launch { vm.setViewerMenuState(state) }
        }
    }

    ProfileContent(
        modifier = Modifier.systemBarsPadding(),
        state = UserProfileState(
            profileState = ProfileState(
                profile = profile?.copy(
                    username = username,
                    aboutMe = description
                ),
                profileType = USERPROFILE,
                observeState = false,
                errorText = errorText,
                activeAlbumId = activeAlbumId,
                isAlbumVisible = true,
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
            viewerMenuState = viewerMenuState,
            smthError = false,
        ), callback = callback
    )
}
