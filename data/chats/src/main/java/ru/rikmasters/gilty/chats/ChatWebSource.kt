package ru.rikmasters.gilty.chats

import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ru.rikmasters.gilty.chats.websocket.model.ChatStatus
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.image.AlbumModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Album
import ru.rikmasters.gilty.shared.models.Hidden
import ru.rikmasters.gilty.shared.models.chats.Chat
import ru.rikmasters.gilty.shared.models.chats.Message
import ru.rikmasters.gilty.shared.wrapper.wrapped
import java.io.File

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs(
        page: Int?, perPage: Int?,
    ): List<ChatModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/chats"
        ) {
            url {
                page?.let { query("page" to "$it") }
                perPage?.let { query("per_page" to "$it") }
            }
        }.wrapped<List<Chat>>().map { it.map() }
    }
    
    suspend fun markAsReadMessage(chatId: String) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/chats/$chatId/markAsRead"
        )
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Int,
    ) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/chats/$chatId/messages"
        ) {
            url {
                messageIds.forEach { query("ids[]" to it) }
                query("all_members" to "$allMembers")
            }
        }
    }
    
    suspend fun getChatAlbum(chatId: String): AlbumModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/chats/$chatId/album"
        ).wrapped<Album>().map()
    }
    
    suspend fun unmuteChatNotifications(chatId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/chats/$chatId/unmute"
        )
    }
    
    suspend fun getChatsStatus(): Int {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/chats/status"
        ).wrapped<ChatStatus>().unreadCount
    }
    
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        updateClientToken()
        client.get(
            "http://$HOST$PREFIX_URL/chats/$chatId/mute"
        ) { url { query("unmute_at" to unmuteAt) } }
    }
    
    suspend fun sendMessage(
        chatId: String,
        replyId: String? = null,
        text: String? = null,
        photos: List<File>? = null,
        attachments: List<AvatarModel>? = null,
        videos: List<File>? = null,
    ) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/chats/$chatId/messages"
        ) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        
                        // id of the parent message if message is reply
                        replyId?.let { append("reply_id", it) }
                        
                        // text from message
                        text?.let { append("text", it) }
                        
                        // attached hidden photos
                        if(!attachments.isNullOrEmpty()) {
                            attachments.forEach {
                                append(
                                    "attachments[]",
                                    Hidden(
                                        it.type.name,
                                        it.albumId,
                                        it.id,
                                    ).toString()
                                )
                            }
                        }
                        
                        // attached photos from camera or gallery
                        if(!photos.isNullOrEmpty()) {
                            photos.forEach { photo ->
                                append("upload[type]", "PHOTO")
                                append("upload[photos][]", photo.readBytes(),
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/jpg")
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=\"${photo.name}\""
                                        )
                                    })
                            }
                        }
                        
                        // attached videos from camera or gallery
                        if(!videos.isNullOrEmpty()) {
                            videos.forEach { video ->
                                append("upload[type]", "VIDEO")
                                append("upload[videos][]", video.readBytes(),
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "video/mp4")
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=\"${video.name}\""
                                        )
                                    })
                            }
                        }
                        
                    }, "WebAppBoundary"
                )
            )
        }
    }
    
    suspend fun getMessages(
        chatId: String,
        page: Int?, perPage: Int?,
    ): List<MessageModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/chats/$chatId/messages"
        ) {
            url {
                page?.let { query("page" to "$it") }
                perPage?.let { query("per_page" to "$it") }
            }
        }.wrapped<List<Message>>().map { it.map() }
    }
    
    suspend fun getChat(chatId: String): ChatModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/chats/$chatId"
        ).wrapped<Chat>().map()
    }
    
    suspend fun completeChat(chatId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/chats/$chatId/complete"
        )
    }
    
    suspend fun isTyping(chatId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/chats/$chatId/typing"
        )
    }
    
    suspend fun madeScreenshot(chatId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/chats/$chatId/screenshot"
        )
    }
    
    suspend fun deleteDialog(
        chatId: String,
        forAll: Boolean,
    ) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/chats/$chatId${
                if(forAll) "/all" else ""
            }"
        )
    }
}