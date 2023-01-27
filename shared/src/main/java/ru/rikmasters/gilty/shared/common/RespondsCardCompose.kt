package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun RespondCardPreview() {
    GiltyTheme {
        Responds(
            stringResource(R.string.profile_responds_label),
            3, DemoAvatarModel
        ) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Responds(
    text: String,
    size: Int?,
    image: AvatarModel?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier.fillMaxWidth(),
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween,
            CenterVertically,
        ) {
            Row(
                Modifier, Start,
                CenterVertically
            ) {
                image?.let {
                    AsyncImage(
                        it.url, (null), Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = Crop
                    )
                }
                Text(
                    text, Modifier.padding(16.dp, 6.dp),
                    colorScheme.tertiary,
                    style = typography.labelSmall
                )
            }
            Row(
                Modifier, Start,
                CenterVertically
            ) {
                size?.let {
                    Box(
                        Modifier
                            .clip(shapes.extraSmall)
                            .background(colorScheme.primary)
                    ) {
                        Text(
                            "$size", Modifier.padding(12.dp, 6.dp),
                            White, fontWeight = SemiBold,
                            style = typography.labelSmall
                        )
                    }
                }
                Icon(
                    Filled.KeyboardArrowRight, (null),
                    Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}