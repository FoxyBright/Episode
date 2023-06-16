package ru.rikmasters.gilty.profile

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders.ContentDisposition
import io.ktor.http.HttpHeaders.ContentType
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVABLES
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType.OBSERVERS
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.Category
import ru.rikmasters.gilty.shared.models.meets.Meeting
import ru.rikmasters.gilty.shared.wrapper.*
import java.io.File

class ProfileWebSource : KtorSource() {

    enum class ObserversType(val value: String) {
        OBSERVERS("watchers"),
        OBSERVABLES("watch")
    }

    suspend fun getUser(id: String) = tryGet(
        "http://$HOST$PREFIX_URL/users/$id"
    ).let {
        coroutinesState({ it }) {
            it.wrapped<Profile>().map()
        }
    }

    suspend fun getUserMeets(
        page: Int? = null,
        perPage: Int? = null,
        type: MeetingsType,
    ) = tryGet("http://$HOST$PREFIX_URL/profile/meetings") {
        url {
            page?.let { query("page" to "$it") }
            query("is_completed" to type.ordinal.toString())
            perPage?.let { query("per_page" to "$it") }
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<Meeting>>()
                .let { it.first to it.second.currentPage }
        }
    }

    suspend fun getUserCategories() = tryGet(
        "http://$HOST$PREFIX_URL/profile/categories"
    ).let {
        coroutinesState({ it }) {
            it.wrapped<List<Category>>()
        }
    }

    suspend fun subscribeToUser(memberId: String) =
        tryPost("http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}")
        { url { query("user_id" to memberId) } }
            .let { coroutinesState({ it }) {} }

    suspend fun unsubscribeFromUser(memberId: String) =
        tryDelete("http://$HOST$PREFIX_URL/profile/${OBSERVABLES.value}") {
            url { query("user_id" to memberId) }
        }.let { coroutinesState({ it }) {} }

    suspend fun deleteObserver(member: UserModel) =
        tryDelete("http://$HOST$PREFIX_URL/profile/${OBSERVERS.value}") {
            url { query("user_id" to member.id.toString()) }
        }.let { coroutinesState({ it }) {} }

