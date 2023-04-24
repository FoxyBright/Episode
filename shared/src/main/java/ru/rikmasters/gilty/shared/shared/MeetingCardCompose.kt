package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoriesListCard
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.now
import ru.rikmasters.gilty.shared.common.extentions.todayControl
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
            DemoMeetingModel.copy(
                datetime = now().toString(),
                isOnline = true
            ), Modifier.padding(20.dp)
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
            "",
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
            "userID",
            DemoMeetingModel.copy(
                condition = FREE,
                datetime = now().toString()
            ), Modifier.padding(20.dp),
            old = false
        ) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingCard(
    meeting: MeetingModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val today = todayControl(meeting.datetime)
    Card(
        { onClick?.let { it() } },
        modifier, shape = shapes.large,
        colors = cardColors(colors.meetingCardBackBackground)
    ) {
        Text(
            meeting.tags.first().title, Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            overflow = Ellipsis,
            fontWeight = Bold,
            maxLines = 1
        )
        Row(Modifier.padding(start = 14.dp)) {
            DateTimeCard(
                meeting.datetime,
                if(meeting.isOnline) green()
                else listOf(
                    meeting.category.color,
                    meeting.category.color
                ), today
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
                meeting.organizer?.avatar?.thumbnail?.url,
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
    userId: String,
    meeting: MeetingModel,
    modifier: Modifier = Modifier,
    old: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val today = todayControl(meeting.datetime)
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
            overflow = Ellipsis,
            maxLines = 1
        )
        Row(Modifier.padding(start = 14.dp)) {
            DateTimeCard(
                meeting.datetime,
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
            val user = meeting.organizer
            val hasAvatar = user?.id != null && userId != user.id
            Box(
                Modifier
                    .background(
                        when {
                            old -> colorScheme.onTertiary
                            meeting.isOnline -> colorScheme.secondary
                            else -> color
                        }, CircleShape
                    )
                    .size(126.dp), Center
            ) {
                if(hasAvatar) Image(
                    rememberAsyncImagePainter(
                        user?.avatar?.thumbnail?.url
                    ), (null), Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = Crop
                )
                if(old && hasAvatar) Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            colors.meetingTransparencyShape,
                            CircleShape
                        )
                )
                if(!hasAvatar) Text(
                    meeting.category.name,
                    Modifier.padding(horizontal = 16.dp), White,
                    style = typography.labelSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = SemiBold,
                    overflow = Ellipsis,
                    maxLines = 3
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