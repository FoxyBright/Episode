package ru.rikmasters.gilty.translation.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun CameraItem(
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = if (enabled) painterResource(id = R.drawable.ic_video_active)
            else painterResource(id = R.drawable.ic_video_inactive),
            contentDescription = "turn on/off camera",
            tint = ThemeExtra.colors.white
        )
    }
}

@Composable
fun MicrophoneItem(
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = if (enabled) painterResource(id = R.drawable.ic_micro_active)
            else painterResource(id = R.drawable.ic_micro_inactive),
            contentDescription = "turn on/off microphone",
            tint = ThemeExtra.colors.white
        )
    }
}

@Composable
fun ChangeFacingItem(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_refresh),
            contentDescription = "change camera",
            tint = ThemeExtra.colors.white
        )
    }
}