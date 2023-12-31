package ru.rikmasters.gilty.translation.viewer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.webrtc.EglBase
import org.webrtc.VideoTrack
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.components.ChatItem
import ru.rikmasters.gilty.translation.shared.components.CloseButton
import ru.rikmasters.gilty.translation.shared.components.MembersCountItem
import ru.rikmasters.gilty.translation.shared.components.StreamerItem
import ru.rikmasters.gilty.translation.shared.components.TimerItem
import ru.rikmasters.gilty.translation.viewer.model.ViewerCustomHUD

@Composable
fun TranslationViewerContent(
    customHUDState: ViewerCustomHUD?,
    videoTrack: VideoTrack?,
    eglBaseContext: EglBase.Context,
    membersCount: Int,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    onChatClicked: () -> Unit,
    isTimerHighlighted: Boolean,
    addTimerString: String,
    onMembersCountClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onToChatPressed: () -> Unit,
    isPortrait: Boolean,
    initialize: (VideoTextureViewRenderer) -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ThemeExtra.colors.preDarkColor,
                            Color(0xFF070707)
                        )
                    )
                )
        ) {
            Box(
                modifier = if (customHUDState == null) Modifier.weight(1f) else Modifier.fillMaxSize()
            ) {
                Camera(
                    shape = if (customHUDState == null) RoundedCornerShape(14.dp) else RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp),
                    videoTrack = videoTrack,
                    eglBaseContext = eglBaseContext,
                    modifier = Modifier.matchParentSize(),
                    initialize = initialize
                )
                if (customHUDState == null) {
                    if (isPortrait) {
                        OnCameraTopBarPortrait(
                            meetingModel = meetingModel,
                            remainTime = remainTime,
                            isTimerHighlighted = isTimerHighlighted,
                            addTimerString = addTimerString,
                            onCloseClicked = onCloseClicked
                        )
                    } else {
                        OnCameraLandscape(
                            meetingModel = meetingModel,
                            remainTime = remainTime,
                            isTimerHighlighted = isTimerHighlighted,
                            addTimerString = addTimerString,
                            onCloseClicked = onCloseClicked,
                            onChatClicked = onChatClicked,
                            membersCount = membersCount,
                            onMembersCountClicked = onMembersCountClicked
                        )
                    }
                }
                if (customHUDState == ViewerCustomHUD.COMPLETED) {
                    CompletedPortrait(onToChatPressed = onToChatPressed)
                }
                if (customHUDState == ViewerCustomHUD.KICKED) {
                    KickedPortrait(onToChatPressed = onToChatPressed)
                }
            }
            if (customHUDState == null && isPortrait) {
                BottomBarPortrait(
                    onChatClicked = onChatClicked,
                    membersCount = membersCount,
                    onMembersCountClicked = onMembersCountClicked
                )
            }
        }
    }
}

@Composable
fun Camera(
    shape: Shape,
    videoTrack: VideoTrack?,
    eglBaseContext: EglBase.Context,
    modifier: Modifier,
    initialize: (VideoTextureViewRenderer) -> Unit
) {
    Surface(
        shape = shape,
        color = ThemeExtra.colors.preDarkColor,
        modifier = modifier
    ) {
        videoTrack?.let {
            VideoRenderer(
                videoTrack = it,
                eglBaseContext = eglBaseContext,
                initialize = initialize,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun BottomBarPortrait(
    onChatClicked: () -> Unit,
    membersCount: Int,
    onMembersCountClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070707),
                        ThemeExtra.colors.preDarkColor
                    ),
                    startY = 0.7f
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(
                    start = 20.dp,
                    end = 20.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatItem(
                onClick = onChatClicked
            )
            MembersCountItem(
                membersCount = membersCount,
                onClick = onMembersCountClicked
            )
        }
    }
}

@Composable
fun KickedPortrait(
    onToChatPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cry),
                contentDescription = "",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_kicked),
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeExtra.colors.white,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_kicked_text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = ThemeExtra.colors.white,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradientButton(
                text = stringResource(id = R.string.translations_expired_button_to_chat),
                online = true,
                onClick = onToChatPressed
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CompletedPortrait(
    onToChatPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_admiration),
                contentDescription = "",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_completed),
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeExtra.colors.white,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(13.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_completed_text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = ThemeExtra.colors.white,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradientButton(
                text = stringResource(id = R.string.translations_expired_button_to_chat),
                online = true,
                onClick = onToChatPressed
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnCameraTopBarPortrait(
    meetingModel: FullMeetingModel?,
    remainTime: String,
    isTimerHighlighted: Boolean,
    addTimerString: String,
    onCloseClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            meetingModel?.let {
                StreamerItem(meetingModel = meetingModel)
                Spacer(modifier = Modifier.weight(1f))
                TimerItem(
                    time = remainTime,
                    onClick = {},
                    isHighlight = isTimerHighlighted,
                    addTimerTime = addTimerString
                )
                Spacer(modifier = Modifier.width(12.dp))
                CloseButton(onClick = onCloseClicked)
            }
        }
    }
}

@Composable
fun OnCameraLandscape(
    meetingModel: FullMeetingModel?,
    remainTime: String,
    isTimerHighlighted: Boolean,
    addTimerString: String,
    onCloseClicked: () -> Unit,
    onChatClicked: () -> Unit,
    membersCount: Int,
    onMembersCountClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                meetingModel?.let {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        StreamerItem(meetingModel = meetingModel)
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TimerItem(
                            time = remainTime,
                            onClick = {},
                            isHighlight = isTimerHighlighted,
                            addTimerTime = addTimerString
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        CloseButton(onClick = onCloseClicked)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                ChatItem(onClick = onChatClicked)
                Spacer(modifier = Modifier.width(40.dp))
            }
            Row {
                MembersCountItem(
                    membersCount = membersCount,
                    onClick = onMembersCountClicked
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
