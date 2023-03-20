package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.TagsViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.TagModel

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
        TagsState(
            search, selected, popular,
            online, tags, category
        ), Modifier, object: TagsCallback {
            
            override fun onTagClick(tag: TagModel) {
                scope.launch {
                    vm.selectTag(tag)
                    if(!popular.contains(tag))
                        vm.addToPopular(tag)
                    vm.searchClear()
                }
            }
            
            override fun onSave() {
                scope.launch {
                    vm.onSave(selected)
                    nav.navigationBack()
                }
            }
            
            override fun onAddTag(text: String) {
                scope.launch { vm.searchChange(text) }
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