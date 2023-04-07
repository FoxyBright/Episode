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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*

/*
@Preview
@Composable
private fun ParticipantsListPreview() {
    GiltyTheme {
        ParticipantsList(
            DemoFullMeetingModel,
            DemoUserModelList
        )
    }
}

 */

interface ParticipantsListCallback {
    
    fun onBack() {}
    fun onMemberClick(member: UserModel) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
        LazyColumn(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            when {
                membersList.loadState.refresh is LoadState.Error -> {}
                membersList.loadState.append is LoadState.Error -> {}
                membersList.loadState.refresh is LoadState.Loading -> {}
                else -> {
                    itemsIndexed(membersList) { index, member ->
                        member?.let {
                            Card(
                                { callback?.onMemberClick(member) },
                                Modifier.fillMaxWidth(),
                                shape = lazyItemsShapes(index, meet.membersCount, 14.dp),
                                colors = cardColors(colorScheme.primaryContainer)
                            ) {
                                BrieflyRow(
                                    "${member.username}, ${member.age}",
                                    Modifier.padding(16.dp),
                                    member.avatar?.thumbnail?.url
                                ); if(index < meet.membersCount - 1)
                                Divider(Modifier.padding(start = 60.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}