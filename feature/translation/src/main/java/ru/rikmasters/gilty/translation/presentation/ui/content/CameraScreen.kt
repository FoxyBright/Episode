package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun CameraScreen(
    modifier: Modifier,
    translationUiState: TranslationUiState,
    translationStatus: TranslationStatus,
    initCamera: (OpenGlView) -> Unit,
    startBroadCast: () -> Unit,
    onCloseClicked: () -> Unit,
    changeFacing: () -> Unit,
    startStreamPreview: () -> Unit,
    stopStreamPreview: () -> Unit,
    remainTime: String,
    userCount: Int,
    onChatClicked: () -> Unit,
    onUsersClicked: () -> Unit,
    extendClicked:() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = if (translationStatus == TranslationStatus.PREVIEW) {
            RoundedCornerShape(
                topEnd = 14.dp,
                topStart = 14.dp
            )
        } else {
            RoundedCornerShape(14.dp)
        },
        color = Color.Transparent
    ) {
        Camera(
            startPreview = startStreamPreview,
            stopPreview = stopStreamPreview,
            initCamera = initCamera
        )
        OnScreenContent(
            translationStatus = translationStatus,
            translationUiState = translationUiState,
            startBroadCast = startBroadCast,
            onCloseClicked = onCloseClicked,
            changeFacing = changeFacing,
            remainTime = remainTime,
            userCount = userCount,
            onChatClicked = onChatClicked,
            onUsersClicked = onUsersClicked,
            onExtendCLicked = extendClicked
        )
    }
}

