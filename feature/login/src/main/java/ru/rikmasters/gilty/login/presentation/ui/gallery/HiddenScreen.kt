package ru.rikmasters.gilty.login.presentation.ui.gallery

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.HiddenViewModel
import ru.rikmasters.gilty.shared.common.HiddenCallback
import ru.rikmasters.gilty.shared.common.HiddenContent
import ru.rikmasters.gilty.shared.common.HiddenState

@Composable
fun HiddenScreen(
    vm: HiddenViewModel,
) {
    
    val photoList by vm.photoList.collectAsState()
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    LaunchedEffect(Unit) { vm.getHidden() }
    
    HiddenContent(HiddenState(photoList), Modifier,
        object: HiddenCallback {
            
            override fun onSelectImage(image: String) {
                scope.launch { vm.selectImage(image) }
            }
            
            override fun onDeleteImage(image: String) {
                scope.launch { vm.deleteImage(image) }
            }
            
            override fun openGallery() {
                nav.navigate("gallery?multi=true")
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    vm.onNext()
                    nav.navigate("profile")
                }
            }
        }
    )
}
