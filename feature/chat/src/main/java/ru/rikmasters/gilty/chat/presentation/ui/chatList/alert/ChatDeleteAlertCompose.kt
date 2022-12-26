package ru.rikmasters.gilty.chat.presentation.ui.chatList.alert

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

enum class AlertState { LIST, CONFIRM }

@Preview
@Composable
private fun AlertListPreview() {
    GiltyTheme {
        ChatDeleteAlert(
            true, LIST, listOf(
                Pair(stringResource(delete_my_chat_button), true),
                Pair(stringResource(delete_my_and_other_chat_button), false)
            ), {}, {}, {})
    }
}

@Preview
@Composable
private fun AlertConfirmPreview() {
    GiltyTheme {
        ChatDeleteAlert(
            true, CONFIRM, listOf(
                Pair(stringResource(delete_my_chat_button), true),
                Pair(stringResource(delete_my_and_other_chat_button), false)
            ), {}, {}, {})
    }
}

@Composable
fun ChatDeleteAlert(
    active: Boolean,
    state: AlertState,
    list: List<Pair<String, Boolean>>,
    listItemSelect: ((Int) -> Unit),
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val items: List<Pair<String, Boolean>>?
    val title: String
    val label: String?
    val button: String
    if(state == LIST) {
        items = list
        title = stringResource(chats_delete)
        label = null
        button = stringResource(confirm_button)
    } else {
        items = null
        title = stringResource(chats_delete_label)
        label = stringResource(
            chats_delete_answer
        ); button = stringResource(meeting_filter_delete_tag_label)
    }
    GAlert(
        active, onDismiss, title, Modifier
            .wrapContentHeight()
            .fillMaxWidth(), label,
        Pair(button, onSuccess),
        Pair(stringResource(notification_respond_cancel_button), onDismiss),
        items, listItemSelect
    )
}