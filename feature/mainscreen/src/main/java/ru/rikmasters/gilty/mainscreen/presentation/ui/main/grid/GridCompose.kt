package ru.rikmasters.gilty.mainscreen.presentation.ui.main.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.MeetingCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TodayMeetingGridPreview() {
    GiltyTheme {
        MeetingGridContent(Modifier.padding(16.dp), DemoMeetingList) {}
    }
}

@Composable
fun MeetingGridContent(
    modifier: Modifier = Modifier,
    meetings: List<MeetingModel>,
    onClick: (MeetingModel) -> (Unit)
) {
    LazyVerticalGrid(
        GridCells.Fixed(2),
        modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(meetings) { MeetingCard(it) { onClick(it) } }
        items(2) {
            Divider(Modifier.fillMaxWidth(), 20.dp, Transparent)
        }
    }
}