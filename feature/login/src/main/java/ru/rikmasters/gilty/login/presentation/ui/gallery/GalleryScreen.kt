package ru.rikmasters.gilty.login.presentation.ui.gallery

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.gallery.GalleryContent
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.SINGLE
import ru.rikmasters.gilty.gallery.gallery.GalleryState
import ru.rikmasters.gilty.login.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.shared.R

@Composable
fun GalleryScreen(
    vm: GalleryViewModel,
    multiple: Boolean,
) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val type = if(multiple)
        MULTIPLE else SINGLE
    
    val images by vm.images.collectAsState()
    val selected by vm.selected.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val filters by vm.filters.collectAsState()
    
    Use<GalleryViewModel>(LoadingTrait) {
        GalleryContent(
            GalleryState(
                type, images, selected, menuState,
                filters, (false),
                stringResource(R.string.add_button),
                selected.isNotEmpty(),
            ), Modifier, object: GalleryCallback {
                
                override fun onImageClick(image: String) {
                    scope.launch {
                        if(multiple) vm.selectImage(image)
                        else nav.navigate("cropper?image=$image")
                    }
                }
                
                override fun onMenuItemClick(item: Int) {
                    scope.launch {
                        vm.onMenuItemClick(
                            if(item == 0) "" else
                                filters[item]
                        )
                    }
                }
        
                override fun onMenuDismiss(state: Boolean) {
                    scope.launch { vm.menuDismiss(state) }
                }
                
                override fun onKebabClick() {
                    scope.launch { vm.kebab() }
                }
                
                override fun onAttach() {
                    scope.launch { vm.attach() }
                }
                
                override fun onBack() {
                    nav.navigationBack()
                }
            }
        )
    }
}