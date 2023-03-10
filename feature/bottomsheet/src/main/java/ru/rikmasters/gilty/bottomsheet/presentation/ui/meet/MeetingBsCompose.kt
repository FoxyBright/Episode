package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
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
                    detailed = Pair("", true),
                    backButton = true,
                )
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
                    (false), meet, DemoUserModelList,
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
                    (false), meet, DemoUserModelList,
                    distanceCalculator(meet), (false)
                )
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
                    (false), meet, DemoUserModelList,
                    distanceCalculator(meet), (true)
                )
            )
        }
    }
}

data class MeetingBsState(
    val menuState: Boolean,
    val meet: FullMeetingModel,
    val membersList: List<UserModel>? = null,
    val meetDistance: String? = null,
    val buttonState: Boolean = true,
    val detailed: Pair<String, Boolean>? = null,
    val backButton: Boolean = false,
)

interface MeetingBsCallback {
    
    fun onKebabClick(state: Boolean) {}
    fun onMenuItemClick(index: Int, meetId: String) {}
    fun onMeetPlaceClick(location: LocationModel?) {}
    fun onMemberClick(member: UserModel) {}
    fun onRespond(meetId: String) {}
    fun onAvatarClick(organizerId: String, meetId: String) {}
    fun onAllMembersClick(meetId: String) {}
    fun onHiddenPhotoActive(hidden: Boolean) {}
    fun onCommentChange(text: String) {}
    fun onCommentTextClear() {}
    fun onRespondsClick(meet: FullMeetingModel) {}
    fun onBack() {}
}

@Composable
fun MeetingBsContent(
    state: MeetingBsState,
    modifier: Modifier = Modifier,
    callback: MeetingBsCallback? = null,
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        
        item {
            TopBar(
                state.meet, state.backButton,
                state.menuState,
                { callback?.onBack() },
                { callback?.onKebabClick(it) },
            ) { callback?.onMenuItemClick(it, state.meet.id) }
        }
        
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
                ) { callback?.onRespond(state.meet.id) }
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun TopBar(
    meet: FullMeetingModel,
    backButton: Boolean,
    menuState: Boolean,
    onBack: () -> Unit,
    onKebabClick: (Boolean) -> Unit,
    onMenuItemSelect: (Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Start, CenterVertically
        ) {
            if(backButton) IconButton(
                { onBack() },
                Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    stringResource(R.string.action_bar_button_back),
                    Modifier, colorScheme.tertiary
                )
            }
            Text(
                meet.tags.joinToString(separator = ", ")
                { it.title }, Modifier,
                colorScheme.tertiary,
                style = typography.labelLarge,
                overflow = Ellipsis,
                maxLines = 1
            )
        }
        Menu(
            menuState, meet,
            { onKebabClick(false) },
        ) { onMenuItemSelect(it) }
        GKebabButton { onKebabClick(true) }
    }
}

@Composable
private fun Menu(
    menuState: Boolean,
    meet: FullMeetingModel,
    onDismiss: (Boolean) -> Unit,
    onItemSelect: (Int) -> Unit,
) {
    val menuItems = arrayListOf(
        Pair(stringResource(R.string.meeting_shared_button))
        { onItemSelect(0) }
    )
    if(meet.memberState == IS_MEMBER) menuItems.add(
        Pair(stringResource(R.string.exit_from_meet))
        { onItemSelect(1) }
    )
    menuItems.add(
        if(meet.memberState == IS_ORGANIZER)
            Pair(stringResource(R.string.meeting_canceled))
            { onItemSelect(2) }
        else Pair(stringResource(R.string.meeting_complain))
        { onItemSelect(3) }
    )
    GDropMenu(
        menuState,
        { onDismiss(false) },
        menuItem = menuItems.toList()
    )
}