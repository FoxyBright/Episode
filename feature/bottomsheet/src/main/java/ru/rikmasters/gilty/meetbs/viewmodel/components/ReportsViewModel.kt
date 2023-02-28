package ru.rikmasters.gilty.meetbs.viewmodel.components

import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.BsViewModel

class ReportsViewModel(
    private val bsVm: BsViewModel = BsViewModel(),
): ViewModel() {
    
    suspend fun dismissAlertState(state: Boolean) {
        bsVm.dismissAlertState(state)
    }
}