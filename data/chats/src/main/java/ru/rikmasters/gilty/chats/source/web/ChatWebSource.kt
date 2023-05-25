@file:Suppress("unused")

package ru.rikmasters.gilty.chats.source.web

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Album
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.wrapper.coroutinesState
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.util.UUID

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs(
        page: Int? = null,
        perPage: Int? = null,
        sortType: SortType = SortType.MEETING_DATE,
    ) = tryGet("http://$HOST$PREFIX_URL/chats") {
        url {
            page?.let { query("page" to "$page") }
            perPage?.let { query("per_page" to "$perPage") }
            query("sort_type" to sortType.stringName)
        }
    }.let { coroutinesState({ it }) { it.wrapped<List<Chat>>() } }
    
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String>,
        all: Boolean,
    ) = tryPatch("http://$HOST$PREFIX_URL/chats/$chatId/markAsRead") {
        setBody(MarkAsReadRequest(messageIds, all))
    }.let { coroutinesState({ it }) {} }
    
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Int,
    ) = tryDelete("http://$HOST$PREFIX_URL/chats/$chatId/messages") {
        url {
            messageIds.forEach { query("ids[]" to it) }
            query("all_members" to "$allMembers")
        }
    }.let { coroutinesState({ it }) {} }
    
    suspend fun getChatAlbum(chatId: String) =
        tryGet("http://$HOST$PREFIX_URL/chats/$chatId/album")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<Album>().map()
                }
            }
    
    suspend fun unmuteChatNotifications(chatId: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/unmute")
            .let { coroutinesState({ it }) {} }
    
    suspend fun getChatsStatus() =
        tryGet("http://$HOST$PREFIX_URL/chats/status")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<ChatStatus>()
                        .unreadCount
                }
            }
    
    suspend fun muteChatNotifications(chatId: String, unmuteAt: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/mute")
        { url { query("unmute_at" to unmuteAt) } }
            .let { coroutinesState({ it }) {} }
    
    suspend fun sendMessage(
        chatId: String,
        replyId: String? = null,
        text: String? = null,
        photos: List<ByteArray>? = null,
        attachments: List<AvatarModel>? = null,
        videos: List<ByteArray>? = null,
    ) = tryPostFormData(
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
                    append(
                        ("attachments[$i][album_id]"),
                        it.albumId
                    )
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
    ).let { coroutinesState({ it }) {} }
    
    suspend fun getMessages(
        chatId: String, page: Int?, perPage: Int?,
    ) = tryGet(
        "http://$HOST$PREFIX_URL/chats/$chatId/messages"
    ) {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<Message>>()
                .let { it.first to it.second.currentPage }
        }
    }
    
    suspend fun getChat(chatId: String) = tryGet(
        "http://$HOST$PREFIX_URL/chats/$chatId"
    ).let {
        coroutinesState({ it }) {
            it.wrapped<Chat>().map()
        }
    }
    
    suspend fun getTranslationViewers(
        chatId: String, query: String?,
        page: Int?, perPage: Int?,
    ) = tryGet(
        "http://$HOST$PREFIX_URL/translations/$chatId/connected"
    ) {
        url {
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
            query?.let { query("query" to it) }
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<User>>()
                .let { (list, pag) ->
                    list.map { it.map() } to pag
                }
        }
    }
    
    suspend fun completeChat(chatId: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/complete")
            .let { coroutinesState({ it }) {} }
    
    suspend fun isTyping(chatId: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/typing")
            .let { coroutinesState({ it }) {} }
    
    suspend fun madeScreenshot(chatId: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/screenshot")
            .let { coroutinesState({ it }) {} }
    
    suspend fun deleteChat(
        chatId: String,
        forAll: Boolean,
    ) = tryDelete(
        "http://$HOST$PREFIX_URL/chats/$chatId${
            if(forAll) "/all" else ""
        }"
    ).let { coroutinesState({ it }) {} }
}