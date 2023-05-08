package ru.rikmasters.gilty.shared.shared.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Gradients.green

@Composable
fun PopularTags(
    tags: List<TagModel>,
    modifier: Modifier = Modifier,
    selected: List<TagModel>,
    isOnline: Boolean,
    category: CategoryModel? = null,
    onClick: (TagModel) -> Unit,
) {
    Column(modifier) {
        Text(
            stringResource(
                if(category == null) R.string.meeting_filter_popular
                else R.string.meeting_filter_popular_tags
            ), Modifier.padding(bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        Card(Modifier, colors = cardColors(Transparent)) {
            category?.let { PopularCategory(it) }
            PopularFlow(
                tags, Modifier,
                selected, isOnline,
            ) { onClick(it) }
        }
    }
}

@Composable
private fun PopularCategory(
    category: CategoryModel,
) {
    Column(
        Modifier.background(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            Start, CenterVertically
        ) {
            GEmojiImage(
                category.emoji,
                Modifier.size(24.dp)
            )
            Text(
                category.name,
                Modifier.padding(start = 16.dp),
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
        }
        GDivider(Modifier.fillMaxWidth())
    }
}

@Composable
private fun PopularFlow(
    tags: List<TagModel>,
    modifier: Modifier = Modifier,
    selected: List<TagModel>,
    isOnline: Boolean,
    onClick: (TagModel) -> Unit,
) {
    FlowLayout(
        modifier
            .background(colorScheme.primaryContainer)
            .padding(top = 16.dp, bottom = 4.dp)
            .padding(horizontal = 16.dp),
        12.dp, 12.dp
    ) {
        tags.forEach {
            GChip(
                Modifier, it.title,
                selected.contains(it),
                isOnline
            ) { onClick(it) }
        }
    }
}

@Composable
fun SelectTags(
    tags: List<TagModel>,
    modifier: Modifier = Modifier,
    isOnline: Boolean,
    onDelete: (TagModel) -> Unit,
) {
    Column(modifier) {
        Text(
            stringResource(R.string.meeting_filter_selected_tag),
            Modifier.padding(bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        Card(Modifier, colors = cardColors(Transparent)) {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
                12.dp, 12.dp
            ) {
                tags.forEach {
                    CrossTag(it, isOnline)
                    { onDelete(it) }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AllTagItem(
    item: TagModel,
    index: Int, size: Int,
    onClick: (TagModel) -> Unit,
) {
    Card(
        { onClick(item) }, Modifier, (true),
        lazyItemsShapes(index, size),
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                item.title, Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
        }
        if(index < size - 1)
            GDivider(Modifier.padding(start = 16.dp))
    }
}

@Composable
fun CrossTag(
    tag: TagModel,
    isOnline: Boolean,
    onDelete: () -> Unit,
) {
    Box(
        Modifier
            .background(
                Brush.linearGradient(
                    if(isOnline) green()
                    else listOf(
                        colorScheme.primary,
                        colorScheme.primary,
                    )
                ), shapes.large
            )
    ) {
        Row(
            Modifier
                .width(Max)
                .padding(vertical = 7.dp)
                .padding(start = 12.dp, end = 8.dp),
            Center, CenterVertically
        ) {
            Text(
                tag.title, Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                White, style = typography.labelSmall,
                fontWeight = SemiBold
            )
            Icon(
                Filled.Close, (null), Modifier
                    .size(18.dp)
                    .clickable { onDelete() },
                White
            )
        }
    }
}