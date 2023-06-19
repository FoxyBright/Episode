package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_cloud_part
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Composable
fun EmojiRow(
    emojiList: List<EmojiModel>,
    modifier: Modifier = Modifier,
    onClick: (EmojiModel) -> Unit,
) {
    Column(modifier) {
        Icon(
            painter = painterResource(
                ic_cloud_part
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 40.dp),
            tint = colors.notificationCloud
        )
        Box(
            Modifier
                .padding(top = 2.dp, bottom = 12.dp)
                .background(
                    color = colors.notificationCloud,
                    shape = CircleShape
                ), Center
        ) { List(emojiList, onClick) }
    }
}

@Composable
private fun List(
    emojiList: List<EmojiModel>,
    onClick: (EmojiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier
            .clip(CircleShape)
            .padding(vertical = 10.dp)
    ) {
        itemsIndexed(
            items = emojiList,
            key = { i, e -> e.path.plus(i) },
            contentType = { _, e -> e.type }
        ) { index, item ->
            Item(item, index, onClick)
        }
    }
}

@Composable
private fun Item(
    item: EmojiModel,
    index: Int,
    onClick: (EmojiModel) -> Unit,
) {
    GEmojiImage(
        emoji = item,
        modifier = Modifier
            .padding(
                start = if(index == 0)
                    14.dp else 0.dp,
                end = 14.dp,
            )
            .size(30.dp)
            .clickable(
                MutableInteractionSource(),
                indication = null
            ) { onClick(item) }
    )
}