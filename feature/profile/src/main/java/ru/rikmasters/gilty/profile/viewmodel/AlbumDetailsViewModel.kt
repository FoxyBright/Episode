package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel

class AlbumDetailsViewModel: ViewModel() {

    private val _image = MutableStateFlow("")
    val image = _image.asStateFlow()

    private val _topEmoji = MutableStateFlow(DemoEmojiModel)
    val topEmoji = _topEmoji.asStateFlow()

    suspend fun loadAlbum(albumId:Int){
        // TODO Load album data from server
    }
}