package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.*

interface TagsCallback {
    
    fun onAddTag(text: String) {}
    fun onTagClick(tag: TagModel) {}
    fun onDeleteTag(tag: TagModel) {}
    fun onBack() {}
    fun onSave() {}
}

data class TagsState(
    val newTag: String,
    val tagList: List<TagModel>,
    val popularTags: List<TagModel>,
    val online: Boolean,
    val tagSearch: List<TagModel>,
    val category: CategoryModel?,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TagsContent(
    state: TagsState,
    modifier: Modifier = Modifier,
    callback: TagsCallback? = null,
) {
    Scaffold(
        modifier
            .fillMaxSize()
            .padding(16.dp), {
            TagSearch(
                SearchState(
                    state.newTag, state.online,
                    { callback?.onBack() },
                    { callback?.onAddTag("") }
                ) { callback?.onAddTag(it) }
            )
            SearchActionBar(
                SearchState(
                    (null), (true), state.newTag,
                    { callback?.onAddTag(it) },
                    state.online,
                    stringResource(R.string.meeting_filter_add_tag_text_holder)
                ) { callback?.onBack() }
            )
        }, {
            GradientButton(
                Modifier.padding(top = 28.dp, bottom = 54.dp),
                stringResource(R.string.save_button),
                online = state.online
            ) { callback?.onSave() }
        }) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(top = it.calculateTopPadding())
        ) {
            Text(
                stringResource(R.string.add_meet_popular_tags),
                Modifier.padding(vertical = 20.dp),
                colorScheme.tertiary,
                style = typography.displayLarge
            )
            LazyColumn(Modifier.fillMaxWidth()) {
                if(state.popularTags.isNotEmpty()
                    && state.newTag.isBlank()
                ) item {
                    state.category?.let { category ->
                        PopularTags(
                            state.popularTags,
                            state.tagList, state.online, category
                        ) { tag -> callback?.onTagClick(tag) }
                    }
                }
                if(state.newTag.isNotBlank())
                    itemsIndexed(state.tagSearch) { i, item ->
                        AllTagItem(
                            item, i, state.tagSearch.size
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
    item: TagModel, index: Int, size: Int,
    onClick: (TagModel) -> Unit,
) {
    Card(
        { onClick(item) }, Modifier, (true),
        lazyItemsShapes(index, size),
        cardColors(colorScheme.primaryContainer)
    ) {
        Text(
            item.title,
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium
        ); if(index < size - 1)
        Divider(Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun PopularTags(
    popularTags: List<TagModel>,
    tagList: List<TagModel>,
    online: Boolean,
    category: CategoryModel,
    onClick: (TagModel) -> Unit,
) {
    Card(colors = cardColors(Transparent)) {
        Column(
            Modifier.background(
                colorScheme.primaryContainer
            )
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp), Start, CenterVertically
            ) {
                GEmojiImage(category.emoji, Modifier.size(24.dp))
                Text(
                    category.name, Modifier.padding(start = 16.dp),
                    style = typography.bodyMedium,
                    fontWeight = SemiBold
                )
            }
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(8.dp), 8.dp, 8.dp
        ) {
            popularTags.forEach {
                GiltyChip(
                    Modifier, it.title,
                    tagList.contains(it), online
                ) { onClick(it) }
            }
        }
    }
}