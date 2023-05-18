package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_shared
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.flyingDollar
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
fun CompleteContent() {
    GiltyTheme {
        CompleteContent(
            DemoMeetingModel,
            false
        )
    }
}

interface CompleteCallBack {
    
    fun onShare() {}
    fun onClose() {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CompleteContent(
    meeting: MeetingModel,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    callback: CompleteCallBack? = null,
) {
    Scaffold(
        modifier,
        topBar = {
            ActionBar(
                stringResource(R.string.add_meet_created),
                Modifier.padding(top = 60.dp)
            )
        },
        bottomBar = {
            Buttons(
                Modifier, isOnline,
                { callback?.onShare() }
            ) { callback?.onClose() }
        }) {
        PhoneContent(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 34.dp)
                .padding(horizontal = 40.dp)
        ) { MeetingCard(meeting, Modifier, isOnline) }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    online: Boolean,
    onShare: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier,
        horizontalAlignment = CenterHorizontally
    ) {
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 35.dp),
            stringResource(R.string.meeting_shared_button),
            icon = ic_shared, online = online
        ) { onShare() }
        Text(
            stringResource(R.string.meeting_close_button), Modifier
                .clickable(
                    MutableInteractionSource(), (null)
                ) { onClose() }
                .padding(vertical = 16.dp),
            colorScheme.tertiary, style = typography.bodyLarge
        )
    }
}

@Composable
private fun MeetingCard(
    meeting: MeetingModel,
    modifier: Modifier = Modifier,
    online: Boolean,
) {
    Box(modifier) {
        Box {
            GCachedImage(
                meeting.organizer?.avatar
                    ?.thumbnail?.url, Modifier
                    .clip(shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = ContentScale.Crop
            )
            MeetBottom(
                Modifier
                    .align(BottomCenter)
                    .offset(y = 18.dp), meeting,
                online
            )
        }
    }
}

@Composable
private fun PhoneContent(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)?,
) {
    Box(modifier.fillMaxSize(), Center) {
        Image(
            painter = painterResource(
                if(isSystemInDarkTheme())
                    R.drawable.ic_new_meet_back_dark
                else R.drawable.ic_new_meet_back
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxWidth(0.82f)
                .fillMaxHeight(0.63f)
        ) { content?.invoke() }
    }
}

@Composable
private fun MeetingStates(
    modifier: Modifier,
    today: Boolean,
    iconState: Boolean,
    meet: MeetingModel,
) {
    Box(modifier) {
        Row(Modifier.align(CenterEnd)) {
            Column(Modifier, Top, End) {
                DateTimeCard(
                    text = meet.datetime,
                    today = today,
                    color = if(meet.isOnline)
                        listOf(
                            colorScheme.secondary,
                            colorScheme.secondary
                        ) else listOf(
                        meet.category.color,
                        meet.category.color
                    )
                )
                if(iconState) CategoriesListCard(
                    modifier = Modifier.padding(
                        top = 6.dp
                    ),
                    meet = meet,
                )
            }
            if(!iconState) CategoriesListCard(
                modifier = Modifier.padding(
                    start = 4.dp
                ),
                meet = meet
            )
        }
    }
}

@Composable
private fun CategoriesListCard(
    modifier: Modifier,
    meet: MeetingModel,
) {
    val dollar = meet
        .condition == MEMBER_PAY
    
    val iconSize = 15.dp
    val pad = 5.dp
    
    Box(
        modifier.background(
            color = colors.miniCategoriesBackground,
            shape = shapes.extraSmall
        )
    ) {
        Row(
            Modifier.padding(pad),
            SpaceEvenly, CenterVertically
        ) {
            GEmojiImage(
                emoji = meet.category.emoji,
                modifier = Modifier
                    .size(iconSize)
            )
            if(dollar) GEmojiImage(
                emoji = flyingDollar,
                modifier = Modifier
                    .padding(start = pad)
                    .size(iconSize)
            )
        }
    }
}

@Composable
private fun DateTimeCard(
    text: String,
    today: Boolean,
    modifier: Modifier = Modifier,
    color: List<Color>,
) {
    Box(
        modifier.background(
            brush = linearGradient(color),
            shape = shapes.extraSmall
        )
    ) {
        Text(
            text = text.format(if(today) "HH:mm" else "dd MMMM"),
            modifier = Modifier.padding(9.dp, 6.dp),
            style = typography.labelSmall
                .copy(White, 10.dp.toSp(), Bold)
        )
    }
}

@Composable
fun MeetBottom(
    modifier: Modifier,
    meet: MeetingModel,
    isOnline: Boolean,
) {
    val today = todayControl(meet.datetime)
    
    val iconState = !today &&
            meet.condition == MEMBER_PAY
    
    Box(modifier) {
        Image(
            painter = painterResource(
                if(isSystemInDarkTheme())
                    R.drawable.ic_back_rect_dark
                else R.drawable.ic_back_rect
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .align(BottomCenter),
            contentScale = FillWidth
        )
        Card(
            modifier = Modifier
                .align(BottomCenter)
                .wrapContentHeight()
                .offset(y = -(10).dp),
            shape = shapes.large,
            colors = cardColors(colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(10.dp)) {
                Row(Modifier.fillMaxWidth(), SpaceBetween) {
                    Text(
                        text = meet.title,
                        modifier = Modifier.weight(1f),
                        style = typography.labelSmall.copy(
                            color = colorScheme.tertiary,
                            fontSize = 14.dp.toSp(),
                            fontWeight = Bold
                        )
                    )
                    MeetingStates(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                bottom = if(!iconState)
                                    12.dp else 0.dp
                            ),
                        today = today,
                        iconState = iconState,
                        meet = meet
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    CardButton(
                        modifier = Modifier
                            .padding(end = 7.dp)
                            .weight(1f),
                        text = stringResource(R.string.not_interesting),
                        icon = R.drawable.cross,
                        size = 12.dp,
                        isOnline = isOnline,
                    )
                    CardButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.meeting_respond),
                        icon = R.drawable.ic_heart,
                        size = 15.dp,
                        isOnline = isOnline,
                    )
                }
            }
        }
    }
}

@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Int,
    size: Dp,
    isOnline: Boolean,
) {
    val color = if(isOnline)
        colorScheme.secondary
    else colorScheme.primary
    
    Box(
        modifier.background(
            color = colors.meetButtonColors,
            shape = shapes.extraLarge
        ), Center
    ) {
        Row(Modifier, Start, CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = 6.dp,
                        end = 2.dp
                    )
                    .size(size),
                tint = color
            )
            Text(
                text = text,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .padding(end = 6.dp),
                style = typography.titleSmall.copy(
                    color = color,
                    fontSize = 10.dp.toSp(),
                    fontWeight = SemiBold
                ),
                maxLines = 1,
            )
        }
    }
}