package ru.rikmasters.gilty.translation.bottoms.preview

import android.view.SurfaceHolder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.components.CameraOrientationRow
import ru.rikmasters.gilty.translation.shared.components.CameraView
import ru.rikmasters.gilty.translation.shared.components.CloseButton
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

@Composable
fun PreviewBsContent(
    initCamera: (OpenGlView) -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    onCloseClicked: () -> Unit,
    changeFacing: (StreamerFacing) -> Unit,
    selectedStreamerFacing: StreamerFacing,
    startBroadCast: () -> Unit
) {
    Box {
        CameraView(
            initCamera = initCamera,
            surfaceHolderCallback = surfaceHolderCallback,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.translations_preview_title),
                    color = ThemeExtra.colors.white,
                    style = ThemeExtra.typography.TranslationTitlePreview
                )
                CloseButton { onCloseClicked() }
            }
            Spacer(modifier = Modifier.weight(1f))
            CameraOrientationRow(
                onChange = changeFacing,
                selectedStreamerFacing = selectedStreamerFacing
            )
            Spacer(modifier = Modifier.height(12.dp))
            GradientButton(
                text = stringResource(id = R.string.translations_start_strean),
                online = true,
                onClick = startBroadCast
            )
            Spacer(modifier = Modifier.height(53.dp))
        }
    }
}