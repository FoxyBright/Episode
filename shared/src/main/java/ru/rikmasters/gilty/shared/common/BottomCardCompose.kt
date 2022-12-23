package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
fun CardButtonPreview() {
    GiltyTheme {
        CardButton(
            Modifier,
            stringResource(R.string.meeting_respond),
            false, ic_heart
        )
    }
}


@Preview
@Composable
fun MeetingStatesPreview() {
    GiltyTheme {
        MeetingStates(
            Modifier,
            DemoMeetingModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    online: Boolean,
    icon: Int,
    onClick: (() -> Unit)? = null,
) {
    val color = if(online)
        colorScheme.secondary
    else colorScheme.primary
    Card(
        { onClick?.let { it() } },
        modifier, (true),
        shapes.extraLarge,
        cardColors(colors.grayButton)
    ) {
        Box(Modifier.fillMaxWidth(), Center) {
            Row(
                Modifier.padding(18.dp, 10.dp),
                Start, CenterVertically
            ) {
                Image(
                    painterResource(icon), (null),
                    Modifier.padding(end = 2.dp),
                    colorFilter = ColorFilter.tint(color)
                )
                Text(
                    text, Modifier, color,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MeetingStates(modifier: Modifier, meet: MeetingModel, small: Boolean = false) {
    val today = todayControl(meet.dateTime)
    Box(modifier) {
        Row(Modifier.align(Alignment.CenterEnd)) {
            Column(horizontalAlignment = Alignment.End) {
                DateTimeCard(
                    meet.dateTime,
                    if(meet.isOnline) Gradients.green()
                    else Gradients.red(), today, Modifier,
                    if(small) typography.displaySmall
                    else typography.labelSmall
                )
                if(!today && meet.condition
                    == ConditionType.MEMBER_PAY
                ) Icons(
                    Modifier.padding(top = 8.dp), meet,
                    if(small) 10.dp else 20.dp
                )
            }
            if(today || meet.condition
                != ConditionType.MEMBER_PAY
            ) Icons(
                Modifier.padding(start = 4.dp), meet,
                if(small) 10.dp else 20.dp
            )
        }
    }
}

@Composable
private fun Icons(
    modifier: Modifier,
    meet: MeetingModel,
    imageSize: Dp = 20.dp
) {
    categoriesListCard(
        modifier,
        meet, (false),
        imageSize
    )
}

@Composable
fun MeetingCardCompose(
    meet: MeetingModel,
    modifier: Modifier = Modifier,
    onSelect: ((DirectionType) -> Unit)? = null,
) {
    Card(
        modifier, shapes.large,
        cardColors(Transparent)
    ) {
        Box {
            AsyncImage(
                meet.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = Crop
            )
            MeetBottom(
                Modifier
                    .align(BottomCenter)
                    .offset(y = 18.dp), meet
            ) { onSelect?.let { c -> c(it) } }
        }
    }
}

@Composable
fun MeetBottom(
    modifier: Modifier,
    meet: MeetingModel,
    onSelect: (DirectionType) -> Unit
) {
    Box(modifier) {
        Image(
            painterResource(
                if(isSystemInDarkTheme())
                    ic_back_rect_dark
                else ic_back_rect
            ), (null), Modifier
                .fillMaxWidth()
                .align(BottomCenter),
            contentScale = FillWidth
        )
        Card(
            Modifier
                .align(BottomCenter)
                .wrapContentHeight()
                .offset(y = -(18).dp),
            shapes.large,
            cardColors(colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    SpaceBetween
                ) {
                    Text(
                        meet.title, Modifier.weight(1f),
                        colorScheme.tertiary,
                        style = typography.labelLarge
                    ); MeetingStates(Modifier.weight(1f), meet)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    CardButton(
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        stringResource(R.string.not_interesting),
                        meet.isOnline, ic_cancel
                    ) { onSelect(LEFT) }
                    CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_respond),
                        meet.isOnline, ic_heart
                    ) { onSelect(RIGHT) }
                }
            }
        }
    }
}