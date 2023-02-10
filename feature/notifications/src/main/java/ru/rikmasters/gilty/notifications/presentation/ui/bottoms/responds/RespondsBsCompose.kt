package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.RespondsListCallback
import ru.rikmasters.gilty.shared.common.RespondsListContent
import ru.rikmasters.gilty.shared.common.RespondsListState
import ru.rikmasters.gilty.shared.model.notification.DemoMeetWithRespondsModel
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun RespondsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            RespondsBsContent(
                NotificationRespondsState(
                    listOf(
                        DemoMeetWithRespondsModel,
                        DemoMeetWithRespondsModel
                    ), listOf(1)
                )
            )
        }
    }
}

data class NotificationRespondsState(
    val responds: List<MeetWithRespondsModel>,
    val respondsStates: List<Int>,
)

@Composable
fun RespondsBsContent(
    state: NotificationRespondsState,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        RowActionBar(
            stringResource(R.string.profile_responds_label),
            modifier = Modifier.padding(start = 16.dp, top = 28.dp)
        )
        RespondsListContent(
            RespondsListState(
                state.responds,
                state.respondsStates
            ), Modifier.padding(16.dp), callback
        )
    }
}