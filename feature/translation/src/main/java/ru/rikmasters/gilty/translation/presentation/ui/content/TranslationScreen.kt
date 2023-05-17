package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.theme.Gradients.blackTranslationReversed
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
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
    startStream: () -> Unit,
    remainTime: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ThemeExtra.colors.preDarkColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ThemeExtra.colors.preDarkColor)
        ) {
            // TODO: Сделать нормальный градиент
            CameraScreen(
                modifier = Modifier
                    .weight(1f)
                    .background(verticalGradient(blackTranslationReversed())),
                translationUiState = translationUiState,
                translationStatus = translationStatus,
                initCamera = initCamera,
                startBroadCast = startStream,
                onCloseClicked = onCloseClicked,
                changeFacing = changeFacing,
                startStreamPreview = startStreamPreview,
                stopStreamPreview = stopStreamPreview,
                remainTime = remainTime
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
        if (translationUiState.translationInfo?.camera == false) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GCashedImage(
                    url = translationUiState.meetingModel?.organizer?.avatar?.thumbnail?.url,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                // TODO: полоска голоса
                if (translationUiState.translationInfo.microphone == false) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_micro_inactive),
                        contentDescription = "Microphone off",
                        tint = ThemeExtra.colors.white
                    )
                }
            }
        } else if (translationUiState.translationInfo?.microphone == false) {
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