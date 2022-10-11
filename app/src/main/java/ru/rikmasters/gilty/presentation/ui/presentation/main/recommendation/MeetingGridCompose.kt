package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.ConditionType
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.ui.theme.Gradients
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import ru.rikmasters.gilty.utility.extentions.format


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TodayMeetingGridPreview() {
    GiltyTheme {
        MeetingGridCompose(Modifier.padding(16.dp), DemoMeetingList) {}
    }
}

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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MeetingCard(
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
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.Body1Bold,
            fontWeight = FontWeight.Bold
        )
        Row(
            Modifier
                .padding(start = 14.dp)
        ) {
            Box(
                Modifier
                    .background(
                        Brush.linearGradient(Gradients().green()),
                        MaterialTheme.shapes.large
                    ),
            ) {
                Text(
                    meeting.dateTime.format(if (today) "HH:mm" else "dd MMMM"),
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = ThemeExtra.typography.SubHeadEb,
                    color = Color.White
                )
            }
            if (today)
                categoriesLine(Modifier.padding(start = 4.dp), meeting)
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
                categoriesLine(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp), meeting
                )
        }
    }
}

@Composable
private fun categoriesLine(modifier: Modifier, meeting: ShortMeetingModel) {
    Surface(
        modifier,
        MaterialTheme.shapes.medium,
        MaterialTheme.colorScheme.background,
        border = BorderStroke(3.dp, ThemeExtra.colors.borderColor)
    ) {
        Row(
            Modifier,
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            AsyncImage(
                meeting.category.emoji.path,
                stringResource(R.string.next_button),
                Modifier
                    .padding(6.dp)
                    .size(20.dp),
                painterResource(R.drawable.cinema)
            )
            if (meeting.condition == ConditionType.MEMBER_PAY)
                AsyncImage(
                    meeting.category.emoji.path,
                    stringResource(R.string.next_button),
                    Modifier
                        .padding(6.dp)
                        .size(20.dp),
                    painterResource(R.drawable.cinema)
                )
        }
    }
}