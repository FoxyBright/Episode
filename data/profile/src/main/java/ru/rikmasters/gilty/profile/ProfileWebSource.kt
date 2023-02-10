package ru.rikmasters.gilty.profile

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
import ru.rikmasters.gilty.profile.ProfileWebSource.HiddenType.MY
import ru.rikmasters.gilty.profile.ProfileWebSource.HiddenType.OTHER
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.models.request.profile.ProfileRequest
import ru.rikmasters.gilty.shared.models.response.meets.Avatar
import ru.rikmasters.gilty.shared.models.response.meets.MeetingResponse
import ru.rikmasters.gilty.shared.models.response.notification.MeetWithRespondsResponse
import ru.rikmasters.gilty.shared.models.response.profile.MemberResponse
import ru.rikmasters.gilty.shared.models.response.profile.ProfileResponse
import ru.rikmasters.gilty.shared.wrapper.Status
import ru.rikmasters.gilty.shared.wrapper.errorWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.io.File


class ProfileWebSource: KtorSource() {
    
    enum class MeetingsType { ACTUAL, HISTORY }
    
    enum class ObserversType(val value: String) {
        OBSERVERS("watchers"),
        OBSERVABLES("watch")
    }
    
    suspend fun getUser(id: String): ProfileModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/users/$id"
        ).wrapped<ProfileResponse>().map()
    }
    
    suspend fun getUserMeets(
        type: MeetingsType,
    ): List<MeetingModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/meetings"
        ) {
            url { query("is_completed" to type.ordinal.toString()) }
        }.wrapped<List<MeetingResponse>>().map { it.map() }
    }
    
    suspend fun subscribeToUser(
        memberId: String,
    ): HttpResponse {
        updateClientToken()
        return client.post(
            "http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}"
        ) { url { query("user_id" to memberId) } }
    }
    
    suspend fun unsubscribeFromUser(
        memberId: String,
    ): HttpResponse {
        updateClientToken()
        return client.delete(
            "http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}"
        ) { url { query("user_id" to memberId) } }
    }
    
    suspend fun deleteObserver(
        member: MemberModel,
    ): HttpResponse {
        updateClientToken()
        return client.delete(
            "http://$HOST$PREFIX_URL/profile/${OBSERVERS.value}"
        ) { url { query("user_id" to member.id) } }
    }
    
    suspend fun getObservers(
        type: ObserversType,
    ): List<MemberModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/${type.value}"
        ).wrapped<List<MemberResponse>>().map { it.map() }
    }
    
    suspend fun deleteHidden(image: AvatarModel) {
        updateClientToken()
        val album = getUserAlbumPrivateId()
        val imageId = image.id
        client.delete(
            "http://$HOST$PREFIX_URL/albums/$album/files/$imageId"
        )
    }
    
    enum class HiddenType { MY, OTHER }
    
    suspend fun getProfileHiddens(
        type: HiddenType,
        albumId: String?,
    ): List<Avatar> {
        updateClientToken()
        val album = when(type) {
            MY -> getUserAlbumPrivateId()
            OTHER -> albumId
        }
        return client.get(
            "http://$HOST$PREFIX_URL/albums/$album/files"
        ).wrapped()
    }
    
    suspend fun setHidden(
        files: List<File>,
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
        cropX: Int,
        cropY: Int,
        cropWidth: Int,
        cropHeight: Int,
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
        } catch(e: Exception) {
            false
        }
    }
    
    suspend fun setUserData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientationId: String? = null,
    ) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/profile"
        ) {
            setBody(
                ProfileRequest(
                    username,
                    gender?.name,
                    age,
                    orientationId,
                    aboutMe
                )
            )
        }
    }
    
    private suspend fun getUserAlbumPrivateId(): String {
        val profile = getUserData()
        return profile.hidden?.albumId!!
    }
    
    suspend fun getUserData(): ProfileModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile"
        ).wrapped<ProfileResponse>().map()
    }
    
    suspend fun isUserRegistered(): Boolean {
        updateClientToken()
        val response = client.get(
            "http://$HOST$PREFIX_URL/profile"
        )
        return try {
            response.wrapped<ProfileResponse>().isCompleted == true
        } catch(e: Exception) {
            response.errorWrapped().status != Status.ERROR
        } catch(e: Exception) {
            false
        }
    }
    
    suspend fun deleteAccount() {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/profile/account"
        )
    }
    
    suspend fun getResponds(
        type: RespondType,
    ): List<MeetWithRespondsModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/responds"
        ) {
            url { query("type" to type.name) }
        }.wrapped<List<MeetWithRespondsResponse>>().map { it.map() }
    }
    
    suspend fun deleteRespond(respondId: String) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/responds/$respondId"
        )
    }
    
    suspend fun acceptRespond(respondId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/responds/$respondId/accept"
        )
    }
    
    suspend fun cancelRespond(respondId: String) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/responds/$respondId/cancel"
        )
    }
}