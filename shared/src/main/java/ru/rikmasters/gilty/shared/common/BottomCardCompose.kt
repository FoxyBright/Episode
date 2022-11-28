package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
fun CardButtonPreview() {
    GiltyTheme {
        CardButton(
            Modifier,
            stringResource(R.string.meeting_respond),
            false, R.drawable.ic_heart
        )
    }
}


@Preview
@Composable
fun MeetingStatesPreview() {
    GiltyTheme {
        MeetingStates(
            Modifier,
            DemoFullMeetingModel
        )
    }
}

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    online: Boolean,
    icon: Int,
    onClick: (() -> Unit)? = null,
) {
    val color = if (online)
        colorScheme.secondary
    else colorScheme.primary
    Button(
        { onClick?.let { it() } },
        modifier,
        shape = MaterialTheme.shapes.extraLarge,
        contentPadding = PaddingValues(12.dp, 10.dp),
        colors = ButtonDefaults.buttonColors(ThemeExtra.colors.grayButton)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(icon), (null),
                Modifier.padding(end = 2.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Text(
                text, Modifier.padding(top = 2.dp), color,
                style = MaterialTheme
                    .typography.labelSmall
                    .copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun MeetingStates(modifier: Modifier, meet: FullMeetingModel, small: Boolean = false) {
    val today = todayControl(meet.dateTime)
    Box(modifier) {
        Row(Modifier.align(Alignment.CenterEnd)) {
            Column(horizontalAlignment = Alignment.End) {
                DateTimeCard(
                    meet.dateTime,
                    if (meet.isOnline) Gradients.green()
                    else Gradients.red(), today, Modifier,
                    if (small) MaterialTheme.typography.titleSmall
                    else MaterialTheme.typography.labelSmall
                )
                if (!today && meet.condition == ConditionType.MEMBER_PAY)
                    Icons(Modifier.padding(top = 8.dp), meet)
            }
            if (today || meet.condition != ConditionType.MEMBER_PAY)
                Icons(Modifier.padding(start = 4.dp), meet)
        }
    }
}

@Composable
private fun Icons(modifier: Modifier, meet: FullMeetingModel) {
    categoriesListCard(modifier, meet, (false))
}

@Composable
fun MeetingCardCompose(
    meet: FullMeetingModel,
    modifier: Modifier = Modifier,
    onSelect: ((DirectionType) -> Unit)? = null,
) {
    Card(
        modifier,
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(Color.Transparent)
    ) {
        Box {
            AsyncImage(
                meet.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = ContentScale.Crop
            )
            MeetBottom(
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 18.dp), meet
            ) { onSelect?.let { c -> c(it) } }
        }
    }
}

@Composable
fun MeetBottom(
    modifier: Modifier,
    meet: FullMeetingModel,
    onSelect: (DirectionType) -> Unit
) {
    Box(modifier) {
        Image(
            painterResource(
                if (isSystemInDarkTheme())
                    R.drawable.ic_back_rect_dark
                else R.drawable.ic_back_rect
            ), (null), Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )
        Card(
            Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .offset(y = -(18).dp),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.Absolute.SpaceBetween) {
                    Text(
                        meet.title, Modifier.weight(1f),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.labelLarge
                    ); MeetingStates(Modifier.weight(1f), meet)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    CardButton(
                        Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        stringResource(R.string.not_interesting),
                        meet.isOnline, R.drawable.ic_cancel
                    ) { onSelect(DirectionType.LEFT) }
                    CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_respond),
                        meet.isOnline, R.drawable.ic_heart
                    ) { onSelect(DirectionType.RIGHT) }
                }
            }
        }
    }
}