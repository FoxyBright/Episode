package ru.rikmasters.gilty.translation.bottoms

import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

sealed interface PreviewEvent {
    data class ToggleFacing(val facing: StreamerFacing) : PreviewEvent
}