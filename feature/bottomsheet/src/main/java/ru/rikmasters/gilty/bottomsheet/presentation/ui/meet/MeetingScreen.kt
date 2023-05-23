package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.viewmodel.MeetingBsViewModel
import ru.rikmasters.gilty.core.util.composable.getActivity
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.common.extentions.distance
import ru.rikmasters.gilty.shared.common.extentions.shareMeet
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.MEETING

@Composable
fun MeetingBs(
    vm: MeetingBsViewModel,
    meetId: String,
    detailed: Boolean,
    nav: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = getActivity()
    
    val memberList = vm.membersList.collectAsLazyPagingItems()
    val lastResponse by vm.lastResponse.collectAsState()
    val location by vm.location.collectAsState()
    val meetReaction by vm.meetReaction.collectAsState()
    val meeting by vm.meet.collectAsState()
    val distance by vm.distance.collectAsState()
    val comment by vm.comment.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val menu by vm.menu.collectAsState()
    
    val buttonState = meeting?.memberState == IS_ORGANIZER
    val backBut = nav.previousBackStackEntry != null
    
    LaunchedEffect(Unit) {
        vm.getLocation(activity)
        vm.getMeet(meetId)
        vm.clearComment()
        vm.changeHidden(false)
    }
    
    LaunchedEffect(meeting) {
        vm.changeDistance(
            meeting?.let {
                it distance location
            } ?: ""
        )
    }
    
    Use<MeetingBsViewModel>(LoadingTrait) {
        meeting?.let { meet ->
            MeetingBsContent(
                MeetingBsState(
                    menu, meet, lastResponse, memberList,
                    distance, buttonState, detailed,
                    comment, hidden, backBut, meetReaction,
                ), Modifier, object: MeetingBsCallback {
                    
                    override fun meetReactionDisable() {
                        scope.launch { vm.meetReactionDisable(false) }
                    }
                    
                    override fun onMenuItemClick(
                        index: Int, meetId: String,
                    ) {
                        scope.launch {
                            when(index) {
                                0 -> shareMeet(meetId, context)
                                1 -> vm.leaveMeet(meetId)
                                2 -> vm.canceledMeet(meetId)
                                3 -> nav.navigate(
                                    "REPORTS?id=$meetId&type=${MEETING.name}"
                                )
                            }
                            vm.menuDismiss(false)
                        }
                    }
                    
                    override fun onRespond(meetId: String) {
                        scope.launch {
                            vm.respondForMeet(meetId)
                            vm.meetReactionDisable(true)
                            vm.clearComment()
                            vm.changeHidden(hidden)
                        }
                    }
                    
                    override fun onAvatarClick(
                        organizerId: String,
                        meetId: String,
                    ) {
                        if(meet.type != MeetType.ANONYMOUS) nav.navigate(
                            "USER?user=$organizerId&meet=$meetId"
                        )
                    }
                    
                    override fun onMemberClick(member: UserModel) {
                        if(meet.type != MeetType.ANONYMOUS) nav.navigate(
                            "USER?user=${member.id}&meet=${meet.id}"
                        )
                    }
                    
                    override fun onMeetPlaceClick(location: LocationModel?) {
                        nav.navigate(
                            "MAP?location=${
                                mapper.writeValueAsString(
                                    location
                                )
                            }"
                        )
                    }
                    
                    override fun onRespondsClick(meet: FullMeetingModel) {
                        nav.navigate("RESPONDS?meet=${meet.id}")
                    }
                    
                    override fun onAllMembersClick(meetId: String) {
                        nav.navigate("PARTICIPANTS?meet=$meetId")
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
                        nav.popBackStack()
                    }
                }
            )
        }
    }
}
