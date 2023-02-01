package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.mainscreen.viewmodels.FiltersViewModel
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchCallback
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchContent
import ru.rikmasters.gilty.shared.common.tagSearch.TagState
import ru.rikmasters.gilty.shared.model.meeting.TagModel

@Composable
fun TagSearchScreen(vm: FiltersViewModel) {
    
    val scope = rememberCoroutineScope()
    
    val online by vm.online.collectAsState()
    
    val searchResult by vm.searchResult.collectAsState()
    val selected by vm.additionallyTags.collectAsState()
    val popular by vm.popularTags.collectAsState()
    val search by vm.tagSearch.collectAsState()
    val popularState by vm.popularVisible.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getPopularTags()
        vm.fullAdditionallyTags()
    }
    
    TagSearchContent(TagState(
        selected, popular, popularState,
        search, searchResult, online
    ), Modifier, object: TagSearchCallback {
        override fun onSearchChange(text: String) {
            scope.launch { vm.searchTags(text) }
        }
        
        override fun onBack() {
            scope.launch { vm.navigate(0) }
        }
        
        override fun onComplete() {
            scope.launch {
                vm.saveTags()
                vm.navigate(0)
            }
        }
        
        override fun onDeleteTag(tag: TagModel) {
            scope.launch { vm.deleteAdditionallyTag(tag) }
        }
        
        override fun onTagClick(tag: TagModel) {
            scope.launch {
                vm.selectTag(tag)
                if(!popular.contains(tag))
                    vm.addToPopular(tag)
                vm.clearTagSearch()
            }
        }
    })
}