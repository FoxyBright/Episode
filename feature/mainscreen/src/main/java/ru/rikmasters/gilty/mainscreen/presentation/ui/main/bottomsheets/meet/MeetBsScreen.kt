package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.meet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.MeetBsViewModel
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.*
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsContent
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsState
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

@Composable
fun MeetBs(
    vm: MeetBsViewModel,
    meetId: String,
    detailed: Boolean,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val memberList by vm.memberList.collectAsState()
    val stack by vm.stack.collectAsState()
    val meeting by vm.meet.collectAsState()
    val navigator by vm.navigator.collectAsState()
    val distance by vm.distance.collectAsState()
    val comment by vm.comment.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val menu by vm.menu.collectAsState()
    
    val details = if(detailed) Pair(comment, hidden) else null
    val buttonState = meeting?.memberState == IS_ORGANIZER
    val backBut = stack.size > 1
    
    LaunchedEffect(Unit) {
        vm.clearStack()
        vm.navigate(MEET, meetId)
        vm.clearComment()
        vm.changeHidden(false)
    }
    
    meeting?.let { meet ->
        navigator?.let { screen ->
            when(screen.navigation) {
                PARTICIPANTS -> ParticipantsBs(vm, meet)
                COMPLAINTS -> ComplaintsBs(vm, meetId)
                ORGANIZER -> OrganizerBs(vm, meet)
                MEET -> MeetingBsContent(
                    MeetingBsState(
                        menu, meet, memberList, distance,
                        buttonState, details, backBut
                    ), Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    object: MeetingBsCallback {
                        
                        override fun onMenuItemClick(index: Int, meetId: String) {
                            scope.launch {
                                if(index == 0) vm.sharedMeet(meetId)
                                if(index == 3) vm.navigate(COMPLAINTS)
                            }
                        }
                        
                        override fun onAvatarClick(organizerId: String) {
                            scope.launch {
                                if(meet.type != MeetType.ANONYMOUS)
                                    vm.getUserActualMeets(organizerId)
                                vm.navigate(ORGANIZER, organizerId)
                            }
                        }
                        
                        override fun onMemberClick(member: MemberModel) {
                            scope.launch {
                                if(meet.type != MeetType.ANONYMOUS)
                                    vm.getUserActualMeets(member.id)
                                vm.navigate(ORGANIZER, member.id)
                            }
                        }
                        
                        override fun onRespond(meetId: String) {
                            scope.launch {
                                vm.respondForMeet(meetId)
                                nav.navigate("reaction?meetId=$meetId")
                                asm.bottomSheet.collapse()
                            }
                        }
                        
                        override fun onMeetPlaceClick(meetLocation: LocationModel?) {
                            scope.launch { vm.meetPlaceClick(meetLocation) }
                        }
                        
                        override fun onAllMembersClick(meetId: String) {
                            scope.launch { vm.navigate(PARTICIPANTS, meetId) }
                        }
                        
                        override fun onHiddenPhotoActive(hidden: Boolean) {
                            scope.launch { vm.changeHidden(hidden) }
                        }
                        
                        override fun onCommentChange(text: String) {
                            scope.launch { vm.changeComment(text) }
                        }
                        
                        override fun onKebabClick(state: Boolean) {
                            scope.launch { vm.menuDismiss(state) }
                        }
                        
                        override fun onBack() {
                            scope.launch { vm.navigateBack() }
                        }
                        
                        override fun onCommentTextClear() {
                            scope.launch { vm.clearComment() }
                        }
                    }
                )
            }
        }
    }
}