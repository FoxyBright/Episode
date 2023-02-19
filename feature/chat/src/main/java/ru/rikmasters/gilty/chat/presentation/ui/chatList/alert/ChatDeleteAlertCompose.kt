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
        ChatDeleteAlert((true), LIST, (1))
    }
}

@Preview
@Composable
private fun AlertConfirmPreview() {
    GiltyTheme {
        ChatDeleteAlert((true), CONFIRM)
    }
}

@Composable
fun ChatDeleteAlert(
    active: Boolean,
    state: AlertState,
    select: Int? = null,
    listItemSelect: ((Int) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onSuccess: (() -> Unit)? = null,
) {
    val items: List<String>?
    val selected: Int?
    val title: String
    val label: String?
    val button: String
    if(state == LIST) {
        items = listOf(
            stringResource(delete_my_chat_button),
            stringResource(delete_my_and_other_chat_button)
        )
        selected = select
        title = stringResource(chats_delete)
        label = null
        button = stringResource(confirm_button)
    } else {
        items = null
        selected = null
        title = stringResource(chats_delete_label)
        label = stringResource(
            chats_delete_answer
        ); button = stringResource(meeting_filter_delete_tag_label)
    }
    GAlert(
        active, Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        Pair(button, onSuccess!!),
        label, title, onDismiss!!,
        Pair(stringResource(notification_respond_cancel_button), onDismiss),
        items, selected, listItemSelect
    )
}