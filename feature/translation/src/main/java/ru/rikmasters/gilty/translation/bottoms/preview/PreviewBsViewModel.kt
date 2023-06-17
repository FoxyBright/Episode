package ru.rikmasters.gilty.translation.bottoms.preview

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

class PreviewBsViewModel : ViewModel() {

    private val _selectedFacing = MutableStateFlow(StreamerFacing.FRONT)
    val selectedFacing = _selectedFacing.asStateFlow()

    fun onEvent(event: PreviewEvent) {
        when(event) {
            is PreviewEvent.ToggleFacing -> {
                _selectedFacing.value = event.facing
            }
        }
    }

}