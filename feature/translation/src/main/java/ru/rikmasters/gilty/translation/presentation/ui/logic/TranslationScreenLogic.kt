package ru.rikmasters.gilty.translation.presentation.ui.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import kotlinx.coroutines.flow.collectLatest
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.presentation.ui.content.TranslationScreen
import ru.rikmasters.gilty.translation.viewmodel.TranslationViewModel

@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel,
    translationId: String
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        vm.onEvent(
            TranslationEvent.EnterScreen(
                meetingId = translationId
            )
        )
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.oneTimeEvent.collectLatest { event ->
                when(event) {
                    is TranslationOneTimeEvent.ErrorHappened -> TODO()
                }
            }
        }
    }

    val translationScreenState by vm.translationUiState.collectAsState()

    val remainTime by vm.remainTime.collectAsState()

    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }

    camera?.let {
        if (
            translationScreenState.selectedCamera == Facing.FRONT && it.cameraFacing != CameraHelper.Facing.FRONT
            || translationScreenState.selectedCamera == Facing.BACK && it.cameraFacing != CameraHelper.Facing.BACK
        ) {
            it.switchCamera()
        }
    }

    LaunchedEffect(translationScreenState.translationInfo?.microphone) {
        camera?.let { camera ->
            translationScreenState.translationInfo?.microphone?.let { enabled ->
                if (enabled) {
                    camera.enableAudio()
                } else {
                    camera.disableAudio()
                }
            }
        }
    }

    LaunchedEffect(translationScreenState.translationInfo?.camera) {
        camera?.let { camera ->
            translationScreenState.translationInfo?.camera?.let { enabled ->
                if (enabled) {
                    camera.glInterface.unMuteVideo()
                    camera.glInterface.clearFilters()
                    camera.resumeRecord()
                } else {
                    camera.glInterface.muteVideo()
                    camera.glInterface.addFilter(BlurFilterRender())
                    camera.pauseRecord()
                }
            }
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
                    vm.onEvent(TranslationEvent.StopStreaming)
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
            vm.onEvent(TranslationEvent.ChangeVideoState)
        },
        onMicrophoneClicked = {
            vm.onEvent(TranslationEvent.ChangeMicrophoneState)
        },
        initCamera = { view ->
            camera = RtmpCamera2(view, connectionChecker)
        },
        startStreamPreview = {
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
        },
        remainTime = remainTime
    )
}

private enum class StreamState {
    STOP, PLAY
}