package ru.rikmasters.gilty.translation.presentation.ui.content.preview

import android.view.SurfaceHolder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.presentation.ui.components.BottomSheetDragItem
import ru.rikmasters.gilty.translation.presentation.ui.components.CameraOrientationRow
import ru.rikmasters.gilty.translation.presentation.ui.components.CameraView
import ru.rikmasters.gilty.translation.presentation.ui.components.CloseButton

@Composable
fun TranslationPreview(
    initializeCamera: (OpenGlView) -> Unit,
    onCloseClicked: () -> Unit,
    changeFacing: () -> Unit,
    selectedFacing: Facing,
    startBroadCast: () -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(
                topEnd = 14.dp,
                topStart = 14.dp
            )
    ) {
        CameraView (
            initCamera = initializeCamera,
            surfaceHolderCallback = surfaceHolderCallback
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            BottomSheetDragItem(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(22.dp))
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
                selectedFacing = selectedFacing
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