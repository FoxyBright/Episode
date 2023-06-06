package ru.rikmasters.gilty.chat.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.gallery.GalleryAdapter.getImages
import java.io.File

class GalleryViewModel(
    
    private val chatVm: ChatViewModel,
): ViewModel() {
    
    private val context = getKoin().get<Context>()
    
    private val _images = MutableStateFlow(emptyList<String>())
    val images = _images.asStateFlow()
    
    private val _selected = MutableStateFlow(emptyList<String>())
    val selected = _selected.asStateFlow()
    
    suspend fun clearSelect() {
        _selected.emit(emptyList())
    }
    
    suspend fun getImages() {
        _images.emit(getImages(context))
    }
    
    suspend fun sendImages(
        chatId: String,
    ) = singleLoading {
        chatVm.onSendMessage(
            chatId = chatId,
            photos = selected.value
                .map { File(it) }
        )
    }
    
    suspend fun selectImage(path: String) {
        val list = selected.value
        _selected.emit(
            if(list.contains(path))
                list - path
            else list + path
        )
    }
}