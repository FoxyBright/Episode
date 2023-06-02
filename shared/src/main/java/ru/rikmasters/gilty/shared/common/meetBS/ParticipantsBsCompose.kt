package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.pagingPreview
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
            stringResource(R.string.meeting_members),
            Modifier, ("${meet.membersCount}" +
                    if(meet.membersMax > 0)
                        "/" + meet.membersMax else ""),
            if(meet.isOnline) colorScheme.secondary
            else colorScheme.primary
        ) { callback?.onBack() }
        Content(
            membersList = membersList,
            membersCount = meet.membersCount,
        ) { callback?.onMemberClick(it) }
    }
}

@Composable
private fun Content(
    membersList: LazyPagingItems<UserModel>,
    membersCount: Int,
    onMemberClick: (UserModel) -> Unit,
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
            ) { index, member ->
                member?.let {
                    MemberCard(
                        index = index,
                        member = it,
                        membersCount = membersCount,
                    ) { onMemberClick(it) }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberCard(
    index: Int,
    member: UserModel,
    membersCount: Int,
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
        shape = lazyItemsShapes(
            index = index,
            size = membersCount,
            radius = 14.dp
        ),
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        BrieflyRow(
            text = username,
            modifier = Modifier.padding(16.dp),
            image = member.avatar?.thumbnail?.url
        )
        if(index < membersCount - 1) GDivider(
            Modifier.padding(start = 60.dp)
        )
    }
}