package ru.rikmasters.gilty.shared.common.tagSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

val allTags =
    listOf(
        "Здесь",
        "Должен",
        "Быть",
        "Список",
        "Всех",
        "Доступных",
        "Тегов"
    ).map { TagModel(it, it) }

@Preview
@Composable
private fun TagSearchPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TagSearchContent(
                TagState(
                    listOf(allTags.first()),
                    listOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly")
                        .map { TagModel(it, it) },
                    (true), (""), allTags, true
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun TagSearchListPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TagSearchContent(
                TagState(
                    listOf(allTags.first()),
                    emptyList(),
                    (false), ("зде"), allTags, false
                ), Modifier.padding(16.dp)
            )
        }
    }
}

interface TagSearchCallback {
    
    fun onSearchChange(text: String) {}
    fun onTagClick(tag: TagModel) {}
    fun onDeleteTag(tag: TagModel) {}
    fun onBack() {}
    fun onComplete() {}
}

data class TagState(
    val selected: List<TagModel>,
    val populars: List<TagModel>,
    val popularState: Boolean,
    val search: String,
    val searchResult: List<TagModel>,
    val online: Boolean,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TagSearchContent(
    state: TagState,
    modifier: Modifier,
    callback: TagSearchCallback? = null,
) {
    Scaffold(
        modifier
            .fillMaxSize()
            .padding(16.dp), {
            SearchActionBar(
                SearchState(
                    (null), (true), state.search,
                    { callback?.onSearchChange(it) },
                    state.online
                ) { callback?.onBack() }
            )
        }, {
            GradientButton(
                Modifier.padding(top = 28.dp, bottom = 54.dp),
                stringResource(R.string.meeting_filter_complete_button),
                online = state.online
            ) { callback?.onComplete() }
        }) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(top = it.calculateTopPadding())
        ) {
            Text(
                if(state.popularState) stringResource(R.string.meeting_filter_popular_tags)
                else stringResource(R.string.meeting_filter_search_from_tag),
                Modifier.padding(top = 20.dp),
                colorScheme.tertiary,
                style = typography.titleLarge
            )
            LazyColumn(Modifier.fillMaxWidth()) {
                if(state.popularState && state.populars.isNotEmpty())
                    item {
                        PopularTags(
                            state.populars,
                            state.selected, state.online
                        ) { tag -> callback?.onTagClick(tag) }
                    }
                if(!state.popularState)
                    itemsIndexed(state.searchResult) { i, item ->
                        AllTagItem(
                            item, i, state.searchResult.size,
                            state.selected, state.online
                        ) { tag -> callback?.onTagClick(tag) }
                    }
                item {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AllTagItem(
    item: TagModel,
    index: Int,
    size: Int,
    tagList: List<TagModel>,
    online: Boolean,
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
            SpaceBetween,
            CenterVertically
        ) {
            Text(
                item.title,
                Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            if(!tagList.contains(item)) Icon(
                Filled.KeyboardArrowRight,
                (null), Modifier, White
            ) else CheckBox(
                true, Modifier.clip(CircleShape),
                listOf(
                    if(online) R.drawable.online_check_box
                    else R.drawable.enabled_check_box,
                    R.drawable.disabled_check_box
                )
            ) { onClick(item) }
        }; if(index < size - 1)
        GDivider(Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun PopularTags(
    popularTags: List<TagModel>,
    tagList: List<TagModel>,
    online: Boolean,
    onClick: (TagModel) -> Unit,
) {
    Card(
        Modifier.padding(top = 20.dp),
        colors = cardColors(
            containerColor = Transparent
        )
    ) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(8.dp), 8.dp, 8.dp
        ) {
            popularTags.forEach {
                GChip(
                    Modifier, it.title,
                    tagList.contains(it),
                    online
                ) { onClick(it) }
            }
        }
    }
}