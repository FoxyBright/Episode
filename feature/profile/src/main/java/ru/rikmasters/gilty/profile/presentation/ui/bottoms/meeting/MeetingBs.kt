package ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer.OrganizerBs
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants.ParticipantsBs
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.OrganizerBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ParticipantsBsViewModel
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsContent
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsState
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

@Composable
fun MeetingBs(
    vm: MeetingBsViewModel,
    meet: MeetingModel,
) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val menu by vm.menu.collectAsState()
    val memberList by vm.memberList.collectAsState()
    val distance by vm.distance.collectAsState()
    
    val shared = true
    val userInMeet = true
    val buttonState = true
    
    LaunchedEffect(Unit) {
        vm.drawMeet(meet)
    }
    
    MeetingBsContent(
        MeetingBsState(
            menu, meet, memberList, distance,
            userInMeet, buttonState, shared
        ), Modifier.padding(16.dp),
        object: MeetingBsCallback {
            
            override fun closeAlert() {
                scope.launch { vm.alertDismiss(false) }
            }
            
            override fun onKebabClick(state: Boolean) {
                scope.launch { vm.menuDismiss(state) }
            }
            
            override fun onMenuItemClick(index: Int) {
                scope.launch {
                    vm.menuDismiss(false)
                    asm.bottomSheet.expand {
                        ComplainsContent(DemoMeetingModel) {
                            scope.launch {
                                asm.bottomSheet.collapse()
                                vm.alertDismiss(true)
                            }
                        }
                    }
                }
            }
            
            override fun onAllMembersClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<ParticipantsBsViewModel>(vm.scope) {
                            ParticipantsBs(it, meet)
                        }
                    }
                }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<OrganizerBsViewModel>(vm.scope) {
                            OrganizerBs(it, meet, scope, scope.coroutineContext)
                        }
                    }
                }
            }
            
            override fun onBottomButtonClick(point: Int) {
                scope.launch {
                    when(point) {
                        1 -> vm.outOfMeet()
                        2 -> vm.sharedMeet()
                        3 -> vm.cancelOfMeet()
                    }
                }
            }
        }
    )
}