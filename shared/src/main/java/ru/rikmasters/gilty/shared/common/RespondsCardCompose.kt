package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun RespondCardPreview() {
    GiltyTheme {
        Responds(3, DemoAvatarModel, stringResource(R.string.profile_responds_label)) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Responds(
    size: Int,
    image: ImageModel,
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick, Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    image.id, (null), Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text, Modifier.padding(start = 16.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "$size", Modifier.padding(12.dp, 6.dp),
                        Color.White, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Icon(
                    androidx.compose.material.icons.Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.profile_responds_label),
                    Modifier,
                    MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}