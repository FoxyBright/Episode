package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.presentation.ui.MeetReaction
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.shared.bottomsheet.*
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue.Collapsed
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
private fun MeetingBsDetailed() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsContent(
                MeetingBsState(
                    (false), DemoFullMeetingModel,
                    Pair(0, null),
                    detailed = true,
                    comment = "",
                    hidden = true,
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
                    (false), meet, Pair(0, null),
                    pagingPreview(DemoUserModelList),
                    "18 км", (false)
                ), Modifier
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
                    (false), meet, Pair(0, null),
                    pagingPreview(DemoUserModelList),
                    "18 км", (false)
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
                    (false), meet, Pair(0, null),
                    pagingPreview(DemoUserModelList),
                    "18 км", (true)
                )
            )
        }
    }
}

data class MeetingBsState(
    val menuState: Boolean,
    val meet: FullMeetingModel,
    val lastRespond: Pair<Int, String?>?,
    val membersList: LazyPagingItems<UserModel>? = null,
    val meetDistance: String? = null,
    val buttonState: Boolean = true,
    val detailed: Boolean = false,
    val comment: String? = null,
    val hidden: Boolean? = null,
    val backButton: Boolean = false,
    val meetReaction: Boolean = false,
)

interface MeetingBsCallback {
    
    fun onKebabClick(state: Boolean)
    fun onMenuItemClick(index: Int, meetId: String)
    fun onMeetPlaceClick(location: LocationModel?)
    fun onMemberClick(member: UserModel)
    fun onRespond(meetId: String)
    fun onAvatarClick(organizerId: String, meetId: String)
    fun onAllMembersClick(meetId: String)
    fun onHiddenPhotoActive(hidden: Boolean)
    fun onCommentChange(text: String)
    fun onCommentTextClear()
    fun onRespondsClick(meet: FullMeetingModel)
    fun onBack()
    fun meetReactionDisable()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingBsContent(
    state: MeetingBsState,
    modifier: Modifier = Modifier,
    callback: MeetingBsCallback? = null,
) {
    val scope = rememberCoroutineScope()
    val bsState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(Collapsed)
    )
    BottomSheetScaffold(
        sheetContent = {
            Additional(state, callback)
        }, scaffoldState = bsState,
        sheetPeekHeight = 0.dp,
        sheetShape = shapes.bigTopShapes,
        sheetBackgroundColor = Color.Transparent
    ) {
        Box {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .alpha(0.5f)
            )
            Scaffold(bottomBar = {
                Button(
                    state.meet.memberState,
                    state.meet.isOnline,
                    Modifier.padding(horizontal = 16.dp)
                ) {
                    if(state.detailed)
                        callback?.onRespond(state.meet.id)
                    else scope.launch {
                        bsState.bottomSheetState.expand()
                    }
                }
            }) {
                // Негативный паддинг ))))))))))
                MeetContent(
                    state, modifier.padding(
                        bottom = if(it.calculateBottomPadding() // - 24.dp >= 0.dp)
                            it.calculateBottomPadding() - 24.dp else 0.dp
                    ), callback
                )
            }
        }
    }
    MeetReaction(state.meet, state.meetReaction) {
        callback?.meetReactionDisable()
    }
}

@Composable
private fun Button(
    memberState: MemberStateType?,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    when(memberState) {
        IS_MEMBER, IS_ORGANIZER -> Unit
        RESPOND_SENT -> TextButton(
            modifier.padding(bottom = 50.dp),
            isOnline, stringResource(R.string.respond_to_meet)
        )
        else -> GradientButton(
            modifier.padding(bottom = 50.dp),
            stringResource(R.string.meeting_join_button_name),
            (memberState == UNDER_REQUIREMENTS),
            isOnline
        ) { onClick() }
    }
}

