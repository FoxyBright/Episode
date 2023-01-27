package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsDetailed() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsContent(
                MeetingBsState(
                    (false), DemoFullMeetingModel,
                    detailed = Pair("", true)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsObserve() {
    GiltyTheme {
        val meet = DemoFullMeetingModel
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            MeetingBsContent(
                MeetingBsState(
                    (false), meet, DemoMemberModelList,
                    distanceCalculator(meet), (false)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsShared() {
    GiltyTheme {
        val meet = DemoFullMeetingModel
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            MeetingBsContent(
                MeetingBsState(
                    (false), meet, DemoMemberModelList,
                    distanceCalculator(meet), (false),
                    shared = true
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsWhenUser() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            val meet = DemoFullMeetingModel.copy(
                type = ANONYMOUS,
                isPrivate = true,
                isOnline = true
            )
            MeetingBsContent(
                MeetingBsState(
                    (false), meet, DemoMemberModelList,
                    distanceCalculator(meet), (true)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

data class MeetingBsState(
    val menuState: Boolean,
    val meet: FullMeetingModel,
    val membersList: List<MemberModel>? = null,
    val meetDistance: String? = null,
    val userInMeet: Boolean = false,
    val buttonState: Boolean = true,
    val shared: Boolean = false,
    val detailed: Pair<String, Boolean>? = null,
    val backButton: Boolean = false
)

interface MeetingBsCallback {
    
    fun onKebabClick(state: Boolean) {}
    fun onMenuItemClick(index: Int, meetId: String) {}
    fun onMeetPlaceClick(meetLocation: LocationModel?) {}
    fun onMemberClick(member: MemberModel) {}
    fun onRespondClick(meetId: String) {}
    fun onAvatarClick(organizerId: String) {}
    fun onAllMembersClick(meetId: String) {}
    fun onHiddenPhotoActive(hidden: Boolean) {}
    fun onCommentChange(text: String) {}
    fun onCommentTextClear() {}
    fun onRespondsClick() {}
    fun onBack(){}
}

@Composable
fun MeetingBsContent(
    state: MeetingBsState,
    modifier: Modifier = Modifier,
    callback: MeetingBsCallback? = null,
) {
    LazyColumn(modifier.background(colorScheme.background)) {
        
        item {
            MeetingBsTopBarCompose(
                Modifier.padding(
                    bottom = state.detailed
                        ?.let { 28.dp } ?: 0.dp
                ), MeetingBsTopBarState(
                    state.meet, state.menuState,
                    description = state.detailed == null,
                    backButton = state.backButton
                ), callback
            )
        }
        
        state.detailed?.let {
            
            item {
                Text(
                    stringResource(R.string.meeting_question_comment_or_assess),
                    Modifier, style = typography.labelLarge
                )
            }
            
            item {
                MeetingBsComment(
                    it.first, state.meet.isOnline,
                    { callback?.onCommentChange(it) },
                    Modifier.padding(top = 22.dp)
                ) { callback?.onCommentTextClear() }
            }
            
            item {
                MeetingBsHidden(
                    Modifier.padding(top = 8.dp),
                    it.second, state.meet.isOnline
                ) { callback?.onHiddenPhotoActive(it) }
            }
        } ?: run {
            
            item {
                MeetingBsConditions(
                    state.meet.map(), Modifier.padding(top = 32.dp)
                )
            }
            
            state.membersList?.let {
                item {
                    MeetingBsParticipants(
                        state.meet, it, Modifier,
                        { callback?.onAllMembersClick(state.meet.id) })
                    { callback?.onMemberClick(it) }
                }
            }
            
            state.meetDistance?.let {
                if(!state.meet.isOnline) item {
                    MeetingBsMap(
                        state.meet, it,
                        Modifier.padding(top = 28.dp)
                    ) { callback?.onMeetPlaceClick(state.meet.location) }
                }
            }
        }
        val memberState = state.meet.memberState
        item {
            when(memberState) {
                RESPOND_SENT -> TextButton(
                    Modifier, state.meet.isOnline,
                    stringResource(R.string.respond_to_meet)
                )
                
                IS_MEMBER, IS_ORGANIZER -> {}
                
                else -> GradientButton(
                    Modifier.padding(top = 20.dp, bottom = 12.dp),
                    stringResource(R.string.meeting_respond),
                    online = state.meet.isOnline,
                    enabled = when(memberState) {
                        IS_KICKED, NOT_UNDER_REQUIREMENTS,
                        RESPOND_REJECTED,
                        -> false
                        
                        else -> true
                    }
                ) { callback?.onRespondClick(state.meet.id) }
            }
        }
        item {
            Spacer(Modifier.height(40.dp))
        }
    }
}