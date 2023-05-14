package ru.rikmasters.gilty.translations.datasource.remote

class TranslationWebSocket : WebSocketManager() {

    override suspend fun handleResponse(response: SocketResponse) {
        val event = TranslationsSocketEvents from response.event
        logV(event?.name.toString())
        when (event) {
            TranslationsSocketEvents.PONG -> inPing = false
            TranslationsSocketEvents.PING -> {}
            TranslationsSocketEvents.CONNECTION_ESTABLISHED -> {
                socketId.emit(mapper.readValue<SocketData>(response.data).socket_id)
                subscribe("private-translation.${translationId.value}")
            }

            TranslationsSocketEvents.SUBSCRIPTION_SUCCEEDED -> {
                logV("$response")
            }

            TranslationsSocketEvents.SUBSCRIBE -> {
            }

            TranslationsSocketEvents.UNSUBSCRIBE -> {}
            TranslationsSocketEvents.TRANSLATION_STARTED -> {
                _answer.send(TranslationCallbackEvents.TranslationStarted)
            }

            TranslationsSocketEvents.TRANSLATION_COMPLETED -> {
                _answer.send(TranslationCallbackEvents.TranslationCompleted)
            }

            TranslationsSocketEvents.SIGNAL -> {
                val signal = mapper.readValue<TranslationSignalModel>(response.data)
                _answer.send(
                    TranslationCallbackEvents.SignalReceived(
                        signal = signal,
                    ),
                )
            }

            TranslationsSocketEvents.APPEND_TIME -> {
                data class AppendInfo(val completed_at: String, val duration: String)

                val info = mapper.readValue<AppendInfo>(response.data)
                _answer.send(
                    TranslationCallbackEvents.TranslationExtended(
                        completedAt = LocalDateTime.of(info.completed_at),
                        duration = info.duration.toInt(),
                    ),
                )
            }

            TranslationsSocketEvents.EXPIRED -> {
                _answer.send(TranslationCallbackEvents.TranslationExpired)
            }

            TranslationsSocketEvents.USER_CONNECTED -> {
                data class User(val user: String, val count: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserConnected(
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }

            TranslationsSocketEvents.USER_DISCONNECTED -> {
                data class User(val user: String, val count: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserDisconnected(
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }
            TranslationsSocketEvents.USER_KICKED -> {
                data class User(val user: String, val count: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserKicked(
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }
            TranslationsSocketEvents.MESSAGE_SENT -> {
                Log.d("TESTSOCKETS", response.data)
            }
            else -> {}
        }
    }

    private val translationId = MutableStateFlow<String?>(null)

    private val _answer = Channel<TranslationCallbackEvents>()
    val answer = _answer.receiveAsFlow()

    suspend fun connectToTranslation(id: String) {
        disconnectFromTranslation()
        subscribe("private-translation.$id.$_userId") {
            if (it) translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslation() {
        translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.$it.$_userId"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
    }

    suspend fun connectToTranslationChat(id: String) {
        disconnectFromTranslationChat()
        subscribe("private-translation.${id}_chat") {
            if (it) translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslationChat() {
        translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.${it}_chat"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
    }
}