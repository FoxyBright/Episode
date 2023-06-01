package ru.rikmasters.gilty.mainscreen.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.MeetingCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun TodayMeetingGridPreview() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    colorScheme.background
                )
        ) {
            MeetingGridContent(
                Modifier.padding(16.dp),
                (DemoMeetingList)
            )
        }
    }
}

@Composable
fun MeetingGridContent(
    modifier: Modifier = Modifier,
    meetings: List<MeetingModel>,
    onClick: ((MeetingModel) -> Unit)? = null,
) {
    LazyVerticalGrid(
        Fixed(2), modifier,
        verticalArrangement = spacedBy(16.dp),
        horizontalArrangement = spacedBy(16.dp)
    ) {
        items(meetings) { meet: MeetingModel? ->
            meet?.let {
                MeetingCard(meeting = meet)
                { onClick?.let { it(meet) } }
            }
        }
        items(2) {
            Divider(
                Modifier.fillMaxWidth(),
                20.dp, Transparent
            )
        }
    }
}