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
import ru.rikmasters.gilty.shared.R.string.meeting_question_comment_or_assess
import ru.rikmasters.gilty.shared.R.string.respond_to_meet
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsDetailed() {
    GiltyTheme {
        val meet = DemoMeetingModel
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsContent(
                MeetingBsState(
                    (false), meet,
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
        val meet = DemoMeetingModel
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
        val meet = DemoMeetingModel
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
            val meet = DemoMeetingModel.copy(
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
    val meet: MeetingModel,
    val membersList: List<MemberModel>? = null,
    val meetDistance: String? = null,
    val userInMeet: Boolean = false,
    val buttonState: Boolean = true,
    val shared: Boolean = false,
    val detailed: Pair<String, Boolean>? = null,
    val alert: Boolean = false,
)

interface MeetingBsCallback {
    
    fun onKebabClick(state: Boolean) {}
    fun onMenuItemClick(index: Int) {}
    fun onMeetPlaceClick(meet: MeetingModel) {}
    fun onMemberClick(member: MemberModel) {}
    fun onBottomButtonClick(point: Int) {}
    fun onAvatarClick() {}
    fun closeAlert() {}
    fun onAllMembersClick() {}
    fun onHiddenPhotoActive(hidden: Boolean) {}
    fun onCommentChange(text: String) {}
    fun onCommentTextClear() {}
    fun onRespondsClick() {}
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
                    description = state.detailed == null
                ), callback
            )
        }
        
        state.detailed?.let {
            
            item {
                Text(
                    stringResource(meeting_question_comment_or_assess),
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
                    state.meet, Modifier.padding(top = 32.dp)
                )
            }
            
            state.membersList?.let {
                item {
                    MeetingBsParticipants(
                        state.meet, it, Modifier,
                        { callback?.onAllMembersClick() })
                    { callback?.onMemberClick(it) }
                }
            }
            
            state.meetDistance?.let {
                if(!state.meet.isOnline) item {
                    MeetingBsMap(
                        state.meet, it, Modifier.padding(top = 28.dp)
                    ) { callback?.onMeetPlaceClick(state.meet) }
                }
            }
        }
        item {
            if(state.buttonState) MeetingBsButtons(
                state.userInMeet, state.meet.isOnline, state.shared
            ) { callback?.onBottomButtonClick(it) }
            else TextButton(
                Modifier.padding(top = 28.dp, bottom = 32.dp),
                (null), stringResource(respond_to_meet)
            )
        }
        item {
            Spacer(Modifier.height(40.dp))
        }
    }
    GAlert(
        state.alert, { callback?.closeAlert() },
        stringResource(R.string.complaints_send_answer),
        label = stringResource(R.string.complaints_moderate_sen_answer),
        success = Pair(stringResource(R.string.meeting_close_button)) { callback?.closeAlert() }
    )
}