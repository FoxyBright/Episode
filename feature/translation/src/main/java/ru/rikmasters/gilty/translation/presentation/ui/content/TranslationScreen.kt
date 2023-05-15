package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.compose.ConstraintLayout
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        val (camera, panel) = createRefs()

        Box(modifier = Modifier.constrainAs(camera) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(panel.top)
        }) {
            CameraScreen(
                translationUiState = translationUiState,
                translationStatus = translationStatus,
                initCamera = initCamera,
                startBroadCast = startStream,
                onCloseClicked = onCloseClicked,
                changeFacing = changeFacing,
                startStreamPreview = startStreamPreview,
                stopStreamPreview = stopStreamPreview
            )
        }
        if (translationStatus == TranslationStatus.STREAM) {
            Box(modifier = Modifier.constrainAs(panel) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }) {
                StreamControlPanel(
                    changeFacing = changeFacing,
                    onCameraClicked = onCameraClicked,
                    onMicrophoneClicked = onMicrophoneClicked,
                    translationUiState = translationUiState
                )
            }
        }
    }
}