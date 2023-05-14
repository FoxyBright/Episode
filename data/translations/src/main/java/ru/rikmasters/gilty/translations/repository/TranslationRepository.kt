package ru.rikmasters.gilty.translations.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSocket
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource

class TranslationRepository(
    private val remoteSource: TranslationWebSource,
    private val webSocket: TranslationWebSocket,
) : CoroutineController() {

    val webSocketFlow = webSocket.answer
    suspend fun connectToTranslation(translationId: String) {
        withContext(Dispatchers.IO) {
            webSocket.connectToTranslation(
                id = translationId
            )
        }
    }

    suspend fun disconnectFromTranslation() {
        withContext(Dispatchers.IO) {
            webSocket.disconnectFromTranslation()
        }
    }

    suspend fun connectToTranslationChat(translationId: String) {
        withContext(Dispatchers.IO) {
            webSocket.connectToTranslationChat(
                id = translationId
            )
        }
    }

    suspend fun disconnectFromTranslationChat() {
        withContext(Dispatchers.IO) {
            webSocket.disconnectFromTranslationChat()
        }
    }

    suspend fun getTranslationInfo(translationId: String): DataStateTest<TranslationInfoModel> =
        coroutinesState {
            remoteSource.getTranslationInfo(
                translationId = translationId,
            ).map()
        }

    suspend fun endTranslation(translationId: String): DataStateTest<Unit> =
        coroutinesState {
            remoteSource.endTranslation(
                translationId = translationId,
            )
        }

    suspend fun sendSignal(
        translationId: String,
        signalType: TranslationSignalTypeModel,
        value: Boolean,
    ): DataStateTest<Unit> =
        coroutinesState {
            remoteSource.sendSignal(
                translationId = translationId,
                signalType = TranslationSignalTypeDTO.map(signalType),
                value = value,
            )
        }

    suspend fun sendMessage(
        translationId: String,
        text: String,
    ): DataStateTest<TranslationMessageModel> =
        coroutinesState {
            remoteSource.sendMessage(
                translationId = translationId,
                text = text,
            ).map()
        }

    fun getMessages(translationId: String): Flow<PagingData<TranslationMessageModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                TranslationMessagesPagingSource(
                    webSource = remoteSource,
                    translationId = translationId,
                )
            },
        ).flow

    fun getConnectedUsers(
        translationId: String,
        query: String?,
    ): Flow<PagingData<FullUserModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                TranslationConnectedUsersPagingSource(
                    webSource = remoteSource,
                    translationId = translationId,
                    query = query,
                )
            },
        ).flow

    suspend fun extendTranslation(
        translationId: String,
        duration: Long,
    ): DataStateTest<TranslationInfoModel> =
        coroutinesState {
            remoteSource.extendTranslation(
                translationId = translationId,
                duration = duration,
            ).map()
        }

    suspend fun kickUser(
        translationId: String,
        userId: String,
    ): DataStateTest<Unit> =
        coroutinesState {
            remoteSource.kickUser(
                translationId = translationId,
                userId = userId,
            )
        }

    suspend fun ping(
        translationId: String,
    ): DataStateTest<Unit> =
        coroutinesState {
            remoteSource.ping(
                translationId = translationId,
            )
        }
}
