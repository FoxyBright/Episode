package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.Gradients.blackTranslation
import ru.rikmasters.gilty.translation.presentation.ui.components.CameraItem
import ru.rikmasters.gilty.translation.presentation.ui.components.ChangeFacingItem
import ru.rikmasters.gilty.translation.presentation.ui.components.MicrophoneItem

@Composable
fun StreamControlPanel(
    modifier: Modifier,
    changeFacing: () -> Unit,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean
) {
    // TODO: Изменить градиент
    Row(
        modifier = modifier
            .background(
                linearGradient(blackTranslation())
            )
            .padding(
                bottom = 40.dp,
                top = 24.dp,
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