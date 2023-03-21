package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.viewmodel.HiddenBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.gallery.gallery.GalleryState
import ru.rikmasters.gilty.shared.R

@Composable
fun HiddenBs(
    vm: HiddenBsViewModel,
    isOnline: Boolean,
    chatId: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val images by vm.images.collectAsState()
    val selected by vm.selected.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.clearSelect()
        vm.getImages(false)
    }
    
    Use<HiddenBsViewModel>(LoadingTrait) {
        HiddenBsContent(
            GalleryState(
                MULTIPLE,
                images.map { it.thumbnail.url },
                selected.map { it.thumbnail.url },
                (false), (null), isOnline,
                stringResource(R.string.send_button),
                selected.isNotEmpty()
            ), Modifier, object: GalleryCallback {
                
                override fun onAttach() {
                    scope.launch {
                        asm.bottomSheet.collapse()
                        vm.sendImages(chatId)
                    }
                }
                
                override fun onImageClick(image: String) {
                    scope.launch { vm.selectImage(image) }
                }
            }
        )
    }
}