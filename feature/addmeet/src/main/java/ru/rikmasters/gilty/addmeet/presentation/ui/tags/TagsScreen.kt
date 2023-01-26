package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.SelectCategory
import ru.rikmasters.gilty.addmeet.viewmodel.TagsViewModel
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun TagsScreen(vm: TagsViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val selected by vm.selected.collectAsState()
    val search by vm.search.collectAsState()
    val popular by vm.popular.collectAsState()
    val tags by vm.tags.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getPopular()
    }
    
    TagsContent(TagsState(
        search, selected, popular,
        Online, tags, SelectCategory
    ), Modifier, object: TagsCallback {
        
        override fun onAddTag(text: String) {
            scope.launch { vm.searchChange(text) }
        }
        
        override fun onSave() {
            scope.launch {
                vm.onSave()
                nav.navigationBack()
            }
        }
        
        override fun onBack() {
            nav.navigationBack()
        }
        
        override fun onDeleteTag(tag: String) {
            scope.launch { vm.deleteTag(tag) }
        }
        
        override fun onTagClick(tag: String) {
            scope.launch {
                vm.selectTag(tag)
                if(!popular.contains(tag))
                    vm.addToPopular(tag)
                vm.searchClear()
            }
        }
    })
}