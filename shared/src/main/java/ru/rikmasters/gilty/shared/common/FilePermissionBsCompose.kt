package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun StoragePermissionBsPreview() {
    GiltyTheme {
        StoragePermissionBs()
    }
}

@Composable
fun StoragePermissionBs(
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .padding(16.dp),
        SpaceBetween
    ) {
        Text(
            stringResource(R.string.profile_gallery_title),
            Modifier, style = typography.labelLarge,
        )
        Column(
            Modifier.fillMaxWidth(),
            Top, CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.ic_image_box),
                (null), Modifier.size(50.dp),
                colorScheme.primary
            )
            Text(
                stringResource(R.string.permissions_settings_label),
                Modifier.padding(top = 22.dp), style = typography.labelLarge,
                textAlign = Center
            )
        }
        GradientButton(
            Modifier
                .padding(bottom = 40.dp),
            stringResource(R.string.permissions_settings_button)
        ) { onSettingsClick?.let { it() } }
    }
}