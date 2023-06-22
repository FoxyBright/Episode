package ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.viewmodel.UserBsViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ANONYMOUS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.PROFILE

@Composable
fun OrganizerBs(
    vm: UserBsViewModel,
    userId: String,
    meetId: String,
    nav: NavHostController,
) {

    val backButton = nav.previousBackStackEntry != null
    val scope = rememberCoroutineScope()

    val avatarViewerSelectImage by vm.avatarViewerSelectImage.collectAsState()
    val avatarViewerImages by vm.avatarViewerImages.collectAsState()
    val avatarViewerState by vm.avatarViewerState.collectAsState()

    val hiddenViewerSelectImage by vm.hiddenViewerSelectImage.collectAsState()
    val hiddenViewerImages by vm.hiddenViewerImages.collectAsState()
    val hiddenViewerState by vm.hiddenViewerState.collectAsState()

    val meets by vm.userActualMeets.collectAsState()
    val isMyProfile by vm.isMyProfile.collectAsState()
    val observeState by vm.observe.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val meetState by vm.meetType.collectAsState()
    val profile by vm.profile.collectAsState()
    val organizerHiddenImages = vm.organizerHiddenImages.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        vm.setUser(userId)
        vm.setMeetType(meetId)
        vm.checkMyProfile(userId)
        vm.observeUser(profile.isWatching)
        vm.getUserActualMeets(userId)
    }

    LaunchedEffect(key1 = organizerHiddenImages.itemSnapshotList.items, block = {
        vm.setHiddenPhotoViewImages(organizerHiddenImages.itemSnapshotList.items)
        if(organizerHiddenImages.itemSnapshotList.items.isNotEmpty())
            vm.setHiddenPhotoViewSelected(organizerHiddenImages.itemSnapshotList.items[0])
    })

    val profileState = ProfileState(
        profile = profile,
        profileType = if (meetState == ANONYMOUS)
            ANONYMOUS_ORGANIZER else ORGANIZER,
        observeState = observeState,
        lockState = profile.hiddenAccess,
        isError = false,
        errorText = ""
    )
    Use<UserBsViewModel>(LoadingTrait) {
        OrganizerContent(
            state = UserState(
                profileState = profileState,
                currentMeetings = meets,
                backButton = backButton,
                menuState = menuState,
                isMyProfile = isMyProfile,
                photoViewState = avatarViewerState,
                viewerImages = avatarViewerImages,
                viewerSelectImage = avatarViewerSelectImage,
                hiddenPhotoViewState = hiddenViewerState,
                hiddenViewerImages = hiddenViewerImages,
                hiddenViewerSelectImage = hiddenViewerSelectImage,
                hiddenViewerMenuState = false,
            ), callback = object : OrganizerCallback {

                override fun profileImage(menuItem: Int) {
                    scope.launch {
                        vm.setPhotoViewSelected(profile.avatar)
                        vm.setPhotoViewImages(listOf(profile.avatar))
                        vm.setPhotoViewState(true)
                    }
                }

                override fun onPhotoViewDismiss(state: Boolean) {
                    scope.launch {
                        vm.setPhotoViewState(state)
                        vm.setHiddenPhotoViewState(state)
                    }
                }

                override fun onMenuItemSelect(point: Int, userId: String?) {
                    nav.navigate("REPORTS?id=$userId&type=$PROFILE")
                    this.onMenuDismiss(false)
                }

                override fun onObserveChange(state: Boolean) {
                    scope.launch { vm.observeUser(state, profile.id) }
                }

                override fun onMeetingClick(meet: MeetingModel) {
                    nav.navigate("MEET?meet=${meet.id}")
                }

                override fun onMenuDismiss(state: Boolean) {
                    scope.launch { vm.menuDismiss(state) }
                }

                override fun hiddenImages() {
                    scope.launch {
                        vm.setHiddenPhotoViewState(true)
                        /*    vm.setPhotoViewSelected(image)
                    vm.setPhotoViewImages(listOf(image))
                    vm.changePhotoViewState(true)*/
                        //vm.setPhotoViewState(true)
                    }

                }

                override fun onBack() {
                    nav.popBackStack()
                }
            }
        )
    }
}