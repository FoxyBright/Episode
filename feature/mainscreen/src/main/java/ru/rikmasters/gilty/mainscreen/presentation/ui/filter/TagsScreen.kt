package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.SoftInputAdjust.Nothing
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.tag.TagsCallback
import ru.rikmasters.gilty.shared.shared.tag.TagsContent
import ru.rikmasters.gilty.shared.shared.tag.TagsState

@Composable
fun TagSearchScreen(vm: FiltersBsViewModel, alpha: Float) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val searchResult by vm.searchResult.collectAsState()
    val selected by vm.additionallyTags.collectAsState()
    val popular by vm.popularTags.collectAsState()
    val isOnline by vm.isOnline.collectAsState()
    val search by vm.tagSearch.collectAsState()
    
    DisposableEffect(Unit) {
        asm.keyboard.setSoftInputMode(Nothing)
        onDispose { asm.keyboard.resetSoftInputAdjust() }
    }
    
    LaunchedEffect(Unit) {
        vm.getPopularTags()
        vm.fullAdditionallyTags()
    }
    
    TagsContent(
        TagsState(
            selected, popular, search,
            searchResult, isOnline,
            (null), (false), alpha
        ), Modifier, object: TagsCallback {
            
            override fun onTagClick(tag: TagModel) {
                scope.launch {
                    vm.selectTag(tag)
                    vm.clearTagSearch()
                }
            }
            
            override fun onComplete() {
                scope.launch {
                    vm.saveTags()
                    vm.navigate(0)
                }
            }
            
            override fun onCreateTag(tagName: String) {
                scope.launch {
                    vm.selectTag(TagModel(tagName))
                    vm.clearTagSearch()
                }
            }
            
            override fun onDeleteTag(tag: TagModel) {
                scope.launch { vm.deleteAdditionallyTag(tag) }
            }
            
            override fun onSearchChange(text: String) {
                scope.launch { vm.searchTags(text) }
            }
            
            override fun onBack() {
                scope.launch { vm.navigate(0) }
            }
        }
    )
}