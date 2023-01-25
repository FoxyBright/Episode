package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.TagsViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.tagSearch.*

@Composable
fun TagsScreen(vm: TagsViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val selected by vm.selected.collectAsState()
    val search by vm.search.collectAsState()
    val popularVisible by vm.popularVisible.collectAsState()
    val popular by vm.popular.collectAsState()
    val tags by vm.tags.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getPopular()
    }
    
    TagSearchContent(TagState(
        selected, popular,
        popularVisible,
        search, tags, Online
    ), Modifier, object: TagSearchCallback {
        override fun onSearchChange(text: String) {
            scope.launch { vm.searchChange(text) }
        }
        
        override fun onBack() {
            nav.navigationBack()
        }
        
        override fun onNext() {
            scope.launch {
                vm.onSave()
                nav.navigationBack()
            }
        }
        
        override fun onDeleteTag(tag: String) {
            scope.launch { vm.deleteTag(tag) }
        }
        
        override fun onTagClick(tag: String) {
            scope.launch { vm.selectTag(tag) }
        }
    })
}