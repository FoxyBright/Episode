package ru.rikmasters.gilty.shared.common.tagSearch

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun TagSearchScreen(
    online: Boolean,
    onBack: () -> Unit,
    onNext: (List<String>) -> Unit,
) {
    val tagList = remember { mutableStateListOf<String>() }
    val searchText = remember { mutableStateOf("") }
    val popularTagsListVisible = remember { mutableStateOf(true) }
    val state = TagState(
        tagList,
        remember { mutableStateListOf("Бэтмэн", "Кингсмен", "Лобби", "Lorem", "Lolly") },
        popularTagsListVisible.value, searchText.value, allTags, online
    )
    TagSearchContent(state, Modifier, object: TagSearchCallback {
        override fun onSearchChange(text: String) {
            popularTagsListVisible.value = text == ""
            searchText.value = text
        }
        
        override fun onBack() {
            onBack()
        }
        
        override fun onNext() {
            onNext(tagList)
        }
        
        override fun onDeleteTag(tag: String) {
            tagList.remove(tag)
        }
        
        override fun onTagClick(tag: String) {
            if(!tagList.contains(tag)) tagList.add(tag)
            else tagList.removeAt(tagList.indexOf(tag))
        }
    })
}