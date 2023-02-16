package ru.rikmasters.gilty.shared.model.image

import ru.rikmasters.gilty.shared.model.enumeration.AlbumType
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

data class AlbumModel(
    val id: String,
    val type: AlbumType,
    val preview: AvatarModel? = null,
    val hasAccess: Boolean,
)
