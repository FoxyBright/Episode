package ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.ORGANIZER
import ru.rikmasters.gilty.shared.common.meetBS.ParticipantsList
import ru.rikmasters.gilty.shared.common.meetBS.ParticipantsListCallback
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun ParticipantsBs(
    vm: MeetingBsViewModel,
    meet: FullMeetingModel,
) {
    
    val scope = rememberCoroutineScope()
    val memberList by vm.memberList.collectAsState()
    
    ParticipantsList(
        meet, memberList, Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        object: ParticipantsListCallback {
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onMemberClick(member: UserModel) {
                if(meet.type != MeetType.ANONYMOUS) scope.launch {
                    vm.navigate(ORGANIZER, member.id!!)
                }
            }
        }
    )
}