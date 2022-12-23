package ru.rikmasters.gilty.profile.presentation.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun ParticipantsListPreview() {
    GiltyTheme { ParticipantsList(DemoMeetingModel, DemoMemberModelList) }
}

@Composable
fun ParticipantsList(
    meet: MeetingModel,
    membersList: List<MemberModel>,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onMemberClick: (() -> Unit)? = null
) {
    LazyColumn(
        modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        item {
            RowActionBar(
                stringResource(R.string.meeting_members),
                "${membersList.size}/${meet.memberCount}"
            ) { onBack?.let { it() } }
        }
        itemsIndexed(membersList) { index, member ->
            Card(
                Modifier.fillMaxWidth(),
                when(index) {
                    0 -> ThemeExtra.shapes.largeTopRoundedShape
                    membersList.size - 1 -> ThemeExtra.shapes.largeBottomRoundedShape
                    else -> ThemeExtra.shapes.zero
                }, CardDefaults.cardColors(
                    colorScheme.primaryContainer
                )
            ) {
                BrieflyRow(
                    meet.organizer.avatar,
                    "${member.username}, ${member.age}",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { onMemberClick?.let { it() } }
                ); if(index < membersList.size - 1)
                Divider(Modifier.padding(start = 60.dp))
            }
        }
    }
}