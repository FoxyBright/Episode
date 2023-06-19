package ru.rikmasters.gilty.translation.streamer.ui

import android.view.SurfaceHolder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.components.CameraItem
import ru.rikmasters.gilty.translation.shared.components.CameraView
import ru.rikmasters.gilty.translation.shared.components.ChangeFacingItem
import ru.rikmasters.gilty.translation.shared.components.ChatItem
import ru.rikmasters.gilty.translation.shared.components.CloseButton
import ru.rikmasters.gilty.translation.shared.components.MembersCountItem
import ru.rikmasters.gilty.translation.shared.components.MicrophoneItem
import ru.rikmasters.gilty.translation.shared.components.StreamerItem
import ru.rikmasters.gilty.translation.shared.components.TimerItem
import ru.rikmasters.gilty.translation.streamer.model.StreamerCustomHUD

@Composable
@Preview
fun F() {
    ExpiredPortrait(
        onTimerClicked = {},
        meetingModel = null,
        remainTime = "00:00",
        isHighlightTimer = false,
        timerAddTime = "",
        onCloseClicked = {},
        membersCount = 1
    )
}

@Composable
fun TranslationStreamerContent(
    initCamera: (OpenGlView) -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    isPortrait: Boolean,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    isHighlightTimer: Boolean,
    addTimerString: String,
    onChatClicked: () -> Unit,
    onMembersClicked: () -> Unit,
    membersCount: Int,
    onTimerClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    bsOpened: Boolean,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    changeFacing: () -> Unit,
    onToChatPressed: () -> Unit,
    customHUDState: StreamerCustomHUD?
) {
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
            modifier = Modifier
                .background(
                    color = ThemeExtra.colors.preDarkColor
                )
                .weight(1f)
        ) {
            Surface(shape = if (isPortrait) RoundedCornerShape(14.dp) else RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp
            )) {
                CameraView(
                    initCamera = initCamera,
                    surfaceHolderCallback = surfaceHolderCallback,
                    modifier = Modifier.matchParentSize()
                )
            }
            if (customHUDState == StreamerCustomHUD.EXPIRED) {
                ExpiredPortrait(
                    onTimerClicked = onTimerClicked,
                    meetingModel = meetingModel,
                    remainTime = remainTime,
                    isHighlightTimer = isHighlightTimer,
                    timerAddTime = addTimerString,
                    onCloseClicked = onCloseClicked,
                    membersCount = membersCount
                )
            }
            if (customHUDState == null) {
                if (isPortrait) {
                    OnStreamTopBarPortrait(
                        meetingModel = meetingModel,
                        remainTime = remainTime,
                        onChatClicked = onChatClicked,
                        onMembersClicked = onMembersClicked,
                        membersCount = membersCount,
                        onTimerClicked = onTimerClicked,
                        isHighlightTimer = isHighlightTimer,
                        timerAddTime = addTimerString,
                        onCloseClicked = onCloseClicked
                    )
                } else {
                    OnStreamLandscape(
                        meetingModel = meetingModel,
                        onChatClicked = onChatClicked,
                        membersCount = membersCount,
                        remainTime = remainTime,
                        onTimerClicked = onTimerClicked,
                        isHighlightTimer = isHighlightTimer,
                        timerAddTime = addTimerString,
                        onMembersClicked = onMembersClicked,
                        onCloseClicked = onCloseClicked,
                        bsOpened = bsOpened,
                        cameraEnabled = cameraEnabled,
                        microphoneEnabled = microphoneEnabled,
                        onCameraClicked = onCameraClicked,
                        onMicrophoneClicked = onMicrophoneClicked,
                        changeFacing = changeFacing
                    )
                }

            }
            if (customHUDState == StreamerCustomHUD.COMPLETED) {
                CompletedPortrait(onToChatPressed = onToChatPressed)
            }
        }
        if (customHUDState == null && isPortrait) {
            BottomBarPortrait(
                cameraEnabled = cameraEnabled,
                microphoneEnabled = microphoneEnabled,
                onCameraClicked = onCameraClicked,
                onMicrophoneClicked = onMicrophoneClicked,
                changeFacing = changeFacing
            )
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
fun BottomBarPortrait(
    cameraEnabled: Boolean,
    onCameraClicked: () -> Unit,
    microphoneEnabled: Boolean,
    onMicrophoneClicked: () -> Unit,
    changeFacing: () -> Unit
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
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            CameraItem(
                enabled = cameraEnabled,
                onClick = onCameraClicked
            )
            MicrophoneItem(
                enabled = microphoneEnabled,
                onClick = onMicrophoneClicked
            )
            ChangeFacingItem(
                onClick = changeFacing
            )
        }
    }
}

@Composable
fun ExpiredPortrait(
    onTimerClicked: () -> Unit,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    isHighlightTimer: Boolean,
    timerAddTime: String,
    onCloseClicked: () -> Unit,
    membersCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                        onClick = onTimerClicked,
                        isHighlight = isHighlightTimer,
                        addTimerTime = timerAddTime
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    CloseButton(onClick = onCloseClicked)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChatItem(onClick = {  })
                MembersCountItem(
                    membersCount = membersCount,
                    onClick = {  }
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bad),
                contentDescription = "",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_expired_time),
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
                text = stringResource(id = R.string.translations_expired_time_append),
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
                text = stringResource(id = R.string.translations_expired_button_turn_on),
                online = true,
                onClick = onTimerClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnStreamLandscape(
    meetingModel: FullMeetingModel?,
    onChatClicked: () -> Unit,
    membersCount: Int,
    remainTime: String,
    onTimerClicked: () -> Unit,
    isHighlightTimer: Boolean,
    timerAddTime: String,
    onMembersClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    bsOpened: Boolean,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    changeFacing: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            meetingModel?.let {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    StreamerItem(meetingModel = meetingModel)
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    TimerItem(
                        time = remainTime,
                        onClick = onTimerClicked,
                        isHighlight = isHighlightTimer,
                        addTimerTime = timerAddTime
                    )
                }
                AnimatedVisibility(
                    visible = !bsOpened,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        CloseButton(onClick = onCloseClicked)
                    }
                }
                AnimatedVisibility(visible = bsOpened) {
                    Spacer(
                        modifier = Modifier
                            .width(29.dp)
                            .weight(1f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        AnimatedVisibility(
            visible = !bsOpened,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraItem(
                    enabled = cameraEnabled,
                    onClick = onCameraClicked,
                    roundedBackground = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                MicrophoneItem(
                    enabled = microphoneEnabled,
                    onClick = onMicrophoneClicked,
                    roundedBackground = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                ChangeFacingItem(
                    onClick = changeFacing,
                    roundedBackground = true
                )
                Spacer(modifier = Modifier.weight(1f))
                ChatItem(onClick = onChatClicked)
                Spacer(modifier = Modifier.width(40.dp))
                MembersCountItem(
                    membersCount = membersCount,
                    onClick = onMembersClicked
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun OnStreamTopBarPortrait(
    meetingModel: FullMeetingModel?,
    onChatClicked: () -> Unit,
    membersCount: Int,
    remainTime: String,
    onTimerClicked: () -> Unit,
    isHighlightTimer: Boolean,
    timerAddTime: String,
    onMembersClicked: () -> Unit,
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
                    onClick = onTimerClicked,
                    isHighlight = isHighlightTimer,
                    addTimerTime = timerAddTime
                )
                Spacer(modifier = Modifier.width(12.dp))
                CloseButton(onClick = onCloseClicked)
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatItem(onClick = onChatClicked)
            MembersCountItem(
                membersCount = membersCount,
                onClick = onMembersClicked
            )
        }
    }
}