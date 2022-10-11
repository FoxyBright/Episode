package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.shared.MeetingCard
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TodayMeetingGridPreview() {
    GiltyTheme {
        MeetingGridCompose(Modifier.padding(16.dp), DemoMeetingList) {}
    }
}

@Composable
fun MeetingGridCompose(
    modifier: Modifier = Modifier,
    meetings: List<ShortMeetingModel>,
    onClick: () -> (Unit)
) {
    LazyVerticalGrid(
        GridCells.Fixed(2),
        modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(meetings) {
            MeetingCard(it) {}
        }
    }
}