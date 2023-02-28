package ru.rikmasters.gilty.meetbs.presentation.ui.organizer

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.meetbs.viewmodel.components.OrganizerViewModel
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ANONYMOUS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun OrganizerBs(
    vm: OrganizerViewModel,
    userId: String,
    meetId: String,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val globalNav = get<NavState>()
    
    val meets by vm.userActualMeets.collectAsState()
    val observeState by vm.observe.collectAsState()
    val meetState by vm.meetType.collectAsState()
    val profile by vm.profile.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.setUser(userId)
        vm.setMeetType(meetId)
        vm.observeUser(profile.isWatching)
        vm.getUserActualMeets(userId)
    }
    
    val profileState = ProfileState(
        profile, if(meetState == ANONYMOUS)
            ANONYMOUS_ORGANIZER else ORGANIZER,
        observeState
    )
    
    OrganizerContent(
        Modifier, UserState(profileState, meets),
        object: OrganizerCallback {
            
            override fun profileImage() {
                scope.launch {
                    globalNav.navigateAbsolute(
                        ("profile/avatar?type=2&image=${profile.avatar?.url}")
                    )
                    asm.bottomSheet.collapse()
                }
            }
            
            override fun onObserveChange(state: Boolean) {
                scope.launch { vm.observeUser(state, profile.id) }
            }
            
            override fun onMeetingClick(meet: MeetingModel) {
                nav.navigate("MEET?meet=${meet.id}")
            }
            
            override fun hiddenImages() {
            }
            
            override fun onBack() {
                nav.popBackStack()
            }
        }
    )
}