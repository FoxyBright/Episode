package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.presentation.ui.MeetReaction
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.CANCELED
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.COMPLETED
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
                state = MeetingBsState(
                    menuState = false,
                    meet = DemoFullMeetingModel,
                    lastRespond = LastRespond(),
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
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    colorScheme.background
                )
        ) {
            MeetingBsContent(
                state = MeetingBsState(
                    menuState = false,
                    meet = DemoFullMeetingModel,
                    lastRespond = LastRespond(),
                    membersList = pagingPreview(
                        DemoUserModelList
                    ),
                    meetDistance = "18 км",
                    buttonState = false
                ),
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsShared() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    colorScheme.background
                )
        ) {
            MeetingBsContent(
                state = MeetingBsState(
                    menuState = false,
                    meet = DemoFullMeetingModel,
                    lastRespond = LastRespond(),
                    membersList = pagingPreview(
                        DemoUserModelList
                    ),
                    meetDistance = "18 км",
                    buttonState = false
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
                .background(
                    colorScheme.background
                )
        ) {
            MeetingBsContent(
                state = MeetingBsState(
                    menuState = false,
                    meet = DemoFullMeetingModel.copy(
                        type = ANONYMOUS,
                        isPrivate = true,
                        isOnline = true
                    ),
                    lastRespond = LastRespond(),
                    membersList = pagingPreview(
                        DemoUserModelList
                    ),
                    meetDistance = "18 км",
                    buttonState = true
                )
            )
        }
    }
}

data class MeetingBsState(
    val menuState: Boolean,
    val meet: FullMeetingModel,
    val lastRespond: LastRespond?,
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
    bsState: BottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(Collapsed)
        ),
) {
    BottomSheetScaffold(
        sheetContent = {
            Additional(state, callback)
        },
        scaffoldState = bsState,
        sheetPeekHeight = 0.dp,
        sheetShape = shapes.bigTopShapes,
        sheetBackgroundColor = Transparent
    ) {
        ContentContainer(
            state = state,
            paddings = it,
            callback = callback,
            bsState = bsState
        )
    }
    MeetReaction(
        meet = state.meet,
        state = state.meetReaction
    ) { callback?.meetReactionDisable() }
}

