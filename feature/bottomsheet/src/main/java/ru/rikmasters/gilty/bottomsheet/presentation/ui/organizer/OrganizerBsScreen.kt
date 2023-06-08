package ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.viewmodel.UserBsViewModel
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

    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val meets by vm.userActualMeets.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    val isMyProfile by vm.isMyProfile.collectAsState()
    val observeState by vm.observe.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val meetState by vm.meetType.collectAsState()
    val profile by vm.profile.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.setUser(userId)
        vm.setMeetType(meetId)
        vm.checkMyProfile(userId)
        vm.observeUser(profile.isWatching)
        vm.getUserActualMeets(userId)
    }

    val profileState = ProfileState(
        profile = profile,
        profileType = if (meetState == ANONYMOUS)
            ANONYMOUS_ORGANIZER else ORGANIZER,
        observeState = observeState,
        lockState = profile.hiddenAccess,
        isError = false,
        errorText = ""
    )

    
    
    OrganizerContent(
        state = UserState(
            profileState = profileState,
            currentMeetings = meets,
            backButton = backButton,
            menuState = menuState,
            isMyProfile = isMyProfile,
            photoViewState = photoViewState,
            viewerImages = viewerImages,
            viewerSelectImage = viewerSelectImage,
        ), callback = object : OrganizerCallback {

            override fun profileImage(menuItem: Int) {
                scope.launch {
                    vm.setPhotoViewSelected(profile.avatar)
                    vm.setPhotoViewImages(listOf(profile.avatar))
                    vm.changePhotoViewState(true)
                }
            }

            override fun onPhotoViewDismiss(state: Boolean) {
                scope.launch { vm.changePhotoViewState(state) }
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
            }

            override fun onBack() {
                nav.popBackStack()
            }
        }
    )
}