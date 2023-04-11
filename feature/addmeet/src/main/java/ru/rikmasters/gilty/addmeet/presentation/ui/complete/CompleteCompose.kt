package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_shared
import ru.rikmasters.gilty.shared.common.MeetingStates
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
fun CompleteContent() {
    GiltyTheme {
        CompleteContent(DemoMeetingModel, (false))
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
                .padding(horizontal = 60.dp)
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
                .padding(top = 28.dp),
            stringResource(R.string.meeting_shared_button),
            icon = ic_shared, online = online
        ) { onShare() }
        Text(
            stringResource(R.string.meeting_close_button), Modifier
                .clickable(
                    MutableInteractionSource(), (null)
                ) { onClose() }
                .padding(top = 12.dp, bottom = 28.dp),
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
            AsyncImage(
                meeting.organizer?.avatar?.thumbnail?.url,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = ContentScale.Crop
            )
            MeetBottom(
                Modifier
                    .align(Alignment.BottomCenter)
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
            painterResource(
                if(isSystemInDarkTheme())
                    R.drawable.ic_new_meet_back_dark
                else R.drawable.ic_new_meet_back
            ), (null), Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.54f)
        ) { content?.invoke() }
    }
}

@Composable
fun MeetBottom(
    modifier: Modifier,
    meet: MeetingModel,
    online: Boolean,
) {
    Box(modifier) {
        Image(
            painterResource(
                if(isSystemInDarkTheme())
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
            shapes.large,
            cardColors(colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        meet.title, Modifier.weight(1f),
                        colorScheme.tertiary,
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    ); MeetingStates(
                    Modifier.weight(1f),
                    meet, true
                )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    CardButton(
                        Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        stringResource(R.string.not_interesting),
                        R.drawable.cross, online,
                    )
                    CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_respond),
                        R.drawable.ic_heart, online,
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
    online: Boolean,
    betweenDistance: Dp = 2.dp,
) {
    val color = if(online) colorScheme.secondary
    else colorScheme.primary
    Box(
        modifier.background(
            colors.grayButton,
            shapes.extraLarge
        ), Center
    ) {
        Row(
            Modifier.padding(6.dp),
            Start, CenterVertically
        ) {
            Icon(
                painterResource(icon), (null),
                Modifier.size(8.dp), color
            )
            Text(
                text, Modifier.padding(start = betweenDistance),
                color, style = typography.titleSmall,
                maxLines = 1
            )
        }
    }
}