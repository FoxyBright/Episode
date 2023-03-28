package ru.rikmasters.gilty.profile

import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
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
    
    suspend fun getUser(id: String) =
        get("http://$HOST$PREFIX_URL/users/$id")!!
            .wrapped<Profile>().map()
    
    suspend fun getUserMeets(type: MeetingsType) =
        get("http://$HOST$PREFIX_URL/profile/meetings") {
            url { query("is_completed" to type.ordinal.toString()) }
        }!!.wrapped<List<Meeting>>()
    
    suspend fun getUserCategories() = get(
        "http://$HOST$PREFIX_URL/profile/categories"
    )!!.wrapped<List<Category>>()
    
    suspend fun subscribeToUser(memberId: String) {
        post("http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}") {
            url { query("user_id" to memberId) }
        }
    }
    
    suspend fun unsubscribeFromUser(memberId: String) {
        delete("http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}") {
            url { query("user_id" to memberId) }
        }
    }
    
    suspend fun deleteObserver(member: UserModel) {
        delete("http://$HOST$PREFIX_URL/profile/${OBSERVERS.value}") {
            url { query("user_id" to member.id.toString()) }
        }
    }
    
    suspend fun getObservers(type: ObserversType) =
        get("http://$HOST$PREFIX_URL/profile/${type.value}")!!
            .wrapped<List<User>>().map { it.map() }
    
    suspend fun deleteHidden(albumId: String, imageId: String) {
        delete("http://$HOST$PREFIX_URL/albums/$albumId/files/$imageId")
    }
    
    suspend fun getFiles(albumId: String) =
        get("http://$HOST$PREFIX_URL/albums/$albumId/files")!!
            .wrapped<List<Avatar>>()
    
    suspend fun addHidden(albumId: String, files: List<File>) =
        post("http://$HOST$PREFIX_URL/albums/$albumId/upload") {
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
                    },
                    ("WebAppBoundary")
                )
            )
        }!!.wrapped<List<Avatar>>()
    
    suspend fun setUserAvatar(
        avatar: File, list: List<Float>,
    ) {
        post("http://$HOST$PREFIX_URL/profile/avatar") {
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
                        append("crop_x", list[0])
                        append("crop_y", list[1])
                        append("crop_width", list[2])
                        append("crop_height", list[3])
                    }, "WebAppBoundary"
                )
            )
        }
    }
    
    private data class Status(val status: String)
    
    suspend fun checkUserName(username: String) = try {
        get("http://$HOST$PREFIX_URL/profile/checkname")
        { url { query("username" to username) } }
            ?.let { it.body<Status>().status == "error" }
            ?: false
    } catch(e: Exception) {
        false
    }
    
    suspend fun setUserData(
        username: String?, aboutMe: String?, age: Int?,
        gender: GenderType?, orientationId: String?,
    ) {
        patch("http://$HOST$PREFIX_URL/profile") {
            setBody(
                ProfileRequest(
                    username, gender?.name,
                    age, orientationId, aboutMe
                )
            )
        }
    }
    
    suspend fun getUserData() =
        get("http://$HOST$PREFIX_URL/profile")
            ?.wrapped<Profile>() ?: Profile()
    
    suspend fun deleteAccount() {
        delete("http://$HOST$PREFIX_URL/profile/account")
    }
    
    suspend fun getResponds(type: RespondType) =
        get("http://$HOST$PREFIX_URL/responds") {
            url { query("type" to type.name) }
        }!!.wrapped<List<MeetWithRespondsResponse>>().map { it.map() }
    
    suspend fun deleteRespond(respondId: String) {
        delete("http://$HOST$PREFIX_URL/responds/$respondId")
    }
    
    suspend fun acceptRespond(respondId: String) {
        post("http://$HOST$PREFIX_URL/responds/$respondId/accept")
    }
    
    suspend fun cancelRespond(respondId: String) {
        patch("http://$HOST$PREFIX_URL/responds/$respondId/cancel")
    }
}