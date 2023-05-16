package ru.rikmasters.gilty.profile

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders.ContentDisposition
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.Category
import ru.rikmasters.gilty.shared.models.meets.Meeting
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator
import ru.rikmasters.gilty.shared.wrapper.errorWrapped
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.io.File

class ProfileWebSource: KtorSource() {
    
    enum class ObserversType(val value: String) {
        OBSERVERS("watchers"),
        OBSERVABLES("watch")
    }
    
    suspend fun getUser(id: String) =
        get("http://$HOST$PREFIX_URL/users/$id")
            ?.let {
                if(it.status == OK)
                    it.wrapped<Profile>().map()
                else null
            } ?: ProfileModel()
    
    suspend fun getUserMeets(
        page: Int? = null,
        perPage: Int? = null,
        type: MeetingsType,
    ) = get("http://$HOST$PREFIX_URL/profile/meetings") {
        url {
            page?.let { query("page" to "$it") }
            query("is_completed" to type.ordinal.toString())
            perPage?.let { query("per_page" to "$it") }
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<Meeting>() to Paginator())
    
    
    suspend fun getUserCategories() = get(
        "http://$HOST$PREFIX_URL/profile/categories"
    )?.let {
        if(it.status == OK)
            it.wrapped<List<Category>>()
        else null
    } ?: emptyList()
    
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
    
    suspend fun getObservers(
        query: String,
        type: ObserversType,
        @Suppress("unused_parameter")
        page: Int = 0,
        @Suppress("unused_parameter")
        perPage: Int = 15,
    ) = get(
        "http://$HOST$PREFIX_URL/profile/${type.value}${
            if(query.isNotBlank()) "?query=$query" else ""
        }"
    ) {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<User>() to Paginator())
    
    suspend fun deleteHidden(albumId: String, imageId: String) {
        delete("http://$HOST$PREFIX_URL/albums/$albumId/files/$imageId")
    }
    
    suspend fun getFiles(albumId: String): Pair<List<Avatar>, Int> = get(
        "http://$HOST$PREFIX_URL/albums/$albumId/files"
    )?.let {
        if(it.status == OK) {
            val amount = it.paginateWrapped<List<Avatar>>()
            Pair(amount.first, amount.second.total)
        } else null
    } ?: (Pair(emptyList(), 0))
    
    suspend fun addHidden(albumId: String, files: List<File>) =
        postFormData(
            "http://$HOST$PREFIX_URL/albums/$albumId/upload",
            formData {
                append("type", "PHOTO")
                files.forEach {
                    append(
                        "photos[]",
                        it.readBytes(),
                        Headers.build {
                            append(ContentType, "image/jpg")
                            append(
                                ContentDisposition,
                                "filename=\"${it.name}\""
                            )
                        }
                    )
                }
            }
        )?.let {
            if(it.status == OK)
                it.wrapped<List<Avatar>>()
            else null
        } ?: emptyList()
    
    suspend fun setUserAvatar(
        avatar: File,
        list: List<Float>,
    ) {
        postFormData(
            "http://$HOST$PREFIX_URL/profile/avatar",
            formData {
                append(
                    "photo",
                    avatar.readBytes(),
                    Headers.build {
                        append(ContentType, "image/jpg")
                        append(
                            ContentDisposition,
                            "filename=\"photo.jpg\""
                        )
                    }
                )
                append("crop_x", list[0])
                append("crop_y", list[1])
                append("crop_width", list[2])
                append("crop_height", list[3])
            }
        )
    }
    
    suspend fun checkUserName(username: String) = unExpectGet(
        "http://$HOST$PREFIX_URL/profile/checkname"
    ) { url { query("username" to username) } }
        .let {
            if(it.status == OK) false
            else it.errorWrapped().error.message ==
                    "errors.user.username.exists"
        }
    
    suspend fun setUserData(
        username: String?,
        aboutMe: String?,
        age: Int?,
        gender: GenderType?,
        orientationId: String?,
    ) {
        patch("http://$HOST$PREFIX_URL/profile") {
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
    
    suspend fun getUserData() = get(
        "http://$HOST$PREFIX_URL/profile"
    )?.let {
        if(it.status == OK)
            it.wrapped<Profile>()
        else null
    } ?: Profile()
    
    suspend fun deleteAccount() {
        delete("http://$HOST$PREFIX_URL/profile/account")
    }
    
    suspend fun getResponds(
        type: RespondType, page: Int?, perPage: Int?,
    ) = get("http://$HOST$PREFIX_URL/responds") {
        url {
            query("type" to type.name)
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<MeetWithRespondsResponse>() to Paginator())
    
    suspend fun getFilesPaging(
        albumId: String,
        page: Int,
        perPage: Int,
    ) = get("http://$HOST$PREFIX_URL/albums/$albumId/files") {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<Avatar>() to Paginator())
    
    suspend fun getMeetResponds(
        meetId: String,
        page: Int?,
        perPage: Int?,
    ) = get("http://$HOST$PREFIX_URL/meetings/$meetId/responds") {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<RespondResponse>() to Paginator())
    
    suspend fun deleteRespond(respondId: String) {
        delete("http://$HOST$PREFIX_URL/responds/$respondId")
    }
    
    suspend fun getNotificationResponds() = getResponds(
        RECEIVED, null, null
    ).first.firstOrNull().let { respond ->
        (respond?.responds_count ?: 0) to
                (respond?.responds?.firstOrNull()
                    ?.author?.avatar?.thumbnail?.url ?: "")
    }
    
    suspend fun acceptRespond(respondId: String) {
        post("http://$HOST$PREFIX_URL/responds/$respondId/accept")
    }
    
    suspend fun cancelRespond(respondId: String) {
        patch("http://$HOST$PREFIX_URL/responds/$respondId/cancel")
    }
}
