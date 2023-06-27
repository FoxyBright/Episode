package ru.rikmasters.gilty.chats.paging

import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import androidx.room.withTransaction
import ru.rikmasters.gilty.chats.models.message.*
import ru.rikmasters.gilty.chats.paging.messages.MessageDataBase
import ru.rikmasters.gilty.chats.paging.messages.entity.DBMessage
import ru.rikmasters.gilty.chats.paging.messages.entity.MessageRemoteKeys
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper

@OptIn(ExperimentalPagingApi::class)
class MessageMediator(
    private val chatId: String,
    private val web: ChatWebSource,
    private val store: MessageDataBase,
    private val perPage: Int,
): RemoteMediator<Int, DBMessage>() {
    
    private val messageDao = store.getMessagesDao()
    private val keysDao = store.getRemoteKeysDao()
    
    private suspend fun rCurrentKey(state: PagingState<Int, DBMessage>) =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id
                ?.let { keysDao.getRemoteKeyByMessageId(it) }
        }
    
    private suspend fun rPrevKey(state: PagingState<Int, DBMessage>) =
        state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { keysDao.getRemoteKeyByMessageId(it.id) }
    
    private suspend fun rNextKey(state: PagingState<Int, DBMessage>) =
        state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { keysDao.getRemoteKeyByMessageId(it.id) }
    
    private suspend fun List<Message>.saveToBase(
        current: Int, pagingEnd: Boolean,
    ) = forEach {
        messageDao.addMessage(it.mapToBase())
        keysDao.addKey(
            MessageRemoteKeys(
                messageId = it.id,
                prevKey = if(current == 1) null
                else current.minus(1),
                currentPage = current,
                nextKey = if(pagingEnd) null
                else current.plus(1)
            )
        )
    }
    
    private suspend fun LoadType.saveDataTransaction(
        response: Pair<List<Message>, ResponseWrapper.Paginator>,
        current: Int, pagingEnd: Boolean,
    ) {
        store.withTransaction {
            if(this == REFRESH) {
                messageDao.clearAllMessages()
                keysDao.clearRemoteKeys()
            }
            response.first.saveToBase(
                current = current,
                pagingEnd = pagingEnd
            )
        }
    }
    
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBMessage>,
    ) = try {
        val current = when(loadType) {
            REFRESH -> rCurrentKey(state).let {
                it?.nextKey?.minus(1) ?: 1
            }
            PREPEND -> rPrevKey(state).let {
                it?.prevKey ?: return Success((it != null))
            }
            APPEND -> rNextKey(state).let {
                it?.nextKey ?: return Success((it != null))
            }
        }
        
        web.getMessages(
            chatId = chatId,
            page = current,
            perPage = perPage
        ).on(
            error = {
                Error(Throwable(it.serverMessage))
            },
            loading = {
                Error(Throwable(it.name))
            },
            success = { response ->
                (response.second.last_page == current).let {
                    loadType.saveDataTransaction(
                        response = response,
                        current = current,
                        pagingEnd = it
                    )
                    Success(it)
                }
            }
        )
    } catch(e: Exception) {
        Error(e)
    }
}