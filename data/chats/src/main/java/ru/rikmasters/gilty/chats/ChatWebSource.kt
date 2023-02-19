package ru.rikmasters.gilty.chats

import io.ktor.client.request.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.image.AlbumModel
import ru.rikmasters.gilty.shared.models.Album
import ru.rikmasters.gilty.shared.models.chats.Chat
import ru.rikmasters.gilty.shared.models.chats.Message
import ru.rikmasters.gilty.shared.wrapper.wrapped

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
            "http://$HOST$PREFIX_URL/$chatId/markAsRead"
        ) { url { query("id" to chatId) } }
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Int,
    ) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/$chatId/messages"
        ) {
            url {
                query("id" to chatId)
                messageIds.forEach { query("ids[]" to it) }
                query("all_members" to "$allMembers")
            }
        }
    }
    
    suspend fun getChatAlbum(chatId: String): AlbumModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/$chatId/album"
        ) {
            url { query("id" to chatId) }
        }.wrapped<Album>().map()
    }
    
    suspend fun unmuteChatNotifications(chatId: String) {
        updateClientToken()
        client.post(
            ("http://$HOST$PREFIX_URL/$chatId/unmute")
        )
    }
    
    suspend fun getChatsStatus(): Int {
        updateClientToken()
        data class ChatStatus(val unreadCount: Int)
        return client.get(
            "http://$HOST$PREFIX_URL/chats/status"
        ) {}.wrapped<ChatStatus>().unreadCount
    }
    
    
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        updateClientToken()
        client.get(
            "http://$HOST$PREFIX_URL/$chatId/mute"
        ) { url { query("unmute_at" to unmuteAt) } }
    }
    
    suspend fun sendMessage(chatId: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/$chatId/messages"
        ) {
            //TODO тут multipart
        }
    }
    
    suspend fun getMessages(
        chatId: String,
        page: Int?, perPage: Int?,
    ): List<MessageModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/$chatId/messages"
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
            "http://$HOST$PREFIX_URL/chats"
        ) {
            url { query("id" to chatId) }
        }.wrapped<Chat>().map()
    }
    
    suspend fun completeChat(chatId: String) {
        updateClientToken()
        client.post(
            ("http://$HOST$PREFIX_URL/chats/$chatId/complete")
        )
    }
    
    suspend fun isTyping(chatId: String) {
        updateClientToken()
        client.post(
            ("http://$HOST$PREFIX_URL/chats/$chatId/typing")
        )
    }
    
    suspend fun madeScreenshot(chatId: String) {
        updateClientToken()
        client.post(
            ("http://$HOST$PREFIX_URL/chats/$chatId/screenshot")
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


//TODO web-sockets

//val parser: Parser = Parser()
//val stringBuilder: StringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23}")
//val json: JsonObject = parser.parse(stringBuilder) as JsonObject
//println("Name : ${json.string("name")}, Age : ${json.int("age")}")
//    data class SocketData(
//        val socket_id: String,
//        val activity_timeout: Int,
//    )
//
//    data class SocketResponse(
//        val event: String,
//        val data: String,
//    )
//
//    suspend fun getDialogs() {
//        runBlocking {
//            val a = client.webSocket(
//                host = "$HOST:6001/app/local?protocol=7&client=js&version=7.2.0&flash=false",
//            ) {
//                while(true) {
//                    val response = receiveDeserialized<SocketResponse>()
//                    logD(response.toString())
//                    logD(response.data)
//                }
//            }
//            client.close()
//        }
//    }
//
//    suspend fun DefaultClientWebSocketSession.outputMessages() {
//        try {
//            for(message in incoming) {
//                message as? Frame.Text ?: continue
//                println(message.readText())
//            }
//        } catch(e: Exception) {
//            println("Error while receiving: " + e.localizedMessage)
//        }
//    }
//
//    suspend fun DefaultClientWebSocketSession.inputMessages() {
//        while(true) {
//            val message = readLine() ?: ""
//            if(message.equals("exit", true)) return
//            try {
//                send(message)
//            } catch(e: Exception) {
//                println("Error while sending: " + e.localizedMessage)
//                return
//            }
//        }
//    }
//
//    suspend fun broadcastingAuth() {
//        updateClientToken()
//        client.post(
//            "http://$HOST/broadcasting/auth"
//        ) {
//
//        }
//    }