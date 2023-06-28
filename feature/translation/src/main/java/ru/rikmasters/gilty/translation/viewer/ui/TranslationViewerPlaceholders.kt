package ru.rikmasters.gilty.translation.viewer.ui

import android.content.Context
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
import ru.rikmasters.gilty.translation.shared.components.TranslationExtendedSnackbar
import ru.rikmasters.gilty.translation.shared.components.TranslationResumedSnackbar
import ru.rikmasters.gilty.translation.shared.components.WeakConnectionSnackbar
import ru.rikmasters.gilty.translation.shared.utils.animatePlacement
import ru.rikmasters.gilty.translation.viewer.model.ViewerHUD
import ru.rikmasters.gilty.translation.viewer.model.ViewerSnackbarState

@Composable
fun OnTopSnackbarsPlacehodlers(
    orientation: Int,
    bsOpened: Boolean,
    isShowPlacehodlers: Boolean,
    isShowSnackbar: Boolean,
    cameraState: Boolean,
    microphoneState: Boolean,
    meeting: FullMeetingModel?,
    snackbarState: ViewerSnackbarState?,
    configuration: Configuration,
    hudState: ViewerHUD?,
    context: Context
) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isShowPlacehodlers) {
                val alignment = if (isShowSnackbar) Alignment.TopCenter else Alignment.Center
                val topPadding = if (isShowSnackbar) 77.dp else 0.dp
                Column(
                    modifier = Modifier.animatePlacement().align(alignment).padding(top = topPadding)
                ) {
                    if (!cameraState) {
                        ProfileAvatar(meetingModel = meeting, modifier = Modifier)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!microphoneState) {
                            MicroWave()
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
                        ViewerSnackbarState.MICRO_OFF -> {
                            MicroInactiveSnackbar(organizer = false)
                        }
                        ViewerSnackbarState.WEAK_CONNECTION -> {
                            WeakConnectionSnackbar()
                        }
                        ViewerSnackbarState.BROADCAST_EXTENDED -> {
                            TranslationExtendedSnackbar()
                        }
                        ViewerSnackbarState.BROADCAST_RESUMED -> {
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
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!microphoneState) {
                            MicroWave()
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
                        ViewerSnackbarState.MICRO_OFF -> {
                            MicroInactiveSnackbar(organizer = false)
                        }
                        ViewerSnackbarState.WEAK_CONNECTION -> {
                            WeakConnectionSnackbar()
                        }
                        ViewerSnackbarState.BROADCAST_EXTENDED -> {
                            TranslationExtendedSnackbar()
                        }
                        ViewerSnackbarState.BROADCAST_RESUMED -> {
                            TranslationResumedSnackbar()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}