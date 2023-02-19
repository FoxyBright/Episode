package ru.rikmasters.gilty.meetbs.presentation.ui.meet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.*
import ru.rikmasters.gilty.meetbs.viewmodel.components.MeetingViewModel
import ru.rikmasters.gilty.shared.common.extentions.shareMeet
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun MeetingBs(
    vm: MeetingViewModel,
    old: Pair<String, String>,
    meetId: String,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val globalNav = get<NavState>()
    
    val memberList by vm.memberList.collectAsState()
    val meeting by vm.meet.collectAsState()
    val distance by vm.distance.collectAsState()
    val comment by vm.comment.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val menu by vm.menu.collectAsState()
    
    // TODO Когда будет готова логика открытия второго BS, открывать для отправки комментария
    val detailed = false
    
    val details = if(detailed) Pair(comment, hidden) else null
    val buttonState = meeting?.memberState == IS_ORGANIZER
    val backBut = !nav.backQueue.isEmpty()
    
    LaunchedEffect(Unit) {
        vm.drawMeet(meetId)
        vm.clearComment()
        vm.changeHidden(false)
    }
    Use<MeetingViewModel>(LoadingTrait) {
        meeting?.let { meet ->
            MeetingBsContent(
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
                                3 -> vm.navigate(nav, REPORTS, meetId)
                            }
                        }
                    }
                    
                    override fun onRespond(meetId: String) {
                        scope.launch {
                            vm.respondForMeet(meetId)
                            globalNav.navigateAbsolute("main/reaction?meetId=$meetId")
                            asm.bottomSheet.collapse()
                        }
                    }
                    
                    override fun onAvatarClick(organizerId: String) {
                        if(meet.type != MeetType.ANONYMOUS) scope.launch {
                            vm.navigate(nav, USER, organizerId)
                        }
                    }
                    
                    override fun onMemberClick(member: UserModel) {
                        if(meet.type != MeetType.ANONYMOUS) scope.launch {
                            vm.navigate(nav, USER, member.id!!)
                        }
                    }
                    
                    override fun onMeetPlaceClick(meetLocation: LocationModel?) {
                        scope.launch { vm.meetPlaceClick(meetLocation) }
                    }
                    
                    override fun onRespondsClick(meet: FullMeetingModel) {
                        scope.launch { vm.navigate(nav, RESPONDS, meet.id) }
                    }
                    
                    override fun onAllMembersClick(meetId: String) {
                        scope.launch { vm.navigate(nav, MEET, meetId) }
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
                        scope.launch {
                            vm.onBack(old)
                            nav.popBackStack()
                        }
                    }
                }
            )
        }
    }
}