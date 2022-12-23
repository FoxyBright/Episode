package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.string.meeting_question_comment_or_assess
import ru.rikmasters.gilty.shared.R.string.respond_to_meet
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBSDetailed() {
    GiltyTheme {
        val meet = getDemoMeetingModel()
        Box(Modifier.background(colorScheme.background)) {
            MeetingBottomSheet(
                MeetingBSState(
                    (false), meet,
                    detailed = Pair("", true)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBSObserve() {
    GiltyTheme {
        val meet = getDemoMeetingModel()
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            MeetingBottomSheet(
                MeetingBSState(
                    (false), meet, DemoMemberModelList,
                    distanceCalculator(meet), (false)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBSShared() {
    GiltyTheme {
        val meet = getDemoMeetingModel()
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            MeetingBottomSheet(
                MeetingBSState(
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
private fun MeetingBSWhenUser() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            val meet = getDemoMeetingModel(
                type = ANONYMOUS,
                isPrivate = true,
                isOnline = true
            )
            MeetingBottomSheet(
                MeetingBSState(
                    (false), meet, DemoMemberModelList,
                    distanceCalculator(meet), (true)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

data class MeetingBSState(
    val menuState: Boolean,
    val meet: MeetingModel,
    val membersList: List<MemberModel>? = null,
    val meetDistance: String? = null,
    val userInMeet: Boolean = false,
    val buttonState: Boolean = true,
    val shared: Boolean = false,
    val detailed: Pair<String, Boolean>? = null,
    val alert: Boolean = false
)

interface MeetingBSCallback {
    
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
}

@Composable
fun MeetingBottomSheet(
    state: MeetingBSState,
    modifier: Modifier = Modifier,
    callback: MeetingBSCallback? = null,
) {
    val meet = state.meet
    val membersList = state.membersList
    LazyColumn(modifier.background(colorScheme.background)) {
        
        item {
            MeetingBSTopBarCompose(
                Modifier, MeetingBSTopBarState(
                    meet, state.menuState
                ), { callback?.onKebabClick(it) },
                { callback?.onMenuItemClick(it) }
            ) { callback?.onAvatarClick() }
        }
        
        state.detailed?.let {
            
            item {
                Text(
                    stringResource(meeting_question_comment_or_assess),
                    Modifier, style = typography.labelLarge
                )
            }
            
            item {
                MeetingBSComment(
                    it.first, state.meet.isOnline,
                    { callback?.onCommentChange(it) },
                    Modifier.padding(top = 22.dp)
                ) { callback?.onCommentTextClear() }
            }
            
            item {
                MeetingBSHidden(
                    it.second, meet.isOnline,
                    Modifier.padding(top = 8.dp)
                ) { callback?.onHiddenPhotoActive(it) }
            }
        } ?: run {
            
            item {
                MeetingBSConditions(
                    meet, Modifier.padding(top = 32.dp)
                )
            }
            
            membersList?.let {
                item {
                    MeetingBSParticipants(
                        meet, it, Modifier,
                        { callback?.onAllMembersClick() })
                    { callback?.onMemberClick(it) }
                }
            }
            
            state.meetDistance?.let {
                if(!meet.isOnline) item {
                    MeetingBSMap(
                        meet, it, Modifier.padding(top = 28.dp)
                    ) { callback?.onMeetPlaceClick(meet) }
                }
            }
        }
        item {
            if(state.buttonState) MeetingBSButtons(
                state.userInMeet, meet.isOnline, state.shared
            ) { callback?.onBottomButtonClick(it) }
            else TextButton(
                stringResource(respond_to_meet),
                modifier = Modifier.padding(top = 28.dp, bottom = 32.dp)
            )
        }
    }
    GAlert(
        state.alert, { callback?.closeAlert() },
        "Отлично, ваша жалоба отправлена!",
        label = "Модераторы скоро рассмотрят\nвашу жалобу",
        success = Pair("Закрыть") { callback?.closeAlert() }
    )
}