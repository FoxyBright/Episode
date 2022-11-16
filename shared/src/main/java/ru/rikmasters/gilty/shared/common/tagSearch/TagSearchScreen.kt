package ru.rikmasters.gilty.shared.common.tagSearch

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun TagSearchScreen(onBack: () -> Unit, onNext: (List<String>) -> Unit) {
    GiltyTheme {
        val tagList = remember { mutableStateListOf<String>() }
        val searchText = remember { mutableStateOf("") }
        val popularTagsListVisible = remember { mutableStateOf(true) }
        val state = TagState(
            tagList,
            remember { mutableStateListOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly") },
            popularTagsListVisible.value, searchText.value, allTags
        )
        TagSearchContent(state, Modifier.padding(16.dp), object : TagSearchCallback {
            override fun searchTextChange(text: String) {
                popularTagsListVisible.value = text == ""
                searchText.value = text
            }

            override fun onBack() {
                onBack()
            }

            override fun onNext() {
                onNext(tagList)
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