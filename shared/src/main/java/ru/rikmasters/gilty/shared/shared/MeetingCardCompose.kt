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
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoriesListCard
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.FREE
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
            DemoMeetingModel.copy(condition = FREE),
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
            Modifier.padding(20.dp),
            old = true
        ) {}
    }
}

@Preview
@Composable
private fun MeetingCategoryCardTodayPreview() {
    GiltyTheme {
        MeetingCategoryCard(
            DemoMeetingModel.copy(condition = FREE),
            Modifier.padding(20.dp),
            today = true,
            old = false
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
    ) {
        Text(
            meeting.title,
            Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
        )
        Row(Modifier.padding(start = 14.dp)) {
            DateTimeCard(
                meeting.dateTime,
                green(), today
            )
            if(today) CategoriesListCard(
                Modifier.padding(start = 4.dp),
                meeting, (true)
            )
        }
        Box(
            Modifier
                .offset(-(26).dp, 10.dp)
                .width(156.dp)
        ) {
            AsyncImage(
                meeting.organizer?.avatar?.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(CircleShape)
                    .size(135.dp),
                contentScale = Crop
            )
            if(!today) CategoriesListCard(
                Modifier
                    .offset(18.dp)
                    .align(TopEnd)
                    .padding(
                        top = 10.dp, end = if(
                            meeting.condition
                            != MEMBER_PAY
                        ) 28.dp else 0.dp
                    ),
                meeting, (true)
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
    ) {
        Text(
            meeting.title, Modifier
                .width(150.dp)
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Row(Modifier.padding(start = 14.dp)) {
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
                .width(156.dp)
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
                    .size(126.dp), Center
            ) {
                Text(
                    meeting.category.name,
                    Modifier, White,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
            CategoriesListCard(
                Modifier
                    .align(TopEnd)
                    .padding(
                        top = 10.dp, end = if(
                            meeting.condition
                            != MEMBER_PAY
                        ) 28.dp else 0.dp
                    ),
                meeting, (true)
            )
        }
    }
}