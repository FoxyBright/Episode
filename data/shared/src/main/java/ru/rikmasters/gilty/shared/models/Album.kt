package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.enumeration.AlbumType.valueOf
import ru.rikmasters.gilty.shared.model.image.AlbumModel

data class Album(
    val id: String,
    val type: String? = null,
    val preview: Avatar? = null,
    val hasAccess: Boolean,
) {
    
    fun map() = AlbumModel(
        id, type?.let { valueOf(it) },
        preview?.map(), hasAccess
    )
}
