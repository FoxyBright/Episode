package ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting.MeetingBs
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.OrganizerBsViewModel
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import kotlin.coroutines.CoroutineContext

@Composable
@Suppress("unused")
fun OrganizerBs(
    vm: OrganizerBsViewModel,
    meet: MeetingModel,
    scope: CoroutineScope,
    coroutineContext: CoroutineContext,
) {
    
    val asm = get<AppStateModel>()
    
    val observeState by vm.observe.collectAsState()
    val meets by vm.meets.collectAsState()
    val profile by vm.profile.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.drawOrganizer()
    }
    
    val profileState = ProfileState(
        profile,
        profileType = ORGANIZER,
        observeState = observeState,
    )
    
    OrganizerProfile(
        Modifier, OrganizerProfileState(
            profileState, meets
        ), object: UserProfileCallback {
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch(coroutineContext) {
                    asm.bottomSheet.expand {
                        Connector<MeetingBsViewModel>(vm.scope) {
                            MeetingBs(it, meet.id)
                        }
                    }
                }
            }
            
            override fun onBack() {
                scope.launch(coroutineContext) {
                    asm.bottomSheet.expand {
                        Connector<MeetingBsViewModel>(vm.scope) {
                            MeetingBs(it, meet.id)
                        }
                    }
                }
            }
            
            override fun onObserveChange(state: Boolean) {
                scope.launch { vm.observeUser(state) }
            }
        }
    )
}