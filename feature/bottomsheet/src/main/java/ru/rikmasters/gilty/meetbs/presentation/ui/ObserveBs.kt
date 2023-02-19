package ru.rikmasters.gilty.meetbs.presentation.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.MEET
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.USER
import ru.rikmasters.gilty.meetbs.presentation.ui.meet.MeetingBs
import ru.rikmasters.gilty.meetbs.presentation.ui.organizer.UserBs
import ru.rikmasters.gilty.meetbs.presentation.ui.participants.ParticipantsBs
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.MeetingViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.ParticipantsViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.UserViewModel

enum class ObserveType { USER, MEET, PARTICIPANTS, REPORTS, RESPONDS, OBSERVERS }

@Composable
fun Observe(
    vm: ObserveViewModel,
    type: ObserveType,
    meetId: String,
    userId: String,
) {
    
    val nav = rememberNavController()
    
    val meeting by vm.meeting.collectAsState()
    val user by vm.user.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.setMeeting(meetId)
        vm.setUser(userId)
        when(type) {
            USER -> nav.navigate("USER")
            MEET -> nav.navigate("MEET")
            else -> {}
        }
    }
    
    val old = Pair(userId, meetId)
    
    NavHost(nav, "MEET") {
        composable("MEET") {
            Connector<MeetingViewModel>(vm.scope) {
                meeting?.let { m -> MeetingBs(it, old, m, nav) }
            }
        }
        composable("USER") {
            Connector<UserViewModel>(vm.scope) {
                meeting?.let { m ->
                    user?.let { u ->
                        UserBs(it, old, m, u, nav)
                    }
                }
            }
        }
        composable("PARTICIPANTS") {
            Connector<ParticipantsViewModel>(vm.scope) {
                meeting?.let { m -> ParticipantsBs(it, old, m, nav) }
            }
        }
        composable("RESPONDS") {
            nav.popBackStack() // TODO пока экрана нет
        }
    }
}