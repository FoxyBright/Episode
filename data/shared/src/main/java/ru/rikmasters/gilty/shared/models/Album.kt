package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.enumeration.AlbumType
import ru.rikmasters.gilty.shared.model.image.AlbumModel
import ru.rikmasters.gilty.shared.models.meets.Avatar

data class Album(
    val id: String,
    val type: String,
    val preview: Avatar? = null,
    val hasAccess: Boolean,
) {
    
    fun map() = AlbumModel(
        id, AlbumType.valueOf(type),
        preview?.map(), hasAccess
    )
}
