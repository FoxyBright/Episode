package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.meets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.ObserveBsViewModel
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.meeting.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.meeting.OrganizerProfileState
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ANONYMOUS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun OrganizerBs(
    vm: ObserveBsViewModel,
    meetState: FullMeetingModel,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val meets by vm.userActualMeets.collectAsState()
    val observeState by vm.observe.collectAsState()
    val profile by vm.profile.collectAsState()
    
    val profileState = ProfileState(
        profile, if(meetState.type == ANONYMOUS)
            ANONYMOUS_ORGANIZER else ORGANIZER,
        observeState
    )
    
    OrganizerProfile(
        Modifier.fillMaxSize(),
        OrganizerProfileState(
            profileState, meets
        ), object: UserProfileCallback {
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch { vm.navigate(MeetNavigation.MEET, meet.id) }
            }
            
            override fun profileImage() {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigateAbsolute(
                        ("profile/avatar?type=2&image=${profile.avatar?.url}")
                    )
                }
            }
            
            override fun hiddenImages() {
            }
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onObserveChange(state: Boolean) {
                scope.launch { vm.observeUser(state, profile.id) }
            }
        }
    )
}