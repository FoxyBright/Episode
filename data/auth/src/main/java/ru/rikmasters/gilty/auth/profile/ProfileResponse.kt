package ru.rikmasters.gilty.auth.profile

data class ProfileResponse(
    
    val id: String,
    val phone: String?,
    val username: String?,
    val gender: Any?,
    val orientation: Any?,
    val age: Any?,
    val about_me: Any?,
    val emoji_type: Any?,
    val average: Any?,
    val avatar: Any?,
    val subscription_expired_at: Any?,
    val thumbnail: Any?,
    val responds: Any?,
    val album_private: Any?,
    val count_watchers: Any?,
    val count_watching: Any?,
    val is_watching: Any?,
    val unblock_at: Any?,
    val is_completed: Any?,
    val is_online: Any?,
    val is_anonymous: Any?,
    val status: Any?
)