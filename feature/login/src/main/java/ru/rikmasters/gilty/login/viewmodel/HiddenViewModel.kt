package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.shared.compress
import java.io.File

class HiddenViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val context = getKoin().get<Context>()
    
    private val _photoList = MutableStateFlow(emptyList<String>())
    val photoList = _photoList.asStateFlow()
    
    private val hiddenList = MutableStateFlow(emptyList<String>())

    private val _photosAmount = MutableStateFlow(0)
    val photosAmount = _photosAmount.asStateFlow()
    
    suspend fun getHidden() = singleLoading {
        regManager.getProfile().hidden?.let {
            val response = regManager.getHidden(it.albumId)
            _photoList.emit(response.first)
            hiddenList.emit(response.first)
            _photosAmount.emit(response.second)
        }
    }
    
    suspend fun selectImage(image: String) {
        if(!photoList.value.contains(image))
            _photoList.emit(photoList.value + image)
    }
    
    suspend fun deleteImage(image: String) = singleLoading {
        if(photoList.value.contains(image))
            _photoList.emit(photoList.value - image)
    }
    
    suspend fun onNext() = singleLoading {
        regManager.deleteHidden(hiddenList.value)
        regManager.addHidden(photoList.value.map {
            File(it).compress(context)
        })
    }
}