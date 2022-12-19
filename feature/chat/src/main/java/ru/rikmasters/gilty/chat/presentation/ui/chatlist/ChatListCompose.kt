package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.shared.NavBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatListPreview() {
    GiltyTheme {
        ChatListContent(
            ChatListState(
                listOf(
                    NavIconState.INACTIVE,
                    NavIconState.NEW,
                    NavIconState.INACTIVE,
                    NavIconState.INACTIVE,
                    NavIconState.ACTIVE
                ),
                listOf(
                    Pair(
                        DemoFullMeetingModel,
                        listOf(DemoMessageModel)
                    )
                )
            )
        )
    }
}

data class ChatListState(
    val stateList: List<NavIconState>,
    val chats: List<Pair<FullMeetingModel, List<MessageModel>>>
)

interface ChatListCallback {
    fun onNavBarSelect(point: Int) {}
    fun onChatClick(chat: Int) {}
}

@Composable
fun ChatListContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Text(
            stringResource(R.string.chats_label),
            Modifier
                .padding(top = 80.dp, bottom = 10.dp)
                .padding(horizontal = 16.dp),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleLarge
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            state.chats.forEachIndexed { index, it ->
                item {
                    ChatRowContent(
                        Modifier, it.first,
                        it.second.last(),
                        LazyItemsShapes(
                            index,
                            state.chats.size
                        )
                    ) { callback?.onChatClick(index) }
                }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        NavBar(
            state.stateList, Modifier.align(Alignment.BottomCenter)
        ) { callback?.onNavBarSelect(it) }
    }
}
