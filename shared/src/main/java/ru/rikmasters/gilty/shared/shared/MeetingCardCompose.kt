package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.categoriesListCard
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun MeetingCardTodayPreview() {
    MeetingCard(DemoMeetingModel, Modifier.padding(20.dp), true) {}
}

@Preview
@Composable
private fun MeetingCardPreview() {
    MeetingCard(DemoMeetingModel, Modifier.padding(20.dp)) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCard(
    meeting: ShortMeetingModel,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick,
        modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(ThemeExtra.colors.meetingCardBackBackground)
    )
    {
        Text(
            meeting.title,
            Modifier
                .width(180.dp)
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            Modifier
                .padding(start = 14.dp)
        ) {
            DateTimeCard(meeting.dateTime, Gradients.green(), today)
            if (today)
                categoriesListCard(
                    Modifier.padding(start = 4.dp), meeting, true
                )
        }
        Box(
            Modifier
                .offset(-(20).dp, 10.dp)
                .width(180.dp)
        ) {
            AsyncImage(
                meeting.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(CircleShape)
                    .size(135.dp),
                placeholder = painterResource(R.drawable.gb),
                contentScale = ContentScale.Crop
            )
            if (!today)
                categoriesListCard(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp), meeting, true
                )
        }
    }
}
