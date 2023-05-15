package ru.rikmasters.gilty.translation.presentation.ui.logic

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.presentation.ui.content.TranslationScreen
import ru.rikmasters.gilty.translation.viewmodel.TranslationViewModel

@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel,
    translationId: String
) {
    LaunchedEffect(Unit) {
        vm.onEvent(
            TranslationEvent.EnterScreen(
                translationId = translationId
            )
        )
    }

    val translationScreenState by vm.translationUiState.collectAsState()

    Log.d("TEST","STate ${translationScreenState.translationStatus}")

    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }

    camera?.let {
        if (
            translationScreenState.selectedCamera == Facing.FRONT && it.cameraFacing != CameraHelper.Facing.FRONT
            || translationScreenState.selectedCamera == Facing.BACK && it.cameraFacing != CameraHelper.Facing.BACK
        ) {
            it.switchCamera()
        }
    }

    var streamState by remember { mutableStateOf(StreamState.STOP) }

    fun stopBroadcast() {
        camera?.stopStream()
        vm.onEvent(TranslationEvent.StopStreaming)
    }

    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                if (it.prepareAudio() && it.prepareVideo()) {
                    streamState = StreamState.PLAY
                    it.startStream(rtmpUrl)
                    vm.onEvent(TranslationEvent.StartStreaming)
                } else {
                    streamState = StreamState.STOP
                }
            } else {
                it.stopStream()
                vm.onEvent(TranslationEvent.StopStreaming)
            }
        }
    }

    val connectionChecker = remember {
        object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
            override fun onConnectionFailedRtmp(reason: String) {}
            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {
                streamState = StreamState.PLAY
            }

            override fun onDisconnectRtmp() {
                streamState = StreamState.STOP
            }

            override fun onNewBitrateRtmp(bitrate: Long) {}
        }
    }



    TranslationScreen(
        translationStatus = translationScreenState.translationStatus?: TranslationStatus.PREVIEW,
        onCloseClicked = {
            stopBroadcast()
        },
        translationUiState = translationScreenState,
        changeFacing = {
            vm.onEvent(TranslationEvent.ChangeFacing)
        },
        onCameraClicked = {

        },
        onMicrophoneClicked = {

        },
        initCamera = { view ->
            Log.d("TEST","INITED $view")
            camera = RtmpCamera2(view, connectionChecker)
        },
        startStreamPreview = {
            Log.d("TEST","Start Preview")
            camera?.startPreview()
        },
        stopStreamPreview = {
            camera?.stopPreview()
        },
        startStream = {
            translationScreenState.translationInfo?.let {
                startBroadCast(
                    rtmpUrl = it.rtmp
                )
            }
        }
    )
}

private enum class StreamState {
    STOP, PLAY
}