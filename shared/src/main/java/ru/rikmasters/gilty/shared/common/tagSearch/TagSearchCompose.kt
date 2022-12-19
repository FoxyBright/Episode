package ru.rikmasters.gilty.shared.common.tagSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

val allTags = listOf("Здесь", "Должен", "Быть", "Список", "Всех", "Доступных", "Тегов")

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TagSearchPreview() {
    GiltyTheme {
        TagSearchContent(
            TagState(
                listOf(allTags.first()),
                listOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly"),
                (true), (""), allTags, true
            ), Modifier.padding(16.dp)
        )
    }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun TagSearchListPreview() {
    GiltyTheme {
        TagSearchContent(
            TagState(
                listOf(allTags.first()),
                listOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly"),
                (false), (""), allTags, false
            ), Modifier.padding(16.dp)
        )
    }
}

interface TagSearchCallback {
    
    fun searchTextChange(text: String) {}
    fun onTagClick(tag: String) {}
    fun onDeleteTag(tag: Int) {}
    fun onBack() {}
    fun onNext() {}
}

data class TagState(
    val tagList: List<String>,
    val popularTags: List<String>,
    val popularTagsListVisible: Boolean,
    val searchText: String,
    val allTagList: List<String>,
    val online: Boolean
)

@Composable
fun TagSearchContent(
    state: TagState,
    modifier: Modifier,
    callback: TagSearchCallback? = null
) {
    Column(modifier) {
        SearchActionBar(
            SearchState(
                (null), (true), state.searchText,
                { callback?.searchTextChange(it) },
                state.online
            ) { callback?.onBack() }
        )
        Text(
            if(state.popularTagsListVisible) stringResource(R.string.meeting_filter_popular_tags)
            else stringResource(R.string.meeting_filter_search_from_tag),
            Modifier.padding(top = 20.dp),
            colorScheme.tertiary,
            style = typography.titleLarge
        )
        LazyColumn(Modifier.fillMaxWidth()) {
            if(state.popularTagsListVisible) item {
                PopularTags(
                    state.popularTags,
                    state.tagList, state.online
                ) { callback?.onTagClick(it) }
            }
            if(state.tagList.isNotEmpty()) item {
                TagList(
                    state.tagList,
                    Modifier.padding(vertical = 16.dp),
                    state.online
                ) { callback?.onDeleteTag(it) }
            }
            if(!state.popularTagsListVisible)
                itemsIndexed(state.allTagList) { i, item ->
                    AllTagItem(
                        item, i, state.allTagList.size,
                        state.tagList, state.online
                    ) { callback?.onTagClick(it) }
                }
            item {
                GradientButton(
                    Modifier.padding(top = 28.dp),
                    stringResource(R.string.meeting_filter_complete_button),
                    online = state.online
                ) { callback?.onNext() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTagItem(
    item: String,
    index: Int,
    size: Int,
    tagList: List<String>,
    online: Boolean,
    onClick: (String) -> Unit
) {
    Card(
        { onClick(item) }, Modifier, (true),
        LazyItemsShapes(index, size),
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween,
            CenterVertically
        ) {
            Text(
                item, Modifier,
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            if(!tagList.contains(item)) Icon(
                Filled.KeyboardArrowRight,
                (null), Modifier, Color.White
            ) else CheckBox(
                true, Modifier,
                listOf(
                    if(online) R.drawable.online_check_box
                    else R.drawable.enabled_check_box,
                    R.drawable.disabled_check_box
                )
            ) {}
        }; if(index < size - 1)
        Divider(Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun PopularTags(
    popularTags: List<String>,
    tagList: List<String>,
    online: Boolean,
    onClick: (String) -> Unit
) {
    Card(Modifier.padding(top = 20.dp)) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(16.dp), 4.dp, 8.dp
        ) {
            popularTags.forEach {
                GiltyChip(
                    Modifier, it,
                    tagList.contains(it),
                    online
                ) { onClick(it) }
            }
        }
    }
}

@Composable
private fun TagList(
    tagList: List<String>,
    modifier: Modifier = Modifier,
    online: Boolean,
    onDeleteTag: (Int) -> Unit
) {
    Card(modifier) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp), 16.dp, 8.dp
        ) {
            tagList.forEachIndexed { index, item ->
                Tag(item, online) { onDeleteTag(index) }
            }
        }
    }
}

@Composable
private fun Tag(
    tag: String,
    online: Boolean,
    onDeleteTag: () -> Unit
) {
    Box(
        Modifier
            .clip(MaterialTheme.shapes.large)
            .background(linearGradient(if(online) green() else red()))
    ) {
        Row(
            Modifier.padding(12.dp, 6.dp),
            Arrangement.Center, CenterVertically
        ) {
            Text(
                tag, Modifier.padding(end = 10.dp),
                Color.White, style = typography.labelSmall
            )
            Icon(
                Filled.Close,
                stringResource(R.string.meeting_filter_delete_tag_label),
                Modifier.clickable { onDeleteTag() },
                Color.White
            )
        }
    }
}