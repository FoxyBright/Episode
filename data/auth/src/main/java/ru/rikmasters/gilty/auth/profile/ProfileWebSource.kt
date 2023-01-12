package ru.rikmasters.gilty.auth.profile

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import java.io.File

class ProfileWebSource: KtorSource() {
    
    enum class GenderType(val value: String) {
        Male("MALE"),
        Female("FEMALE"),
        Other("OTHER")
    }
    
    suspend fun setUserAvatar(
        avatar: File,
        cropX: Int = 0,
        cropY: Int = 0,
        cropWidth: Int = 0,
        cropHeight: Int = 0,
    ) = client.post(
        "http://$HOST$PREFIX_URL/profile/avatar"
    ) {
        formData {
            append("photo", avatar.readBytes())
            append("crop_x", cropX)
            append("crop_y", cropY)
            append("crop_width", cropWidth)
            append("crop_height", cropHeight)
        }
    }.body<String?>()
    
    suspend fun setUserData(
        username: String? = null,
        gender: GenderType? = null,
        age: Int? = null,
        orientationId: String? = null,
        aboutMe: String? = null,
    ) = client.patch(
        "http://$HOST$PREFIX_URL/profile"
    ) {
        setBody(
            ProfileRequest(
                username,
                gender?.value,
                age,
                orientationId,
                aboutMe
            )
        )
    }.body<String?>()
    
    suspend fun isUserRegistered(): Boolean = client.get(
        "http://$HOST$PREFIX_URL/profile"
    ).body<String?>()?.let {
        when {
            it.contains("\"status\":\"error\"") -> false // TODO Сделать нормальный парсер
            it.contains("\"username\":null") -> false
            else -> true
        }
    } ?: false
}