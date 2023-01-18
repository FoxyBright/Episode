package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class HiddenViewModel: ViewModel() {
    
    private val _photoList = MutableStateFlow(listOf<String>())
    val photoList = _photoList.asStateFlow()
    
    suspend fun selectImage(image: String){
    
    }
    
    suspend fun deleteImage(image: String){
    
    }
    
    
}