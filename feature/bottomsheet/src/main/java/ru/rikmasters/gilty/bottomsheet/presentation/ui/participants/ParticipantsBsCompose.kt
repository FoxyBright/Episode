package ru.rikmasters.gilty.bottomsheet.presentation.ui.participants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ParticipantsListPreview() {
    GiltyTheme {
        ParticipantsList(
            meet = DemoFullMeetingModel,
            membersList = pagingPreview(
                DemoUserModelList
            )
        )
    }
}

interface ParticipantsListCallback {
    
    fun onBack() {}
    fun onMemberClick(member: UserModel) {}
    fun onDelete(member: UserModel) {}
}

@Composable
fun ParticipantsList(
    meet: FullMeetingModel,
    membersList: LazyPagingItems<UserModel>,
    modifier: Modifier = Modifier,
    callback: ParticipantsListCallback? = null,
) {
    Column(modifier) {
        RowActionBar(
            title = stringResource(R.string.meeting_members),
            details = ("${meet.membersCount}" +
                    if(meet.membersMax > 0)
                        "/" + meet.membersMax else ""),
            detailsColor = if(meet.isOnline)
                colorScheme.secondary
            else colorScheme.primary
        ) { callback?.onBack() }
        Content(
            isOrganizer = meet.memberState
                    == IS_ORGANIZER,
            membersList = membersList,
            membersCount = meet.membersCount,
            callback = callback
        )
    }
}

@Composable
private fun Content(
    isOrganizer: Boolean = false,
    membersList: LazyPagingItems<UserModel>,
    membersCount: Int,
    callback: ParticipantsListCallback?,
) {
    LazyColumn(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(
                colorScheme.background
            )
    ) {
        val state =
            membersList.loadState
        when {
            state.refresh is
                    LoadState.Error -> Unit
            state.append is
                    LoadState.Error -> Unit
            state.refresh is
                    LoadState.Loading -> Unit
            else -> itemsIndexed(
                items = membersList
            ) { index, item ->
                item?.let { member ->
                    val shape = lazyItemsShapes(
                        index = index,
                        size = membersCount,
                        radius = 14.dp
                    )
                    if(!isOrganizer) MemberCard(
                        member = member, index = index,
                        size = membersCount, shape = shape,
                    ) { callback?.onMemberClick(member) }
                    else (member to rememberDragRowState())
                        .let { (member, state) ->
                            DragableRow(
                                member = member,
                                index = index,
                                size = membersCount,
                                shape = shape,
                                state = state,
                                callback = callback,
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun DragableRow(
    member: UserModel,
    index: Int,
    size: Int,
    shape: Shape,
    state: DragRowState,
    modifier: Modifier = Modifier,
    callback: ParticipantsListCallback?,
) {
    var isBackgroundVisible by remember {
        mutableStateOf(false)
    }
    
    LaunchedEffect(state.offset.targetValue.x) {
        isBackgroundVisible = state.offset
            .targetValue.x != 0.0f
    }
    
    Box(
        modifier
            .fillMaxWidth()
            .background(
                color = if(isBackgroundVisible)
                    colorScheme.primary
                else Transparent,
                shape = shape
            )
    ) {
        SwipeableRowBack(Modifier.align(CenterEnd))
        Row(
            Modifier.swipeableRow(
                state = state,
                context = LocalContext.current
            ) { callback?.onDelete(member) },
            Center, CenterVertically
        ) {
            MemberCard(
                member = member, index = index,
                size = size, shape = shape,
            ) { callback?.onMemberClick(member) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MemberCard(
    index: Int,
    member: UserModel,
    size: Int,
    shape: Shape,
    onMemberClick: () -> Unit,
) {
    val username = "${member.username}${
        if(member.age in 18..99) {
            ", ${member.age}"
        } else ""
    }"
    Card(
        onClick = { onMemberClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        BrieflyRow(
            text = username,
            modifier = Modifier.padding(16.dp),
            image = member.avatar?.thumbnail?.url ?: "",
            isOnline = member.isOnline?: false,
            group = member.group?: UserGroupTypeModel.DEFAULT
        )
        if(index < size - 1) GDivider(
            Modifier.padding(start = 60.dp)
        )
    }
}