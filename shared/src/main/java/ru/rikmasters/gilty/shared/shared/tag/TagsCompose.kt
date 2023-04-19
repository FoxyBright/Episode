package ru.rikmasters.gilty.shared.shared.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private val allTags = listOf(
    "Здесь", "Должен",
    "Быть", "Список",
    "Всех", "Доступных", "Тегов"
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
            TagsContent(
                TagsState(
                    listOf(allTags.first()),
                    listOf(
                        "Бэтмэн", "Кингсмен",
                        "Лобби", "Lorem", "Lolly"
                    ).map { TagModel(it, it) },
                    (""), allTags, (true), (null)
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
            TagsContent(
                TagsState(
                    listOf(allTags.first()),
                    emptyList(), ("love"),
                    allTags, (false), (null)
                ), Modifier.padding(16.dp)
            )
        }
    }
}

data class TagsState(
    val selected: List<TagModel>,
    val populars: List<TagModel>,
    val search: String,
    val searchResult: List<TagModel>,
    val isOnline: Boolean,
    val category: CategoryModel?,
    val add: Boolean = false,
)

interface TagsCallback {
    
    fun onSearchChange(text: String) {}
    fun onCreateTag(tagName: String) {}
    fun onTagClick(tag: TagModel) {}
    fun onDeleteTag(tag: TagModel) {}
    fun onBack() {}
    fun onComplete() {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TagsContent(
    state: TagsState,
    modifier: Modifier,
    callback: TagsCallback? = null,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(
                bottom = if(state.add)
                    0.dp else 20.dp
            ), topBar = {
            TagSearch(
                state.search,
                Modifier.padding(
                    top = 28.dp,
                    bottom = 4.dp
                ),
                state.isOnline, state.add,
                { callback?.onBack() },
                { callback?.onSearchChange(it) },
            ) { callback?.onCreateTag(it) }
        }, bottomBar = {
            if(state.selected.isNotEmpty())
                GradientButton(
                    Modifier
                        .padding(
                            top = 28.dp,
                            bottom = 20.dp
                        )
                        .padding(horizontal = 16.dp),
                    stringResource(R.string.meeting_filter_complete_button),
                    online = state.isOnline
                ) { callback?.onComplete() }
        }
    ) {
        Content(
            state, Modifier
                .padding(horizontal = 16.dp)
                .padding(top = it.calculateTopPadding()),
            callback
        )
    }
}

@Composable
private fun Content(
    state: TagsState,
    modifier: Modifier,
    callback: TagsCallback?,
) {
    if(state.search.isNotBlank()
        && state.searchResult.isEmpty()
    ) Box(Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.meeting_filter_tags_placeholder),
            Modifier.align(Alignment.Center),
            style = typography.bodyMedium,
            fontWeight = SemiBold,
            color = colorScheme.scrim
        )
    }
    else Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            if(state.search.isBlank() && state.populars.isNotEmpty()) {
                item {
                    PopularTags(
                        state.populars, Modifier,
                        state.selected, state.isOnline,
                        state.category
                    ) { tag -> callback?.onTagClick(tag) }
                }
                if(state.selected.isNotEmpty()) item {
                    SelectTags(
                        state.selected,
                        Modifier.padding(top = 28.dp),
                        state.isOnline,
                    ) { callback?.onDeleteTag(it) }
                }
            }
            if(state.search.isNotBlank())
                itemsIndexed(state.searchResult) { i, item ->
                    AllTagItem(
                        item, i, state.searchResult.size,
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