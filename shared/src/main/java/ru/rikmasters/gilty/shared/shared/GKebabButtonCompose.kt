package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun KebabPreview() {
    GiltyTheme {
        GKebabButton()
    }
}

@Composable
fun GKebabButton(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.outlineVariant,
    onClick: (() -> Unit)? = null
) {
    IconButton({ onClick?.let { it() } }, modifier) {
        Icon(
            painterResource(R.drawable.ic_kebab),
            (null), Modifier, color
        )
    }
}