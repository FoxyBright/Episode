package ru.rikmasters.gilty.translations.models

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.models.Avatar
import ru.rikmasters.gilty.shared.models.Rating

data class TranslationMessageAuthor(
    val id: String,
    val gender: GenderType,
    val username: String,
    val group: GroupType,
    // TODO: parse emoji
    val emojiType: String,
    val avatar: Avatar,
    val age: Int,
    val meetingRating: Rating,
    val unlockAt: String,
    val subscriptionExpiredAt: String,
    val isAnonymous: Boolean,
    val isOnline: Boolean
)
