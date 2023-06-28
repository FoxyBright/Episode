package ru.rikmasters.gilty.translation.streamer.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.translation.shared.components.MicroInactiveSnackbar
import ru.rikmasters.gilty.translation.shared.components.MicroWave
import ru.rikmasters.gilty.translation.shared.components.ProfileAvatar
import ru.rikmasters.gilty.translation.shared.components.TranslationResumedSnackbar
import ru.rikmasters.gilty.translation.shared.components.WeakConnectionSnackbar
import ru.rikmasters.gilty.translation.shared.utils.animatePlacement
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState

@Composable
fun OnTopSnackbarsPlacehodlers(
    orientation: Int,
    bsOpened: Boolean,
    isShowPlacehodlers: Boolean,
    isShowSnackbar: Boolean,
    cameraState: Boolean,
    microphoneState: Boolean,
    meeting: FullMeetingModel?,
    snackbarState: StreamerSnackbarState?,
    configuration: Configuration,
    hudState: StreamerHUD?
) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isShowPlacehodlers) {
                val alignment = if (isShowSnackbar) Alignment.TopCenter else Alignment.Center
                val topPadding = if (isShowSnackbar) 90.dp else 0.dp
                Column(
                    modifier = Modifier.animatePlacement().align(alignment).padding(top = topPadding)
                ) {
                    if (!cameraState) {
                        ProfileAvatar(meetingModel = meeting, modifier = Modifier)
                        if (!microphoneState) {
                            Spacer(modifier = Modifier.height(8.dp))
                       //     MicroWave(meetingModel = meeting, modifier = Modifier)
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = isShowSnackbar && hudState == null,
                modifier = Modifier.align(Alignment.Center),
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    when (snackbarState) {
                        StreamerSnackbarState.MICRO_OFF -> {
                            MicroInactiveSnackbar(organizer = true)
                        }
                        StreamerSnackbarState.WEAK_CONNECTION -> {
                            WeakConnectionSnackbar()
                        }
                        StreamerSnackbarState.BROADCAST_EXTENDED -> {
                            TranslationResumedSnackbar()
                        }
                        else -> {}
                    }
                }
            }
        }
    } else {
        Box(
            modifier = if (bsOpened) {
                Modifier
                    .fillMaxHeight()
                    .width((configuration.screenWidthDp * 0.6).dp)
            } else {
                Modifier.fillMaxSize()
            }
        ) {
            AnimatedVisibility(
                visible = isShowPlacehodlers && !isShowSnackbar,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center ,initialSize = { it * 0.5.toInt() }),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center ,targetSize = { it * 0.5.toInt() })
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    if (!cameraState) {
                        ProfileAvatar(meetingModel = meeting, modifier = Modifier)
                        if (!microphoneState) {
                            Spacer(modifier = Modifier.height(8.dp))
                           // MicroWave(meetingModel = meeting, modifier = Modifier)
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = isShowSnackbar,
                modifier = Modifier.align(Alignment.Center),
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    when (snackbarState) {
                        StreamerSnackbarState.MICRO_OFF -> {
                            MicroInactiveSnackbar(organizer = true)
                        }
                        StreamerSnackbarState.WEAK_CONNECTION -> {
                            WeakConnectionSnackbar()
                        }
                        StreamerSnackbarState.BROADCAST_EXTENDED -> {
                            TranslationResumedSnackbar()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}