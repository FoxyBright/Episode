package ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ParticipantsListPreview() {
    GiltyTheme {
        ParticipantsList(
            DemoFullMeetingModel,
            DemoMemberModelList
        )
    }
}

interface ParticipantsListCallback {
    
    fun onBack() {}
    fun onMemberClick(member: MemberModel) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ParticipantsList(
    meet: FullMeetingModel,
    membersList: List<MemberModel>,
    modifier: Modifier = Modifier,
    callback: ParticipantsListCallback? = null,
) {
    Column(modifier) {
        RowActionBar(
            stringResource(R.string.meeting_members),
            "${membersList.size}/${meet.membersCount}"
        ) { callback?.onBack() }
        LazyColumn(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            itemsIndexed(membersList) { index, member ->
                Card(
                    { callback?.onMemberClick(member) },
                    Modifier.fillMaxWidth(),
                    shape = lazyItemsShapes(index, membersList.size, 14.dp),
                    colors = cardColors(colorScheme.primaryContainer)
                ) {
                    BrieflyRow(
                        meet.organizer.avatar,
                        "${member.username}, ${member.age}",
                        modifier = Modifier.padding(16.dp)
                    ); if(index < membersList.size - 1)
                    Divider(Modifier.padding(start = 60.dp))
                }
            }
        }
    }
}