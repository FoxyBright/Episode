package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var ListHidden: List<String> = listOf()

class HiddenViewModel: ViewModel() {
    
    private val _photoList = MutableStateFlow(ListHidden)
    val photoList = _photoList.asStateFlow()
    
    suspend fun selectImage(image: String) {
        if(!photoList.value.contains(image))
            _photoList.emit(photoList.value + image)
        ListHidden = photoList.value
    }
    
    suspend fun deleteImage(image: String) {
        if(photoList.value.contains(image))
            _photoList.emit(photoList.value - image)
        ListHidden = photoList.value
    }
}