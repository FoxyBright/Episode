package ru.rikmasters.gilty.mainscreen.presentation.ui.tagsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.mainscreen.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

private val allTags = listOf("Здесь", "Должен", "Быть", "Список", "Всех", "Доступных", "Тегов")

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingTagSearchPreview() {
    GiltyTheme {
        val tagList = remember { mutableStateListOf<String>() }
        val searchText = remember { mutableStateOf("") }
        val popularTagsListVisible = remember { mutableStateOf(true) }
        val state = MeetingTagState(
            tagList,
            remember { mutableStateListOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly") },
            popularTagsListVisible.value,
            searchText.value,
            allTags
        )
        MeetingTagSearch(state, Modifier.padding(16.dp), object : MeetingTagSearchCallback {
            override fun searchTextChange(text: String) {
                popularTagsListVisible.value = text == ""
                searchText.value = text
            }

            override fun onDeleteTag(tag: Int) {
                tagList.removeAt(tag)
            }

            override fun onTagClick(tag: String) {
                if (!tagList.contains(tag)) tagList.add(tag)
                else tagList.removeAt(tagList.indexOf(tag))
            }
        })
    }
}

interface MeetingTagSearchCallback : NavigationInterface {
    fun searchTextChange(text: String)
    fun onTagClick(tag: String)
    fun onDeleteTag(tag: Int)
}

data class MeetingTagState(
    val tagList: List<String>,
    val popularTags: List<String>,
    val popularTagsListVisible: Boolean,
    val searchText: String,
    val allTagList: List<String>
)

@Composable
fun MeetingTagSearch(
    state: MeetingTagState,
    modifier: Modifier,
    callback: MeetingTagSearchCallback? = null
) {
    Column(modifier) {
        SearchActionBar(
            SearchState("", true, state.searchText,
                { callback?.searchTextChange(it) }) { callback?.onBack() }
        )
        Text(
            if (state.popularTagsListVisible) stringResource(R.string.meeting_filter_popular_tags)
            else stringResource(R.string.meeting_filter_search_from_tag),
            Modifier.padding(top = 20.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.H3
        )
        if (state.popularTagsListVisible)
            Card(Modifier.padding(top = 20.dp)) {
                FlowLayout(
                    Modifier
                        .background(ThemeExtra.colors.cardBackground)
                        .padding(16.dp), 4.dp, 8.dp
                ) {
                    state.popularTags.forEach {
                        GiltyChip(
                            Modifier,
                            it,
                            state.tagList.contains(it)
                        ) { callback?.onTagClick(it) }
                    }
                }
            }
        if (state.tagList.isNotEmpty())
            Card(Modifier.padding(top = 16.dp)) {
                Surface {
                    FlowLayout(
                        Modifier
                            .background(ThemeExtra.colors.cardBackground)
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp), 16.dp, 8.dp
                    ) {
                        state.tagList.forEachIndexed { index, item ->
                            Box(
                                Modifier
                                    .clip(MaterialTheme.shapes.large)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Row(
                                    Modifier.padding(12.dp, 6.dp),
                                    Arrangement.Center, Alignment.CenterVertically
                                ) {
                                    Text(
                                        item,
                                        Modifier.padding(end = 10.dp),
                                        Color.White,
                                        style = ThemeExtra.typography.MediumText
                                    )
                                    Icon(
                                        Icons.Filled.Close,
                                        stringResource(R.string.meeting_filter_delete_tag_label),
                                        Modifier.clickable { callback?.onDeleteTag(index) },
                                        Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        if (!state.popularTagsListVisible)
            LazyColumn(
                Modifier
                    .padding(top = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(ThemeExtra.colors.cardBackground)
            ) {
                itemsIndexed(state.allTagList) { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { callback?.onTagClick(item) },
                        Arrangement.SpaceBetween, Alignment.CenterVertically
                    ) {
                        Text(
                            item,
                            Modifier.padding(start = 16.dp),
                            style = ThemeExtra.typography.Body1Medium
                        )
                        if (!state.tagList.contains(item))
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                null,
                                Modifier,
                                Color.White
                            )
                        else CheckBox(true) {}
                    }
                    if (index < state.allTagList.size - 1) Divider(Modifier.padding(start = 16.dp))
                }
            }
        GradientButton(
            Modifier.padding(top = 28.dp),
            stringResource(R.string.meeting_filter_complete_button)
        ) { callback?.onNext() }
    }
}