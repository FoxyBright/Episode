package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.TagsViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.tag.TagsCallback
import ru.rikmasters.gilty.shared.shared.tag.TagsContent
import ru.rikmasters.gilty.shared.shared.tag.TagsState

@Composable
fun TagsScreen(vm: TagsViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val selected by vm.selected.collectAsState()
    val category by vm.category.collectAsState()
    val popular by vm.popular.collectAsState()
    val tags by vm.tags.collectAsState()
    val online by vm.online.collectAsState()
    val search by vm.search.collectAsState()
    
    LaunchedEffect(Unit) { vm.getPopular() }
    
    TagsContent(
        state = TagsState(
            selected = selected,
            populars = popular,
            search = search,
            searchResult = tags,
            isOnline = online,
            category = category,
            add = true,
            alpha = 0f
        ),
        callback = object: TagsCallback {
            
            override fun onTagClick(tag: TagModel) {
                scope.launch {
                    vm.selectTag(tag)
                    vm.searchClear()
                }
            }
            
            override fun onSearchChange(text: String) {
                scope.launch { vm.searchChange(text) }
            }
            
            override fun onCreateTag(tagName: String) {
                scope.launch {
                    if(tagName.isNotBlank())
                        vm.selectTag(TagModel(tagName))
                    vm.searchClear()
                }
            }
            
            override fun onComplete() {
                scope.launch {
                    vm.onSave(selected)
                    nav.navigationBack()
                }
            }
            
            override fun onDeleteTag(tag: TagModel) {
                scope.launch { vm.deleteTag(tag) }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}
