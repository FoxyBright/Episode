package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun StreamControlPanel(
    modifier: Modifier,
    changeFacing: () -> Unit,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    translationUiState: TranslationUiState
) {
    Row(
        modifier = modifier
            .background(
                Brush.linearGradient(Gradients.gray())
            )
            .padding(
                bottom = 40.dp,
                top = 24.dp,
                start = 20.dp,
                end = 20.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onCameraClicked) {
            Icon(
                painter = translationUiState.translationInfo?.camera?.let {
                    if (it) painterResource(id = R.drawable.ic_video_active)
                    else painterResource(id = R.drawable.ic_video_inactive)
                } ?: painterResource(id = R.drawable.ic_video_active),
                contentDescription = "turn on/off camera",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        IconButton(onClick = onMicrophoneClicked) {
            Icon(
                painter = translationUiState.translationInfo?.microphone?.let {
                    if (it) painterResource(id = R.drawable.ic_micro_active)
                    else painterResource(id = R.drawable.ic_micro_inactive)
                } ?: painterResource(id = R.drawable.ic_micro_active),
                contentDescription = "turn on/off microphone",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        IconButton(onClick = changeFacing) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "change camera",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}