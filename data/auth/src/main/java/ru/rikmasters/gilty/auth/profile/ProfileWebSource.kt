package ru.rikmasters.gilty.auth.profile

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders.ContentDisposition
import io.ktor.http.HttpHeaders.ContentType
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import java.io.File


class ProfileWebSource: KtorSource() {
    
    enum class GenderType(val value: String) {
        Female("FEMALE"),
        Male("MALE"),
        Other("OTHER");
        
        companion object {
            
            fun valueOf(index: Int) = values()[index]
        }
    }
    
    suspend fun setUserAvatar(
        avatar: File,
        cropX: Int = 100,
        cropY: Int = 100,
        cropWidth: Int = 100,
        cropHeight: Int = 100
    ) = client.post(
        "http://$HOST$PREFIX_URL/profile/avatar"
    ) {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        "photo", avatar.readBytes(),
                        Headers.build {
                            append(ContentType, "image/jpg")
                            append(ContentDisposition, "filename=\"photo.jpg\"")
                        },
                    )
                    append("crop_x", cropX)
                    append("crop_y", cropY)
                    append("crop_width", cropWidth)
                    append("crop_height", cropHeight)
                },
                boundary = "WebAppBoundary"
            )
        )
    }.body<String?>()
    
    suspend fun setUserData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientationId: String? = "HETERO"
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