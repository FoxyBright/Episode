package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.shared.compress
import ru.rikmasters.gilty.gallery.gallery.GalleryAdapter.Companion.getImages
import java.io.File


class GalleryViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val context = getKoin().get<Context>()
    
    private val imagesList = getImages(context)
    private val _images = MutableStateFlow(imagesList)
    val images = _images.asStateFlow()
    
    private val _selected = MutableStateFlow(listOf<String>())
    val selected = _selected.asStateFlow()
    
    private val _menuState = MutableStateFlow(false)
    val menuState = _menuState.asStateFlow()
    
    private val filterList = listOf(
        "Все медиа", "WhatsApp Images",
        "Screenshots", "Viber",
        "Telegram", "Camera", "Instagram"
    )
    
    private val _filters = MutableStateFlow(filterList)
    val filters = _filters.asStateFlow()
    
    suspend fun setImage(
        file: File, list: List<Float>,
    ) = singleLoading {
        regManager.setAvatar(file.compress(context), list)
    }
    
    suspend fun selectImage(image: String) {
        val list = selected.value
        _selected.emit(
            if(list.contains(image))
                list - image
            else
                list + image
        )
    }
    
    suspend fun updateImages() {
        _images.emit(getImages(context))
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menuState.emit(state)
    }
    
    suspend fun kebab() {
        makeToast("В меню пока пусто")
    }
    
    suspend fun onMenuItemClick(item: String) {
        _images.emit(getImages(context, item))
        _menuState.emit(false)
    }
    
    suspend fun attach() = singleLoading {
        regManager.addHidden(
            selected.value.map {
                File(it).compress(context)
            }
        )
    }
}