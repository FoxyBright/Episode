package ru.rikmasters.gilty.presentation.ui.presentation.responds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.RespondType
import ru.rikmasters.gilty.presentation.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.presentation.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.presentation.model.notification.RespondModel
import ru.rikmasters.gilty.presentation.ui.shared.RowActionBar
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun NotificationRespondsComposePreview() {
    GiltyTheme {
        NotificationRespondsCompose(
            NotificationRespondsState(
                RespondType.RECEIVED,
                listOf(
                    DemoSendRespondsModel,
                    DemoSendRespondsModel,
                    DemoReceivedRespondsModel
                )
            )
        )
    }
}

interface NotificationRespondsCallback : RespondsComposeCallback {
    fun onTypeChange(type: RespondType)
}

data class NotificationRespondsState(
    val type: RespondType,
    val responds: List<RespondModel>,
)

@Composable
fun NotificationRespondsCompose(
    state: NotificationRespondsState,
    modifier: Modifier = Modifier,
    callback: RespondsComposeCallback? = null
) {
    Column(modifier.fillMaxSize()) {
        RowActionBar(stringResource(R.string.profile_responds_label)) { callback?.onBack() }
        RespondsCompose(getSendResponds(state.responds, state.type))
    }
}

private fun getSendResponds(
    responds: List<RespondModel>,
    type: RespondType
): List<RespondModel> {
    val list = arrayListOf<RespondModel>()
    responds.forEach { if (it.type == type) list.add(it) }
    return list
}