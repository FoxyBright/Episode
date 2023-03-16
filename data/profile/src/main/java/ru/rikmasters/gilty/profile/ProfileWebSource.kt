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
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.Category
import ru.rikmasters.gilty.shared.models.meets.Meeting
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.io.File


class ProfileWebSource: KtorSource() {
    
    enum class ObserversType(val value: String) {
        OBSERVERS("watchers"),
        OBSERVABLES("watch")
    }
    
    suspend fun getUser(id: String): ProfileModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/users/$id"
        ).wrapped<Profile>().map()
    }
    
    suspend fun getUserMeets(
        type: MeetingsType,
    ): List<Meeting> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/meetings"
        ) {
            url { query("is_completed" to type.ordinal.toString()) }
        }.wrapped()
    }
    
    suspend fun getUserCategories(): List<Category> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/categories"
        ).wrapped()
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
        member: UserModel,
    ): HttpResponse {
        updateClientToken()
        return client.delete(
            "http://$HOST$PREFIX_URL/profile/${OBSERVERS.value}"
        ) { url { query("user_id" to member.id.toString()) } }
    }
    
    suspend fun getObservers(
        query: String,
        type: ObserversType,
    ): List<UserModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/${type.value}${
                if(query.isNotBlank()) "?query=$query" else ""
            }"
        ).wrapped<List<User>>().map { it.map() }
    }
    
    suspend fun deleteHidden(
        albumId: String,
        imageId: String,
    ) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/albums/$albumId/files/$imageId"
        )
    }
    
    suspend fun getFiles(albumId: String): List<Avatar> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/albums/$albumId/files"
        ).wrapped()
    }
    
    suspend fun addHidden(
        albumId: String,
        files: List<File>,
    ): List<Avatar> {
        updateClientToken()
        return client.post(
            "http://$HOST$PREFIX_URL/albums/$albumId/upload"
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
        }.wrapped()
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
        username: String?,
        aboutMe: String?,
        age: Int?,
        gender: GenderType?,
        orientationId: String?,
    ) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/profile"
        ) {
            setBody(
                ProfileRequest(
                    username, gender?.name,
                    age, orientationId, aboutMe
                )
            )
        }
    }
    
    suspend fun getUserData(): Profile {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile"
        ).wrapped()
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