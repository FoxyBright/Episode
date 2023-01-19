package ru.rikmasters.gilty.login.viewmodel

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Environment.isExternalStorageManager
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.getImages


class GalleryViewModel: ViewModel() {
    
    private val context = getKoin().get<Context>()
    
    private val imagesList = getImages(context)
    private val _images = MutableStateFlow(imagesList)
    val images = _images.asStateFlow()
    
    private val _selected = MutableStateFlow(listOf<String>())
    val selected = _selected.asStateFlow()
    
    private val _permissions = MutableStateFlow(checkStoragePermission())
    val permissions = _permissions.asStateFlow()
    
    private val _menuState = MutableStateFlow(false)
    val menuState = _menuState.asStateFlow()
    
    private val filterList = listOf(
        "Все медиа", "WhatsApp Images",
        "Screenshots", "Viber",
        "Telegram", "Camera", "Instagram"
    )
    private val _filters = MutableStateFlow(filterList)
    val filters = _filters.asStateFlow()
    
    suspend fun selectImage(image: String) {
        val list = selected.value
        _selected.emit(
            if(list.contains(image))
                list - image
            else
                list + image
        )
    }
    
    suspend fun setPermissions(state: Boolean) {
        _permissions.emit(state)
    }
    
    private fun checkStoragePermission() =
        if(Build.VERSION.SDK_INT >= 30) {
            isExternalStorageManager()
        } else checkSelfPermission(
            context, WRITE_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED
    
    
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
    
    fun imageClick(image: String, points: List<Int>) {
        Avatar = image
        ListPoints = points
    }
    
    fun attach() {
        ListHidden = selected.value
        Hidden = selected.value.first()
    }
}