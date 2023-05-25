package ru.rikmasters.gilty.translation.presentation.ui.content.active

import android.view.SurfaceHolder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.ConnectionStatus
import ru.rikmasters.gilty.translation.presentation.ui.components.*

@Composable
fun TranslationActivePortrait(
    initializeCamera: (OpenGlView) -> Unit,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    onCloseClicked: () -> Unit,
    onChatClicked: () -> Unit,
    membersCount: Int,
    onMembersClicked: () -> Unit,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    changeFacing: () -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    connectionStatus: ConnectionStatus,
    onReconnectCLicked: () -> Unit,
    onTimerClicked: () -> Unit,
    isHighlightTimer: Boolean,
    timerAddTime: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                CameraView(
                    initCamera = initializeCamera,
                    surfaceHolderCallback = surfaceHolderCallback
                )
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
                        .padding(
                            bottom = 10.dp,
                            top = 10.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
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
        when {
            connectionStatus == ConnectionStatus.RECONNECTING -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_reconnecting),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.translations_reconnecting),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.translations_reconnecting_wait),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            connectionStatus == ConnectionStatus.NO_CONNECTION -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_no_connection),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.translations_no_connection),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.translations_no_connection_wait),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ThemeExtra.colors.mainDayGreen,
                        modifier = Modifier.clickable { onReconnectCLicked() }
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            text = stringResource(id = R.string.translations_no_connection_refresh),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeExtra.colors.white
                        )
                    }
                }
            }

            connectionStatus == ConnectionStatus.LOW_CONNECTION -> {
                if (!cameraEnabled && !microphoneEnabled) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(77.dp))
                            GCachedImage(
                                url = meetingModel?.organizer?.avatar?.thumbnail?.url,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_microphone_off_voice),
                                contentDescription = "Microphone off",
                                tint = Color.Unspecified
                            )
                        }
                        Surface(
                            color = ThemeExtra.colors.thirdOpaqueGray,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    vertical = 6.dp,
                                    horizontal = 16.dp
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_weak_connection),
                                    contentDescription = "Microphone off",
                                    tint = ThemeExtra.colors.white
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.translations_weak_connection),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ThemeExtra.colors.white
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                } else if (!microphoneEnabled) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(100.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_microphone_off_voice),
                                contentDescription = "Microphone off",
                                tint = Color.Unspecified
                            )
                        }
                        Surface(
                            color = ThemeExtra.colors.thirdOpaqueGray,
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    vertical = 6.dp,
                                    horizontal = 16.dp
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_weak_connection),
                                    contentDescription = "Microphone off",
                                    tint = ThemeExtra.colors.white
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.translations_weak_connection),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ThemeExtra.colors.white
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                } else {
                    Surface(
                        color = ThemeExtra.colors.thirdOpaqueGray,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                vertical = 6.dp,
                                horizontal = 16.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_weak_connection),
                                contentDescription = "Microphone off",
                                tint = ThemeExtra.colors.white
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.translations_weak_connection),
                                style = MaterialTheme.typography.bodyMedium,
                                color = ThemeExtra.colors.white
                            )
                        }
                    }
                }
            }

            !cameraEnabled && !microphoneEnabled -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GCachedImage(
                        url = meetingModel?.organizer?.avatar?.thumbnail?.url,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_microphone_off_voice),
                        contentDescription = "Microphone off",
                        tint = Color.Unspecified
                    )
                }
            }

            !cameraEnabled && microphoneEnabled -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GCachedImage(
                        url = meetingModel?.organizer?.avatar?.thumbnail?.url,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            cameraEnabled && !microphoneEnabled -> {
                Surface(
                    color = ThemeExtra.colors.thirdOpaqueGray,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            vertical = 6.dp,
                            horizontal = 16.dp
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_micro_inactive),
                            contentDescription = "Microphone off",
                            tint = ThemeExtra.colors.white
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.translations_micro_off_organizer),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeExtra.colors.white
                        )
                    }
                }
            }
        }
    }
}