@Composable
private fun ContentContainer(
    state: MeetingBsState,
    paddings: PaddingValues,
    modifier: Modifier = Modifier,
    callback: MeetingBsCallback?,
    bsState: BottomSheetScaffoldState,
    scope: CoroutineScope = rememberCoroutineScope(),
    ms: MemberStateType? = state.meet.memberState,
) {
    Box(Modifier.padding(paddings)) {
        MeetContent(
            state = state,
            modifier = modifier
                .align(Center)
                .padding(
                    bottom = if(
                        ms != IS_MEMBER
                        && ms != IS_ORGANIZER
                    ) 42.dp else 0.dp
                ),
            callback = callback
        )
        Button(
            memberState = state
                .meet.memberState,
            isOnline = state
                .meet.isOnline,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(BottomCenter)
        ) {
            if(state.detailed)
                callback?.onRespond(state.meet.id)
            else scope.launch {
                bsState.bottomSheetState.expand()
            }
        }
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
            modifier = modifier.padding(bottom = 40.dp),
            text = stringResource(R.string.respond_to_meet)
        )
        
        else -> GradientButton(
            modifier = modifier.padding(bottom = 40.dp),
            text = stringResource(
                R.string.meeting_join_button_name
            ),
            enabled = memberState == UNDER_REQUIREMENTS,
            online = isOnline
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
            text = stringResource(
                R.string.meeting_question_comment_or_assess
            ),
            modifier = Modifier.fillMaxWidth(),
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
    Column(
        modifier
            .fillMaxSize()
            .background(
                colorScheme.background
            )
            .padding(horizontal = 16.dp)
    ) {
        TopBar(
            meet = state.meet,
            backButton = state.backButton,
            menuState = state.menuState,
            onBack = { callback?.onBack() },
            onKebabClick = { callback?.onKebabClick(it) }
        ) { callback?.onMenuItemClick(it, state.meet.id) }
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(18.dp))
            MeetingBsTopBarCompose(
                state = MeetingBsTopBarState(
                    meet = state.meet,
                    lastRespond = state.lastRespond,
                    description = state.detailed,
                    backButton = state.backButton
                ),
                callback = callback
            )
            if(state.detailed)
                Detailed(state, callback)
            else
                Content(state, callback)
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun Detailed(
    state: MeetingBsState,
    callback: MeetingBsCallback?,
) {
    Text(
        text = stringResource(
            R.string.meeting_question_comment_or_assess
        ),
        modifier = Modifier.padding(top = 4.dp),
        style = typography.labelLarge
    )
    MeetingBsComment(
        text = state.comment ?: "",
        online = state.meet.isOnline,
        onTextChange = { callback?.onCommentChange(it) },
        modifier = Modifier.padding(top = 22.dp)
    ) { callback?.onCommentTextClear() }
    MeetingBsHidden(
        modifier = Modifier.padding(top = 8.dp),
        state = state.hidden ?: false,
        online = state.meet.isOnline
    ) { callback?.onHiddenPhotoActive(it) }
}

@Composable
private fun Content(
    state: MeetingBsState,
    callback: MeetingBsCallback?,
) {
    MeetingBsConditions(
        meet = state.meet.map(),
        modifier = Modifier.padding(
            top = if(state.meet.description.isNotBlank())
                32.dp else 0.dp
        )
    )
    state.membersList?.let {
        MeetingBsParticipants(
            meet = state.meet,
            membersList = it,
            modifier = Modifier,
            onAllViewClick = {
                callback?.onAllMembersClick(
                    state.meet.id
                )
            },
            onMemberClick = { member ->
                callback?.onMemberClick(member)
            }
        )
    }
    if(state.meetDistance != null
        && !state.meet.isOnline
    ) MeetingBsMap(
        meet = state.meet,
        distance = state.meetDistance,
        modifier = Modifier.padding(top = 28.dp),
        onClick = {
            callback?.onMeetPlaceClick(
                state.meet.location
            )
        }
    )
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
            .padding(top = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Start, CenterVertically
        ) {
            TopBarBackButton(backButton, onBack)
            meet.TopBarTitle()
        }
        Menu(
            menuState = menuState,
            meet = meet,
            onDismiss = { onKebabClick(false) }
        ) { onMenuItemSelect(it) }
        GKebabButton { onKebabClick(true) }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun FullMeetingModel.TopBarTitle() {
    Text(
        text = tags.joinToString(", ")
        { it.title },
        modifier = Modifier
            .padding(end = 9.dp)
            .basicMarquee(),
        color = colorScheme.tertiary,
        style = typography.labelLarge,
        overflow = Ellipsis,
        maxLines = 1
    )
    Text(
        text = stringResource(
            when(status) {
                CANCELED ->
                    R.string.meetings_canceled
                COMPLETED ->
                    R.string.meetings_finished
                else -> R.string.empty_String
            }
        ),
        style = typography.labelSmall,
        overflow = Ellipsis,
        maxLines = 1
    )
}

@Composable
private fun TopBarBackButton(
    backButton: Boolean,
    onBack: () -> Unit,
) {
    if(backButton) IconButton(
        onClick = { onBack() },
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Icon(
            painter = painterResource(
                R.drawable.ic_back
            ),
            contentDescription = stringResource(
                R.string.action_bar_button_back
            ),
            tint = colorScheme.tertiary
        )
    }
}

@Composable
private fun Menu(
    menuState: Boolean,
    meet: FullMeetingModel,
    ms: MemberStateType? = meet.memberState,
    onDismiss: (Boolean) -> Unit,
    onItemSelect: (Int) -> Unit,
) {
    val menuItems =
        arrayListOf<Pair<String, () -> Unit>>()
    if(ms == IS_MEMBER || ms == IS_ORGANIZER)
        menuItems.add(
            stringResource(R.string.meeting_shared_button) to
                    { onItemSelect(0) })
    if(ms == IS_MEMBER)
        menuItems.add(
            stringResource(R.string.exit_from_meet) to
                    { onItemSelect(1) })
    menuItems.add(
        if(ms == IS_ORGANIZER)
            stringResource(R.string.meeting_canceled) to
                    { onItemSelect(2) }
        else stringResource(R.string.meeting_complain) to
                { onItemSelect(3) })
    if(menuState && menuItems.size == 1) {
        onDismiss(false)
        menuItems.first().second()
    }
    GDropMenu(
        menuState = menuState,
        collapse = { onDismiss(false) },
        menuItem = menuItems.toList()
    )
}