@Composable
private fun Additional(
    state: MeetingBsState,
    callback: MeetingBsCallback?,
) {
    var comment by remember {
        mutableStateOf(state.comment ?: "")
    }
    var hidden by remember {
        mutableStateOf(state.hidden ?: false)
    }
    Column(
        Modifier
            .background(colorScheme.background)
            .padding(horizontal = 16.dp),
        Top, CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .size(40.dp, 5.dp)
                .background(
                    colorScheme.outline,
                    CircleShape
                )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.meeting_question_comment_or_assess),
            Modifier.fillMaxWidth(),
            style = typography.labelLarge,
            color = colorScheme.tertiary
        )
        MeetingBsComment(
            comment, state.meet.isOnline,
            { comment = it },
            Modifier.padding(top = 22.dp)
        ) { comment = "" }
        MeetingBsHidden(
            Modifier.padding(top = 8.dp),
            hidden, state.meet.isOnline
        ) { hidden = it }
        Spacer(Modifier.height(28.dp))
        Button(
            state.meet.memberState,
            state.meet.isOnline,
        ) {
            callback?.onCommentChange(comment)
            callback?.onHiddenPhotoActive(hidden)
            callback?.onRespond(state.meet.id)
        }
    }
}

@Composable
private fun MeetContent(
    state: MeetingBsState,
    modifier: Modifier = Modifier,
    callback: MeetingBsCallback?,
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        item {
            TopBar(
                state.meet,
                state.backButton,
                state.menuState,
                { callback?.onBack() },
                { callback?.onKebabClick(it) }
            ) { callback?.onMenuItemClick(it, state.meet.id) }
        }
        
        item {
            MeetingBsTopBarCompose(
                Modifier.padding(
                    bottom = if(state.detailed)
                        28.dp else 0.dp
                ), MeetingBsTopBarState(
                    state.meet,
                    state.menuState,
                    state.lastRespond,
                    description = state.detailed,
                    backButton = state.backButton
                ), callback
            )
        }
        
        if(state.detailed) {
            
            item {
                Text(
                    stringResource(R.string.meeting_question_comment_or_assess),
                    Modifier,
                    style = typography.labelLarge
                )
            }
            
            item {
                MeetingBsComment(
                    state.comment ?: "",
                    state.meet.isOnline,
                    { callback?.onCommentChange(it) },
                    Modifier.padding(top = 22.dp)
                ) { callback?.onCommentTextClear() }
            }
            
            item {
                MeetingBsHidden(
                    Modifier.padding(top = 8.dp),
                    state.hidden ?: false,
                    state.meet.isOnline
                ) { callback?.onHiddenPhotoActive(it) }
            }
        } else {
            item {
                MeetingBsConditions(
                    state.meet.map(),
                    Modifier.padding(
                        top = if(state.meet.description.isNotBlank())
                            32.dp else 0.dp
                    )
                )
            }
            state.membersList?.let {
                item {
                    MeetingBsParticipants(
                        state.meet, it, Modifier,
                        { callback?.onAllMembersClick(state.meet.id) }
                    ) { callback?.onMemberClick(it) }
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
            itemSpacer(40.dp)
        }
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
                    Modifier,
                    colorScheme.tertiary
                )
            }
            Text(
                meet.tags.joinToString(separator = ", ") { it.title },
                Modifier,
                colorScheme.tertiary,
                style = typography.labelLarge,
                overflow = Ellipsis,
                maxLines = 1
            )
        }
        Menu(
            menuState,
            meet,
            { onKebabClick(false) }
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
        Pair(stringResource(R.string.meeting_shared_button)) {
            onItemSelect(
                0
            )
        }
    )
    if(meet.memberState == IS_MEMBER) menuItems.add(
        Pair(stringResource(R.string.exit_from_meet)) { onItemSelect(1) }
    )
    menuItems.add(
        if(meet.memberState == IS_ORGANIZER) {
            Pair(stringResource(R.string.meeting_canceled)) {
                onItemSelect(
                    2
                )
            }
        } else Pair(stringResource(R.string.meeting_complain)) {
            onItemSelect(
                3
            )
        }
    )
    GDropMenu(
        menuState,
        { onDismiss(false) },
        menuItem = menuItems.toList()
    )
}