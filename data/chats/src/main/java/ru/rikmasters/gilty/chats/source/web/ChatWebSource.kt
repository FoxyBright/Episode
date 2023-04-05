package ru.rikmasters.gilty.chats.source.web

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ru.rikmasters.gilty.chats.models.chat.Chat
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
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Album
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.util.UUID

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs(
        page: Int? = null,
        perPage: Int? = null,
        unread: Int = 0,
    ): Pair<List<Chat>, Paginator> {
        return get("http://$HOST$PREFIX_URL/chats") {
            url {
                page?.let { query("page" to "$page") }
                perPage?.let { query("per_page" to "$perPage") }
                query("unread" to "$unread")
            }
        }!!.paginateWrapped()
    }
    
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
    
    suspend fun getChatAlbum(chatId: String): AlbumModel {
        return get("http://$HOST$PREFIX_URL/chats/$chatId/album")
            ?.wrapped<Album>()
            ?.map()
            ?: AlbumModel()
    }
    
    suspend fun unmuteChatNotifications(chatId: String) {
        post("http://$HOST$PREFIX_URL/chats/$chatId/unmute")
    }
    
    suspend fun getChatsStatus(): Int {
        return get("http://$HOST$PREFIX_URL/chats/status")
            ?.wrapped<ChatStatus>()
            ?.unreadCount
            ?: 0
    }
    
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
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
                if(!attachments.isNullOrEmpty())
                    attachments.forEachIndexed { i, it ->
                        append(("attachments[$i][type]"), ("PRIVATE_PHOTO"))
                        append(("attachments[$i][album_id]"), it.albumId)
                        append(("attachments[$i][file_id]"), it.id)
                    }
                
                if(!photos.isNullOrEmpty())
                    photos.forEachIndexed { i, it ->
                        log.d("photo -> $it")
                        append(("upload[type]"), ("PHOTO"))
                        append(("upload[photos][$i]"), it, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpg")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${it}.jpg\""
                            )
                        })
                    }
                
                // attached videos from camera or gallery
                if(!videos.isNullOrEmpty()) {
                    videos.forEach { video ->
                        append("upload[type]", "VIDEO")
                        append("upload[videos][]", video,
                            Headers.build {
                                append(HttpHeaders.ContentType, "video/mp4")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${UUID.randomUUID()}.mp4\""
                                )
                            }
                        )
                    }
                }
                
            },
        )
    }
    
    suspend fun getMessages(
        chatId: String,
        page: Int?, perPage: Int?,
    ): Pair<List<Message>, Paginator> {
        return get(
            "http://$HOST$PREFIX_URL/chats/$chatId/messages"
        ) {
            url {
                page?.let { query("page" to "$it") }
                perPage?.let { query("per_page" to "$it") }
            }
        }!!.paginateWrapped()
    }
    
    suspend fun getChat(chatId: String): ChatModel {
        return get(
            "http://$HOST$PREFIX_URL/chats/$chatId"
        )?.wrapped<Chat>()?.map() ?: ChatModel()
    }
    
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