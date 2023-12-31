@file:Suppress("unused")

package ru.rikmasters.gilty.chats.source.web

import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.Headers.Companion.build
import io.ktor.http.HttpHeaders.ContentDisposition
import io.ktor.http.HttpHeaders.ContentType
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.chat.SortType
import ru.rikmasters.gilty.chats.models.message.MarkAsReadRequest
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.ChatStatus
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
import java.io.File

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs(
        page: Int? = null,
        perPage: Int? = null,
        sortType: SortType = SortType.MEETING_DATE,
        isArchiveOn: Boolean = false,
    ) = tryGet("http://$HOST$PREFIX_URL/chats") {
        url {
            page?.let { query("page" to "$page") }
            perPage?.let { query("per_page" to "$perPage") }
            query("sort_type" to sortType.stringName)
            query("archive" to if(isArchiveOn) "1" else "0")
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
    
    suspend fun getChatsStatus() = tryGet(
        "http://$HOST$PREFIX_URL/chats/status"
    ).let { coroutinesState({ it }) { it.wrapped<ChatStatus>() } }
    
    suspend fun muteChatNotifications(chatId: String, unmuteAt: String) =
        tryPost("http://$HOST$PREFIX_URL/chats/$chatId/mute")
        { url { query("unmute_at" to unmuteAt) } }
            .let { coroutinesState({ it }) {} }
    
    suspend fun sendMessage(
        chatId: String,
        replyId: String? = null,
        text: String? = null,
        photos: List<File>? = null,
        attachments: List<AvatarModel>? = null,
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
                append(
                    key = "upload[type]",
                    value = "PHOTO"
                )
                photos.forEach {
                    append(
                        key = "upload[photos][]",
                        value = it.readBytes(),
                        headers = build {
                            append(
                                name = ContentType,
                                value = "image/jpg"
                            )
                            append(
                                name = ContentDisposition,
                                value = "filename=\"${it.name}.jpg\""
                            )
                        }
                    )
                }
            }
        }
    ).let {
        coroutinesState(
            request = { it },
            expectCode = 201,
            response = {}
        )
    }
    
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