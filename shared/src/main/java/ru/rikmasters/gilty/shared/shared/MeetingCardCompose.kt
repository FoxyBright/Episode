package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.categoriesListCard
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.Gradients.gray
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun MeetingCardTodayPreview() {
    MeetingCard(DemoFullMeetingModel, Modifier.padding(20.dp), true) {}
}

@Preview
@Composable
private fun MeetingCardPreview() {
    MeetingCard(DemoFullMeetingModel, Modifier.padding(20.dp)) {}
}

@Preview
@Composable
private fun MeetingCategoryCardPreview() {
    MeetingCategoryCard(DemoFullMeetingModel, Modifier.padding(20.dp)) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCard(
    meeting: FullMeetingModel,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick,
        modifier,
        shape = shapes.large,
        colors = cardColors(colors.meetingCardBackBackground)
    )
    {
        Text(
            meeting.title,
            Modifier
                .width(180.dp)
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
        )
        Row(
            Modifier
                .padding(start = 14.dp)
        ) {
            DateTimeCard(meeting.dateTime, green(), today)
            if(today)
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
            val countDp = if(
                meeting.condition != MEMBER_PAY
            ) 32 else 8
            if(!today)
                categoriesListCard(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = countDp.dp),
                    meeting, true
                )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCategoryCard(
    meeting: FullMeetingModel,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    old: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier, shape = shapes.large,
        colors = cardColors(colors.meetingCardBackBackground)
    )
    {
        Text(
            meeting.title, Modifier
                .width(180.dp)
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
        )
        Row(
            Modifier
                .padding(start = 14.dp)
        ) {
            DateTimeCard(
                meeting.dateTime,
                if(old) gray()
                else if(meeting.isOnline) green()
                else red(), today
            )
        }
        Box(
            Modifier
                .padding(top = 10.dp, bottom = 4.dp)
                .offset(-(8).dp)
                .width(180.dp)
        ) {
            Box(
                Modifier
                    .background(
                        linearGradient(
                            if(old) gray()
                            else if(meeting.isOnline) green()
                            else red()
                        ), CircleShape
                    )
                    .size(135.dp),
                Alignment.Center
            ) {
                Text(
                    meeting.category.name, Modifier, Color.White,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
            val countDp = if(
                meeting.condition != MEMBER_PAY
            ) 32 else 8
            categoriesListCard(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = countDp.dp), meeting,
                true
            )
        }
    }
}