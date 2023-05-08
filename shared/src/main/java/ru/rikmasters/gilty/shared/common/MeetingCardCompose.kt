package ru.rikmasters.gilty.shared.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.MeetCardType.MEET
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.AnimatedImage
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

private val meeting = DemoMeetingModel

@Preview
@Composable
private fun MeetingCardPreview() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    colorScheme.background
                )
        ) {
            MeetCard(
                Modifier.padding(16.dp),
                MEET, (true), meeting, (0f), (true)
            )
        }
    }
}

@Preview
@Composable
private fun EmptyMeetCardPreview() {
    GiltyTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    colorScheme.background
                )
        ) { EmptyCard(Modifier.padding(16.dp)) }
    }
}

@Preview
@Composable
private fun CardButtonPreview() {
    GiltyTheme {
        CardButton(
            Modifier.width(150.dp),
            stringResource(R.string.meeting_respond),
            meeting.category.color, R.drawable.ic_heart
        )
    }
}

@Preview
@Composable
private fun MeetingStatesPreview() {
    GiltyTheme {
        MeetingStates(
            Modifier, meeting
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    icon: Int? = null,
    background: Color = colors.meetButtonColors,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier, (true),
        shapes.extraLarge,
        cardColors(background)
    ) {
        Box(Modifier.fillMaxWidth(), Center) {
            Row(
                Modifier.padding(10.dp),
                Start, CenterVertically
            ) {
                icon?.let {
                    Image(
                        painterResource(it), (null),
                        Modifier.padding(end = 6.dp),
                        colorFilter = tint(color)
                    )
                }
                Text(
                    text, Modifier, color,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
        }
    }
}

@Composable
fun MeetingStates(
    modifier: Modifier,
    meet: MeetingModel,
    small: Boolean = false,
) {
    val today = todayControl(meet.datetime)
    
    val dtColor = if(meet.isOnline)
        listOf(
            colorScheme.secondary,
            colorScheme.secondary
        ) else listOf(
        meet.category.color,
        meet.category.color
    )
    
    val dtTextStyle = if(small)
        typography.displaySmall
    else typography.labelSmall
    
    val iconState = !today &&
            meet.condition == MEMBER_PAY
    
    val iconsSize = if(small) 10.dp else 16.dp
    
    Box(modifier) {
        Row(Modifier.align(CenterEnd)) {
            Column(Modifier, Top, End) {
                DateTimeCard(
                    meet.datetime, dtColor,
                    today, Modifier, dtTextStyle
                )
                if(iconState)
                    CategoriesListCard(
                        Modifier.padding(top = 8.dp),
                        meet, (false), iconsSize
                    )
            }
            if(!iconState) CategoriesListCard(
                Modifier.padding(start = 4.dp),
                meet, (false), iconsSize
            )
        }
    }
}

@Composable
fun EmptyCard(
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    onRepeatClick: (() -> Unit)? = null,
) {
    Card(
        modifier, shapes.large,
        cardColors(Transparent)
    ) {
        Box {
            AnimatedImage(
                if(isSystemInDarkTheme())
                    R.raw.corgi_night else
                    R.raw.corgi,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.94f)
                    .background(
                        colorScheme.primaryContainer,
                        shapes.large
                    ),
                contentScale = Fit
            )
            Box(
                Modifier
                    .wrapContentHeight()
                    .align(BottomCenter)
                    .offset(y = -(18).dp)
            ) {
                ShadowBack(Modifier.offset(y = (-30).dp))
                Card(
                    Modifier,
                    shapes.large,
                    cardColors(
                        colorScheme.primaryContainer
                    ),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.meeting_empty_meet_label),
                            Modifier.fillMaxWidth(),
                            colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                            style = typography.labelLarge
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 22.dp)
                        ) {
                            CardButton(
                                Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                stringResource(R.string.meeting_repeat_button),
                                colorScheme.primary
                            ) { onRepeatClick?.let { it() } }
                            CardButton(
                                Modifier.weight(1f),
                                stringResource(R.string.meeting_get_more_button),
                                colorScheme.primary
                            ) { onMoreClick?.let { it() } }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShadowBack(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                verticalGradient(
                    listOf(
                        colorScheme.primaryContainer,
                        colors.meetCardShadow
                    )
                )
            )
    )
}

enum class MeetCardType { EMPTY, MEET }

@Composable
fun MeetCard(
    modifier: Modifier = Modifier,
    type: MeetCardType,
    stack: Boolean = true,
    meet: MeetingModel? = null,
    offset: Float = 0f,
    hasFilters: Boolean = false,
    onMoreClick: (() -> Unit)? = null,
    onRepeatClick: (() -> Unit)? = null,
    onSelect: ((DirectionType) -> Unit)? = null,
) {
    Box(modifier) {
        if(type == MEET) Image(
            painterResource(
                if(isSystemInDarkTheme())
                    R.drawable.ic_back_rect_dark
                else R.drawable.ic_back_rect
            ), (null), Modifier
                .align(BottomCenter)
                .offset(
                    y = animateDpAsState(
                        if(stack) 16.dp
                        else 0.dp,
                        tween(800)
                    ).value
                )
        )
        Column(
            Modifier
                .fillMaxSize()
                .clip(shapes.large)
        ) {
            if(type == MEET) MeetCard(
                meet, offset, onSelect
            ) else EmptyCard(
                hasFilters, onMoreClick,
                onRepeatClick
            )
        }
    }
}

@Composable
private fun EmptyCard(
    hasFilters: Boolean,
    onMoreClick: (() -> Unit)?,
    onRepeatClick: (() -> Unit)?,
) {
    Column {
        EmptyTop(
            Modifier
                .weight(1f)
                .offset(y = 24.dp)
        )
        EmptyBottom(
            Modifier.fillMaxHeight(0.28f),
            hasFilters,
            { onRepeatClick?.let { it() } }
        ) { onMoreClick?.let { it() } }
    }
}

@Composable
private fun MeetCard(
    meet: MeetingModel?,
    offset: Float,
    onSelect: ((DirectionType) -> Unit)?,
) {
    meet?.let {
        Box {
            MeetTop(
                it.organizer?.avatar,
                Modifier
                    .clip(ThemeExtra.shapes.bigTopShapes)
                    .fillMaxSize()
                    .offset(y = (-24).dp)
            )
            MeetBottom(
                it, offset, Modifier.align(BottomCenter)
            ) { type -> onSelect?.let { it(type) } }
        }
    }
}

@Composable
private fun EmptyTop(
    modifier: Modifier,
) {
    AnimatedImage(
        if(isSystemInDarkTheme())
            R.raw.corgi_night else
            R.raw.corgi,
        modifier
            .fillMaxSize()
            .background(
                colorScheme.primaryContainer,
                ThemeExtra.shapes.bigTopShapes
            ),
        contentScale = Crop
    )
}

@Composable
private fun MeetTop(
    avatar: AvatarModel?,
    modifier: Modifier,
) {
    GCashedImage(
        avatar?.thumbnail?.url,
        modifier
            .fillMaxSize()
            .padding(horizontal = 1.dp),
        contentScale = Crop
    )
}

@Composable
private fun MeetBottom(
    meet: MeetingModel,
    offset: Float,
    modifier: Modifier = Modifier,
    onSelect: ((DirectionType) -> Unit)? = null,
) {
    val leftSwipe = offset < -(50)
    val rightSwipe = offset > 50
    val swipe = leftSwipe || rightSwipe
    val back = colors.meetButtonColors
    val color = if(meet.isOnline)
        colorScheme.secondary
    else meet.category.color
    val textColor: Color
    val backColor: Color
    if(swipe) {
        textColor = White
        backColor = color
    } else {
        textColor = color
        backColor = back
    }
    Box(
        modifier
            .background(
                colorScheme.primaryContainer,
                ThemeExtra.shapes.bigShapes
            )
    ) {
        Column(
            Modifier.padding(16.dp),
            Arrangement.SpaceBetween
        ) {
            MeetInfo(meet)
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
                    color = if(leftSwipe)
                        textColor else color,
                    icon = R.drawable.ic_cancel,
                    background = if(leftSwipe)
                        backColor else back
                ) { onSelect?.let { it(LEFT) } }
                CardButton(
                    Modifier.weight(1f),
                    stringResource(R.string.meeting_respond),
                    color = if(rightSwipe)
                        textColor else color,
                    icon = R.drawable.ic_heart,
                    background = if(rightSwipe)
                        backColor else back
                ) { onSelect?.let { it(RIGHT) } }
            }
        }
    }
}

@Composable
private fun EmptyBottom(
    modifier: Modifier,
    hasFilters: Boolean,
    onRepeatClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxWidth()) {
        ShadowBack(Modifier.offset(y = (-30).dp))
        Box(
            Modifier.background(
                colorScheme.primaryContainer,
                ThemeExtra.shapes.bigShapes
            )
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.meeting_empty_meet_label),
                    Modifier.fillMaxWidth(),
                    colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    style = typography.labelLarge
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    CardButton(
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        stringResource(R.string.meeting_repeat_button),
                        colorScheme.primary
                    ) { onRepeatClick?.let { it() } }
                    if(hasFilters) CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_get_more_button),
                        colorScheme.primary
                    ) { onMoreClick?.let { it() } }
                }
            }
        }
    }
}

@Composable
private fun MeetInfo(meet: MeetingModel) {
    Row(
        Modifier.fillMaxWidth(),
        SpaceBetween
    ) {
        Text(
            meet.title, Modifier.weight(1f),
            colorScheme.tertiary,
            style = typography.labelLarge.copy(
                lineHeight = 30.sp
            )
        )
        MeetingStates(Modifier.weight(1f), meet)
    }
}