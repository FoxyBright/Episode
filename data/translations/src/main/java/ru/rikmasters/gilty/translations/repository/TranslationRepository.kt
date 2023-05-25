package ru.rikmasters.gilty.translations.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.models.enumeration.TranslationSignalTypeDTO
import ru.rikmasters.gilty.shared.models.translations.TranslationInfoDTO
import ru.rikmasters.gilty.shared.models.translations.TranslationMessageDTO
import ru.rikmasters.gilty.shared.wrapper.coroutinesState
import ru.rikmasters.gilty.shared.wrapper.wrapped
import ru.rikmasters.gilty.shared.wrapper.wrappedTest
import ru.rikmasters.gilty.translations.datasource.paging.TranslationConnectedUsersPagingSource
import ru.rikmasters.gilty.translations.datasource.paging.TranslationMessagesPagingSource
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSocket
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource

class TranslationRepository(
    private val remoteSource: TranslationWebSource,
    private val webSocket: TranslationWebSocket,
): CoroutineController() {
    
    val webSocketFlow = webSocket.answer
    
    @Suppress("unused")
    fun disconnectWebSocket() {
        webSocket.disconnect()
    }
    
    suspend fun connectWebSocket(userId: String, translationId: String) {
        webSocket.updateTranslationId(translationId)
        webSocket.connect(userId)
    }
    
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
    
    suspend fun getTranslationInfo(
        translationId: String,
    ): DataStateTest<TranslationInfoModel> =
        remoteSource.getTranslationInfo(translationId).let {
            coroutinesState({ it }) {
                it.wrappedTest<TranslationInfoDTO>().map()
            }
        }
    
    suspend fun endTranslation(translationId: String): DataStateTest<Unit> =
        remoteSource.endTranslation(
            translationId = translationId,
        ).let { coroutinesState({ it }) {} }
    
    suspend fun sendSignal(
        translationId: String,
        signalType: TranslationSignalTypeModel,
        value: Boolean,
    ): DataStateTest<Unit> = remoteSource.sendSignal(
        translationId = translationId,
        signalType = TranslationSignalTypeDTO.map(signalType),
        value = value,
    ).let { coroutinesState({ it }) {} }
    
    suspend fun sendMessage(
        translationId: String,
        text: String,
    ): DataStateTest<TranslationMessageModel> =
        remoteSource.sendMessage(
            translationId = translationId,
            text = text,
        ).let {
            coroutinesState({ it }) {
                it.wrapped<TranslationMessageDTO>().map()
            }
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
        remoteSource.extendTranslation(
            translationId = translationId,
            duration = duration,
        ).let {
            coroutinesState({ it }) {
                it.wrapped<TranslationInfoDTO>().map()
            }
        }
    
    suspend fun kickUser(
        translationId: String,
        userId: String,
    ): DataStateTest<Unit> =
        remoteSource.kickUser(
            translationId = translationId,
            userId = userId,
        ).let { coroutinesState({ it }) {} }
    
    suspend fun ping(
        translationId: String,
    ): DataStateTest<Unit> = remoteSource
        .ping(translationId)
        .let { coroutinesState({ it }) {} }
}

