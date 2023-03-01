package ru.rikmasters.gilty.bottomsheet.presentation.ui.participants

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.ParticipantsViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.meetBS.ParticipantsList
import ru.rikmasters.gilty.shared.common.meetBS.ParticipantsListCallback
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.meeting.UserModel


@Composable
fun ParticipantsBs(
    vm: ParticipantsViewModel,
    meetId: String,
    nav: NavHostController,
) {
    
    val memberList by vm.participants.collectAsState()
    val meeting by vm.meet.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getMeet(meetId)
        vm.getParticipants(meetId)
    }
    
    Use<ParticipantsViewModel>(LoadingTrait) {
        meeting?.let { meet ->
            ParticipantsList(
                meet, memberList, Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                object: ParticipantsListCallback {
                    
                    override fun onMemberClick(member: UserModel) {
                        if(meet.type != ANONYMOUS) nav.navigate(
                            "USER?user=${member.id}&meet=$meet"
                        )
                    }
                    
                    override fun onBack() {
                        nav.popBackStack()
                    }
                }
            )
        }
    }
}