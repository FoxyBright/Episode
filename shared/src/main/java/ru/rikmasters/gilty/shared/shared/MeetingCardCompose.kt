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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.White
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
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.theme.Gradients.gray
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun MeetingCardTodayPreview() {
    GiltyTheme {
        MeetingCard(
            DemoMeetingModel,
            Modifier.padding(20.dp),
            true
        )
    }
}

@Preview
@Composable
private fun MeetingCardPreview() {
    GiltyTheme {
        MeetingCard(
            DemoMeetingModel,
            Modifier.padding(20.dp)
        )
    }
}

@Preview
@Composable
private fun MeetingCategoryCardPreview() {
    GiltyTheme {
        MeetingCategoryCard(
            DemoMeetingModel,
            Modifier.padding(20.dp), old = false
        ) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCard(
    meeting: MeetingModel,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier, shape = shapes.large,
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
                .offset(-(26).dp, 10.dp)
                .width(180.dp)
        ) {
            AsyncImage(
                meeting.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(CircleShape)
                    .size(135.dp),
                placeholder = painterResource(R.drawable.ic_image_box),
                contentScale = ContentScale.Crop
            )
            val countDp = if(
                meeting.condition != MEMBER_PAY
            ) 32 else 8
            if(!today)
                categoriesListCard(
                    Modifier
                        .align(TopEnd)
                        .padding(top = 10.dp, end = countDp.dp),
                    meeting, true
                )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCategoryCard(
    meeting: MeetingModel,
    modifier: Modifier = Modifier,
    today: Boolean = false,
    old: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val color = meeting.category.color
    Card(
        { onClick?.let { it() } },
        modifier, shape = shapes.large,
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
                when {
                    old -> gray()
                    meeting.isOnline -> green()
                    else -> listOf(color, color)
                }, today
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
                            when {
                                old -> gray()
                                meeting.isOnline -> listOf(
                                    colorScheme.secondary,
                                    colorScheme.secondary
                                )
                    
                                else -> listOf(color, color)
                            }
                        ), CircleShape
                    )
                    .size(135.dp), Center
            ) {
                Text(
                    meeting.category.display,
                    Modifier, White,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
            val countDp = if(
                meeting.condition != MEMBER_PAY
            ) 32 else 8
            categoriesListCard(
                Modifier
                    .align(TopEnd)
                    .padding(
                        top = 10.dp,
                        end = countDp.dp
                    ), meeting, (true)
            )
        }
    }
}