package ru.rikmasters.gilty.auth.profile

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders.ContentDisposition
import io.ktor.http.HttpHeaders.ContentType
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.wrapper.*
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
    
    suspend fun setHidden(
        files: List<File>
    ): HttpResponse {
        updateClientToken()
        val album = getUserAlbumPrivateId()
        val response = client.post(
            "http://$HOST$PREFIX_URL/albums/$album/upload"
        ) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("type", "PHOTO")
                        files.forEach {
                            append(
                                "photos[]", it.readBytes(),
                                Headers.build {
                                    append(ContentType, "image/jpg")
                                    append(ContentDisposition, "filename=\"${it.name}\"")
                                },
                            )
                        }
                    }, "WebAppBoundary"
                )
            )
        }
        
        return response
    }
    
    suspend fun setUserAvatar(
        avatar: File,
        cropX: Int = 100,
        cropY: Int = 100,
        cropWidth: Int = 100,
        cropHeight: Int = 100
    ): HttpResponse {
        updateClientToken()
        return client.post(
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
                    }, "WebAppBoundary"
                )
            )
        }
    }
    
    suspend fun checkUserName(username: String): Boolean {
        updateClientToken()
        val response = client.get(
            "http://$HOST$PREFIX_URL/profile/checkname"
        ) { url { query("username" to username) } }
        
        data class Status(val status: String)
        
        return try {
            response.body<Status>().status == "error"
        } catch(_: Exception) {
            false
        }
    }
    
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
    }
    
    private suspend fun getUserAlbumPrivateId(): String {
        val profile = getUserData()
        return profile.album_private?.id!!
    }
    
    suspend fun getUserData(): ProfileResponse {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile"
        ) {}.wrapped()
    }
    
    suspend fun isUserRegistered(): Boolean {
        updateClientToken()
        val response = client.get(
            "http://$HOST$PREFIX_URL/profile"
        )
        return try {
            response.wrapped<ProfileResponse>().is_completed == true
        } catch(_: Exception) {
            response.errorWrapped().status != Status.ERROR
        } catch(_: Exception) {
            false
        }
    }
}