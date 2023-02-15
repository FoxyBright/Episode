package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.meets

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
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.ObserveBsViewModel
import ru.rikmasters.gilty.shared.common.extentions.shareMeet
import ru.rikmasters.gilty.shared.common.meetBS.*
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.*
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun ObserveBs(
    vm: ObserveBsViewModel,
    type: MeetNavigation,
    meet: MeetingModel? = null,
    user: UserModel? = null,
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
    val menu by vm.menu.collectAsState()
    
    //TODO для открытия второго BS с комментарием
    //    val hidden by vm.hidden.collectAsState()
    //    val comment by vm.comment.collectAsState()
    //    val details = if(
    //        meet.memberState != IS_MEMBER
    //        && meet.memberState != IS_ORGANIZER
    //    ) Pair(comment, hidden) else null
    
    val buttonState = meeting?.memberState == IS_ORGANIZER
    val backBut = stack.size > 1
    
    LaunchedEffect(Unit) {
        vm.clearStack()
        vm.navigate(
            type, if(type == MEET) meet?.id!!
            else user?.id!!
        )
        vm.clearComment()
        vm.changeHidden(false)
    }
    
    meeting?.let {
        navigator?.let { screen ->
            when(screen.navigation) {
                PARTICIPANTS -> ParticipantsBs(vm, it)
                COMPLAINTS -> ComplaintsBs(vm, meet?.id!!)
                ORGANIZER -> OrganizerBs(vm, it)
                MEET -> MeetingBsContent(
                    MeetingBsState(
                        menu, it, memberList, distance,
                        buttonState, (null), backBut
                    ), Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    object: MeetingBsCallback {
                        
                        override fun onMenuItemClick(index: Int, meetId: String) {
                            scope.launch {
                                if(index == 0) shareMeet(meetId, context)
                                if(index == 3) vm.navigate(COMPLAINTS)
                            }
                        }
                        
                        override fun onAvatarClick(organizerId: String) {
                            scope.launch {
                                if(it.type != MeetType.ANONYMOUS)
                                    vm.getUserActualMeets(organizerId)
                                vm.navigate(ORGANIZER, organizerId)
                            }
                        }
                        
                        override fun onMemberClick(member: UserModel) {
                            scope.launch {
                                if(it.type != MeetType.ANONYMOUS)
                                    vm.getUserActualMeets(member.id!!)
                                vm.navigate(ORGANIZER, member.id!!)
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