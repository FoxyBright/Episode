package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun TranslationScreen(
    translationStatus: TranslationStatus,
    onCloseClicked: () -> Unit,
    translationUiState: TranslationUiState,
    changeFacing: () -> Unit,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    initCamera: (OpenGlView) -> Unit,
    startStreamPreview: () -> Unit,
    stopStreamPreview: () -> Unit,
    startStream: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        CameraScreen(
            modifier = Modifier.weight(1f),
            translationUiState = translationUiState,
            translationStatus = translationStatus,
            initCamera = initCamera,
            startBroadCast = startStream,
            onCloseClicked = onCloseClicked,
            changeFacing = changeFacing,
            startStreamPreview = startStreamPreview,
            stopStreamPreview = stopStreamPreview
        )
        if (translationStatus == TranslationStatus.STREAM) {
            StreamControlPanel(
                modifier = Modifier.fillMaxWidth(),
                changeFacing = changeFacing,
                onCameraClicked = onCameraClicked,
                onMicrophoneClicked = onMicrophoneClicked,
                translationUiState = translationUiState
            )
        }
    }
}