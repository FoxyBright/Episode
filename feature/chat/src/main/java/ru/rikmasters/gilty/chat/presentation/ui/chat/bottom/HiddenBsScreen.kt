package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.viewmodel.HiddenBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@Composable
fun HiddenBs(
    vm: HiddenBsViewModel,
    isOnline: Boolean,
    chatId: String
) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()

    val images = vm.images.collectAsLazyPagingItems()
    val selected by vm.selected.collectAsState()

    LaunchedEffect(Unit) {
        vm.clearSelect()
    }

    Use<HiddenBsViewModel>(LoadingTrait) {
        HiddenBsContent(
            HiddenGalleryState(
                MULTIPLE,
                images,
                selected,
                (false),
                (null),
                isOnline,
                stringResource(R.string.send_button),
                selected.isNotEmpty()
            ),
            Modifier,
            object : GalleryCallback {
                override fun onAttach() {
                    scope.launch {
                        asm.bottomSheet.collapse()
                        vm.sendImages(chatId)
                    }
                }

                override fun onHiddenImageClick(image: AvatarModel) {
                    scope.launch { vm.selectImage(image) }
                }
            }
        )
    }
}
