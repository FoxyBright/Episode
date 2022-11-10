package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun EmojiRow(modifier:Modifier = Modifier, onClick: (EmojiModel) -> Unit) {
    Column(modifier) {
        Image(
            painterResource(R.drawable.ic_cloud_part),
            null, Modifier.padding(start = 60.dp)
        )
        LazyRow(
            Modifier
                .padding(10.dp)
                .background(ThemeExtra.colors.chipGray, CircleShape)
        ) { items(EmojiList) { Emoji(it) { e -> onClick(e) } } }
    }
}

@Composable
private fun Emoji(
    emoji: EmojiModel,
    onClick: (EmojiModel) -> Unit
) {
    Image(
        if (emoji.type == "D") painterResource(emoji.path.toInt())
        else rememberAsyncImagePainter(emoji.path), (null),
        Modifier
            .padding(10.dp)
            .size(20.dp)
            .clip(CircleShape)
            .clickable { onClick(emoji) }
    )
}