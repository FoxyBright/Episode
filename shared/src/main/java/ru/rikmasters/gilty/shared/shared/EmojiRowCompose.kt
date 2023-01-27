package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_cloud_part
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Composable
fun EmojiRow(
    modifier: Modifier = Modifier,
    onClick: (EmojiModel) -> Unit
) {
    Column(modifier) {
        Image(
            painterResource(ic_cloud_part),
            (null), Modifier.padding(start = 40.dp)
        )
        Box(
            Modifier
                .padding(top = 2.dp, bottom = 12.dp)
                .background(
                    colors.chipGray, CircleShape
                ), Center
        ) {
            LazyRow(Modifier.clip(CircleShape)) {
                items(EmojiModel.list) {
                    GEmojiImage(it, Modifier
                        .padding(10.dp)
                        .size(30.dp)
                        .clickable(
                            MutableInteractionSource(), (null)
                        ) { onClick(it) }
                    )
                }
            }
        }
    }
}