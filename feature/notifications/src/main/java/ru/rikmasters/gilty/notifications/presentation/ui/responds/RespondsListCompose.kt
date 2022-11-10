package ru.rikmasters.gilty.notifications.presentation.ui.responds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.common.RespondsListContent
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun NotificationRespondsPreview() {
    GiltyTheme {
        NotificationRespondsContent(
            NotificationRespondsState(
                listOf(
                    Pair(
                        DemoFullMeetingModel,
                        listOf(
                            DemoReceivedRespondsModel,
                            DemoReceivedRespondModelWithoutPhoto
                        )
                    ),
                    Pair(
                        DemoFullMeetingModel,
                        listOf(DemoSendRespondsModel)
                    )
                ), listOf(true, false)
            )
        )
    }
}

data class NotificationRespondsState(
    val responds: List<Pair<MeetingModel, List<RespondModel>>>,
    val respondsStates: List<Boolean>
)

@Composable
fun NotificationRespondsContent(
    state: NotificationRespondsState,
    modifier: Modifier = Modifier,
    callback: RespondCallback? = null
) {
    Column(modifier.fillMaxSize()) {
        RowActionBar(
            stringResource(R.string.profile_responds_label),
            modifier = Modifier.padding(start = 16.dp, top = 28.dp)
        )
        RespondsListContent(
            state.responds, state.respondsStates,
            Modifier.padding(16.dp), callback
        )
    }
}