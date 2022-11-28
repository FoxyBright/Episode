package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_shared
import ru.rikmasters.gilty.shared.common.MeetingStates
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
fun CompleteContent() {
    GiltyTheme {
        CompleteContent(DemoFullMeetingModel)
    }
}

interface CompleteCallBack {
    fun onShare() {}
    fun onClose() {}
}

@Composable
fun CompleteContent(
    meeting: FullMeetingModel,
    modifier: Modifier = Modifier,
    callback: CompleteCallBack? = null
) {
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        ActionBar("Meet создан")
        PhoneContent(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(top = 34.dp)
                .padding(horizontal = 60.dp)
        ) { MeetingCard(meeting) }
        Box(Modifier.fillMaxSize()) {
            Buttons(
                Modifier.align(Alignment.BottomCenter),
                { callback?.onShare() }) { callback?.onClose() }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    onShare: () -> Unit,
    onClose: () -> Unit
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
            icon = ic_shared
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
    meeting: FullMeetingModel,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Box {
            AsyncImage(
                meeting.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.94f),
                contentScale = ContentScale.Crop
            )
            MeetBottom(
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 18.dp), meeting
            )
        }
    }
}

@Composable
private fun PhoneContent(
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)?
) {
    Box(modifier.fillMaxSize(), Alignment.Center) {
        Image(
            painterResource(
                if (isSystemInDarkTheme())
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
    meet: FullMeetingModel,
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
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        meet.title, Modifier.weight(1f),
                        colorScheme.tertiary,
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    ); MeetingStates(Modifier.weight(1f), meet, true)
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
                        R.drawable.ic_cancel
                    )
                    CardButton(
                        Modifier.weight(1f),
                        stringResource(R.string.meeting_respond),
                        R.drawable.ic_heart
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
) {
    Card(
        modifier, MaterialTheme.shapes.extraLarge,
        CardDefaults.cardColors(ThemeExtra.colors.grayButton)
    ) {
        Row(
            Modifier.padding(12.dp, 10.dp),
            Arrangement.Start,
            Alignment.CenterVertically
        ) {
            Icon(
                painterResource(icon), (null),
                Modifier.size(8.dp),
                colorScheme.primary
            )
            Text(
                text, Modifier.padding(top = 2.dp, start = 2.dp),
                colorScheme.primary,
                style = typography.displaySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}