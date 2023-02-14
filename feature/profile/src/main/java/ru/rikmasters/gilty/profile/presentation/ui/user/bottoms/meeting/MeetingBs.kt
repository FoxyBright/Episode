package ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.meeting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.shared.common.extentions.shareMeet
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.*
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsContent
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsState
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun MeetingBs(
    vm: MeetingBsViewModel,
    meetId: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val memberList by vm.memberList.collectAsState()
    val navigator by vm.navigator.collectAsState()
    val meeting by vm.meet.collectAsState()
    val stack by vm.stack.collectAsState()
    val distance by vm.distance.collectAsState()
    val comment by vm.comment.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val menu by vm.menu.collectAsState()
    
    val details = if(false) Pair(comment, hidden) else null
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
                ORGANIZER -> OrganizerBs(vm, meet.type)
                PARTICIPANTS -> ParticipantsBs(vm, meet)
                COMPLAINTS -> ComplaintsBs(vm, meetId)
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
                                when(index) {
                                    0 -> shareMeet(meetId, context)
                                    1 -> vm.leaveMeet(meetId)
                                    2 -> vm.canceledMeet(meetId)
                                    3 -> vm.navigate(COMPLAINTS)
                                    else -> {}
                                }
                                if(index in 1..2) asm.bottomSheet.collapse()
                            }
                        }
                        
                        override fun onAvatarClick(organizerId: String) {
                            scope.launch {
                                if(meet.type != ANONYMOUS)
                                    vm.getUserActualMeets(organizerId)
                                vm.navigate(ORGANIZER, organizerId)
                            }
                        }
                        
                        override fun onMemberClick(member: UserModel) {
                            scope.launch {
                                if(meet.type != ANONYMOUS)
                                    vm.getUserActualMeets(member.id!!)
                                vm.navigate(ORGANIZER, member.id!!)
                            }
                        }
                        
                        override fun onAllMembersClick(meetId: String) {
                            scope.launch { vm.navigate(PARTICIPANTS, meetId) }
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
                        
                        override fun onHiddenPhotoActive(hidden: Boolean) {
                            scope.launch { vm.changeHidden(hidden) }
                        }
                        
                        override fun onCommentChange(text: String) {
                            scope.launch { vm.changeComment(text) }
                        }
                        
                        override fun onKebabClick(state: Boolean) {
                            scope.launch { vm.menuDismiss(state) }
                        }
                        
                        override fun onCommentTextClear() {
                            scope.launch { vm.clearComment() }
                        }
                        
                        override fun onBack() {
                            scope.launch { vm.navigateBack() }
                        }
                        
                        override fun onRespondsClick() {
                        }
                    }
                )
            }
        }
    }
}