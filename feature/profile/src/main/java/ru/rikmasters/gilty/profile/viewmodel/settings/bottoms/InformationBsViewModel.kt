package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import ru.rikmasters.gilty.core.viewmodel.ViewModel

class InformationBsViewModel: ViewModel() {
    
    suspend fun getInformation(item: Int) {
        makeToast("$item - Информации пока нет!")
    }
}