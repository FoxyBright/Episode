@file:Suppress("unused")

package ru.rikmasters.gilty.chats.source.web

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode.Companion.OK
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.chat.SortType
import ru.rikmasters.gilty.chats.models.message.MarkAsReadRequest
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.ChatStatus
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.image.AlbumModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Album
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.util.UUID

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs(
        page: Int? = null,
        perPage: Int? = null,
        sortType: SortType = SortType.MEETING_DATE,
        isArchiveOn: Boolean = false,
    ) = get("http://$HOST$PREFIX_URL/chats") {
        url {
            page?.let { query("page" to "$page") }
            perPage?.let { query("per_page" to "$perPage") }
            query("sort_type" to sortType.stringName)
            query("archive" to if(isArchiveOn) "1" else "0")
        }
    }?.let {
        if(it.status == OK) it.wrapped<List<Chat>>() else null
    } ?: emptyList()
    
    
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String>,
        all: Boolean,
    ) {
        patch("http://$HOST$PREFIX_URL/chats/$chatId/markAsRead") {
            setBody(MarkAsReadRequest(messageIds, all))
        }
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Int,
    ) {
        delete("http://$HOST$PREFIX_URL/chats/$chatId/messages") {
            url {
                messageIds.forEach { query("ids[]" to it) }
                query("all_members" to "$allMembers")
            }
        }
    }
    
    suspend fun getChatAlbum(
        chatId: String,
    ) = get(
        "http://$HOST$PREFIX_URL/chats/$chatId/album"
    )?.let {
        if(it.status == OK)
            it.wrapped<Album>().map()
        else null
    } ?: AlbumModel()
    
    suspend fun unmuteChatNotifications(chatId: String) {
        post("http://$HOST$PREFIX_URL/chats/$chatId/unmute")
    }
    
    suspend fun getChatsStatus() =
        get("http://$HOST$PREFIX_URL/chats/status")
            ?.let {
                if(it.status == OK)
                    it.wrapped<ChatStatus>()
                        .unreadCount
                else null
            } ?: 0
    
    suspend fun muteChatNotifications(
        chatId: String,
        unmuteAt: String,
    ) {
        get("http://$HOST$PREFIX_URL/chats/$chatId/mute") {
            url { query("unmute_at" to unmuteAt) }
        }
    }
    
    suspend fun sendMessage(
        chatId: String,
        replyId: String? = null,
        text: String? = null,
        photos: List<ByteArray>? = null,
        attachments: List<AvatarModel>? = null,
        videos: List<ByteArray>? = null,
    ) {
        postFormData(
            "http://$HOST$PREFIX_URL/chats/$chatId/messages",
            formData {
                // id of the parent message if message is reply
                replyId?.let { append("reply_id", it) }
                
                // text from message
                text?.let { append("text", it) }
                
                // attached hidden photos
                if(!attachments.isNullOrEmpty()) {
                    attachments.forEachIndexed { i, it ->
                        append(
                            ("attachments[$i][type]"),
                            ("PRIVATE_PHOTO")
                        )
                        append(("attachments[$i][album_id]"), it.albumId)
                        append(("attachments[$i][file_id]"), it.id)
                    }
                }
                
                if(!photos.isNullOrEmpty()) {
                    photos.forEachIndexed { i, it ->
                        log.d("photo -> $it")
                        append(("upload[type]"), ("PHOTO"))
                        append(
                            ("upload[photos][$i]"),
                            it,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "image/jpg"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"$it.jpg\""
                                )
                            }
                        )
                    }
                }
                
                // attached videos from camera or gallery
                if(!videos.isNullOrEmpty()) {
                    videos.forEach { video ->
                        append("upload[type]", "VIDEO")
                        append(
                            "upload[videos][]",
                            video,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "video/mp4"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${UUID.randomUUID()}.mp4\""
                                )
                            }
                        )
                    }
                }
            }
        )
    }
    
    suspend fun getMessages(
        chatId: String, page: Int?, perPage: Int?,
    ) = get(
        "http://$HOST$PREFIX_URL/chats/$chatId/messages"
    ) {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }?.let { if(it.status == OK) it.paginateWrapped() else null }
        ?: (emptyList<Message>() to Paginator())
    
    suspend fun getChat(chatId: String) = get(
        "http://$HOST$PREFIX_URL/chats/$chatId"
    )?.let {
        if(it.status == OK)
            it.wrapped<Chat>().map()
        else null
    } ?: ChatModel()
    
    suspend fun getTranslationViewers(
        chatId: String, query: String?,
        page: Int?, perPage: Int?,
    ) = get("http://$HOST$PREFIX_URL/translations/$chatId/connected") {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
            query?.let { query("query" to it) }
        }
    }?.let { res ->
        if(res.status == OK)
            res.paginateWrapped<List<User>>().let { (list, pag) ->
                list.map { it.map() } to pag
            } else null
    } ?: (emptyList<UserModel>() to Paginator())
    
    suspend fun completeChat(chatId: String) {
        post("http://$HOST$PREFIX_URL/chats/$chatId/complete")
    }
    
    suspend fun isTyping(chatId: String) {
        post("http://$HOST$PREFIX_URL/chats/$chatId/typing")
    }
    
    suspend fun madeScreenshot(chatId: String) {
        post("http://$HOST$PREFIX_URL/chats/$chatId/screenshot")
    }
    
    suspend fun deleteChat(
        chatId: String,
        forAll: Boolean,
    ) {
        delete(
            "http://$HOST$PREFIX_URL/chats/$chatId${
                if(forAll) "/all" else ""
            }"
        )
    }
}