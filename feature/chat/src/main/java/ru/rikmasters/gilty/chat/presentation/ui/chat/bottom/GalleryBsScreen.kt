package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.gallery.gallery.GalleryBsContent
import ru.rikmasters.gilty.gallery.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.gallery.gallery.GalleryState
import ru.rikmasters.gilty.shared.R

@Composable
fun GalleryBs(
    vm: GalleryViewModel,
    isOnline: Boolean,
    chatId: String,
) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selected by vm.selected.collectAsState()
    val images by vm.images.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.clearSelect()
        vm.getImages()
    }
    
    GalleryBsContent(
        state = GalleryState(
            type = MULTIPLE,
            images = images,
            selectedImages = selected,
            menuState = false,
            menuItems = null,
            isOnline = isOnline,
            buttonLabel = stringResource(R.string.send_button),
            buttonEnabled = selected.isNotEmpty()
        ),
        callback = object: GalleryCallback {
            
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
