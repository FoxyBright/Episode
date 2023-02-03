package ru.rikmasters.gilty.mainscreen.presentation.ui.respond

import androidx.compose.runtime.*
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.viewmodels.RespondsViewModel
import ru.rikmasters.gilty.shared.common.meetBS.MeetRespondContent

@Composable
fun MeetRespondScreen(
    vm: RespondsViewModel,
    meetId: String,
) {
    
    val nav = get<NavState>()
    
    val meeting by vm.meeting.collectAsState()
    
    LaunchedEffect(Unit) { vm.getMeet(meetId) }
    
    meeting?.let { meet ->
        MeetRespondContent(
            meet.organizer.avatar,
            meet.category,
            meet.withoutResponds
        ) { nav.navigationBack() }
    }
}