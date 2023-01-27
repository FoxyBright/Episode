package ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting.MeetingBs
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ParticipantsBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

@Composable
fun ParticipantsBs(
    vm: ParticipantsBsViewModel,
    meet: FullMeetingModel,
) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val memberList by vm.memberList.collectAsState()
    
    ParticipantsList(
        meet, memberList, Modifier,
        object: ParticipantsListCallback {
            override fun onBack() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<MeetingBsViewModel> {
                            MeetingBs(it, meet.id)
                        }
                    }
                }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        // Organizer(user, meet, asm, scope)
                    }
                }
            }
        }
    )
}