    suspend fun getObservers(
        query: String,
        type: ObserversType,
        @Suppress("unused_parameter")
        page: Int = 0,
        @Suppress("unused_parameter")
        perPage: Int = 15,
    ) = tryGet(
        "http://$HOST$PREFIX_URL/profile/${type.value}${
            if (query.isNotBlank()) "?query=$query" else ""
        }"
    ) {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<User>>().first
        }
    }

    suspend fun deleteHidden(albumId: String, imageId: String) =
        tryDelete(
            "http://$HOST$PREFIX_URL/albums/$albumId/files/$imageId"
        ).let { coroutinesState({ it }) {} }

    suspend fun changeAlbumPosition(albumId: String, imageId: String, position: Int) =
        tryPost(
            "http://$HOST$PREFIX_URL/albums/$albumId/position"
        ) {
            setBody(ChangeAlbumPositionRequest(imageId, position))
        }.let { coroutinesState({ it }) {} }

    data class ChangeAlbumPositionRequest(
        val file_id: String,
        val position: Int,
    )

    suspend fun getFiles(
        albumId: String,
    ) = tryGet("http://$HOST$PREFIX_URL/albums/$albumId/files")
        .let {
            coroutinesState({ it }) {
                it.paginateWrapped<List<Avatar>>()
                    .let { p -> p.first to p.second.total }
            }
        }

    suspend fun addHidden(albumId: String, files: List<File>) =
        tryPostFormData(
            url = "http://$HOST$PREFIX_URL/albums/$albumId/upload",
            formData = formData {
                append(
                    key = "type",
                    value = "PHOTO"
                )
                files.forEach {
                    append(
                        key = "photos[]",
                        value = it.readBytes(),
                        headers = Headers.build {
                            append(
                                name = ContentType,
                                value = "image/jpg"
                            )
                            append(
                                name = ContentDisposition,
                                value = "filename=\"${it.name}\""
                            )
                        }
                    )
                }
            }
        ).let {
            coroutinesState({ it }) {
                it.wrapped<List<Avatar>>()
            }
        }

    suspend fun setUserAvatar(avatar: File, list: List<Float>) =
        tryPostFormData(
            url = "http://$HOST$PREFIX_URL/profile/avatar",
            formData = formData {
                append(
                    key = "photo",
                    value = avatar.readBytes(),
                    headers = Headers.build {
                        append(
                            name = ContentType,
                            value = "image/jpg"
                        )
                        append(
                            name = ContentDisposition,
                            value = "filename=\"photo.jpg\""
                        )
                    }
                )
                append(
                    key = "crop_x",
                    value = list[0]
                )
                append(
                    key = "crop_y",
                    value = list[1]
                )
                append(
                    key = "crop_width",
                    value = list[2]
                )
                append(
                    key = "crop_height",
                    value = list[3]
                )
            }
        ).let { coroutinesState({ it }) {} }

    suspend fun checkUserName(username: String) =
        tryGet("http://$HOST$PREFIX_URL/profile/checkname") {
            url { query("username" to username) }
        }.let { coroutinesState({ it }) { false } }

    suspend fun setUserData(
        username: String?,
        aboutMe: String?,
        age: Int?,
        gender: GenderType?,
        orientationId: String?,
    ) = tryPatch("http://$HOST$PREFIX_URL/profile") {
        setBody(
            ProfileRequest(
                username, gender?.name,
                age, orientationId, aboutMe
            )
        )
    }.let { coroutinesState({ it }) {} }

    suspend fun getUserData() = tryGet(
        "http://$HOST$PREFIX_URL/profile"
    ).let {
        coroutinesState({ it }) {
            it.wrapped<Profile>()
        }
    }

    suspend fun deleteAccount() =
        tryDelete("http://$HOST$PREFIX_URL/profile/account")
            .let { coroutinesState({ it }) {} }

    suspend fun getResponds(
        type: RespondType, page: Int?, perPage: Int?,
    ) = tryGet("http://$HOST$PREFIX_URL/responds") {
        url {
            query("type" to type.name)
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<MeetWithRespondsResponse>>().first
        }
    }

    suspend fun getFilesPaging(
        albumId: String, page: Int, perPage: Int,
    ) = tryGet("http://$HOST$PREFIX_URL/albums/$albumId/files") {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }.let {

        coroutinesState({ it }) {
            it.paginateWrapped<List<Avatar>>()
                .let { p -> p.first to p.second.total }
        }
        /*coroutinesState({ it }) {
            it.paginateWrapped<List<Avatar>>().first
        }*/
    }

    suspend fun getMeetResponds(
        meetId: String, page: Int?, perPage: Int?,
    ) = tryGet("http://$HOST$PREFIX_URL/meetings/$meetId/responds") {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<RespondResponse>>().first
        }
    }

    suspend fun deleteRespond(respondId: String) =
        tryDelete("http://$HOST$PREFIX_URL/responds/$respondId")
            .let { coroutinesState({ it }) {} }

    suspend fun getNotificationResponds(): LastRespond {
        val request = getResponds(
            RECEIVED, null, null
        ).on(
            success = { it },
            loading = { emptyList() },
            error = { emptyList() }
        ).firstOrNull()

        return request.let {
            LastRespond(
                (it?.responds?.firstOrNull()
                    ?.author?.avatar?.thumbnail?.url ?: ""),
                it?.responds?.firstOrNull()?.author?.isOnline ?: false,
                it?.responds?.firstOrNull()?.author?.group?.map() ?: UserGroupTypeModel.DEFAULT,
                (it?.responds_count ?: 0)
            )


        }
    }

    suspend fun acceptRespond(respondId: String) =
        tryPost("http://$HOST$PREFIX_URL/responds/$respondId/accept")
            .let { coroutinesState({ it }) {} }

    suspend fun cancelRespond(respondId: String) =
        tryPatch("http://$HOST$PREFIX_URL/responds/$respondId/cancel")
            .let { coroutinesState({ it }) {} }
}
