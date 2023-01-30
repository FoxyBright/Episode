package ru.rikmasters.gilty.profile.presentation.ui.bottoms.responds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Respond
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.common.RespondsListContent
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SEND
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.shared.EmptyScreen
import ru.rikmasters.gilty.shared.shared.GiltyTab
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun RespondsListPreview() {
    GiltyTheme {
        RespondsList(
            listOf(
                Pair(
                    DemoMeetingModel,
                    listOf(
                        DemoReceivedRespondsModel,
                        DemoReceivedRespondModelWithoutPhoto,
                        DemoSendRespondsModel,
                        DemoSendRespondsModel
                    )
                )
            ), listOf(true, false), (0)
        )
    }
}

@Composable
fun RespondsList(
    responds: List<Pair<MeetingModel, List<RespondModel>>>,
    respondsStates: List<Boolean>,
    selectTab: Int,
    modifier: Modifier = Modifier,
    callback: RespondCallback? = null,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Text(
            stringResource(R.string.profile_responds_label),
            Modifier
                .padding(
                    start = 56.dp,
                    bottom = 24.dp,
                    top = 12.dp
                ),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        GiltyTab(
            listOf(
                stringResource(R.string.profile_sent_responds),
                stringResource(R.string.profile_received_responds)
            ), selectTab, Modifier.padding(horizontal = 16.dp)
        ) { callback?.onTabChange(it) }
        if(selectTab == 0) SentResponds(
            Modifier.padding(16.dp, 8.dp),
            responds, callback
        )
        else ReceivedResponds(
            Modifier.padding(16.dp, 8.dp),
            responds, respondsStates, callback
        )
    }
}

@Composable
private fun SentResponds(
    modifier: Modifier = Modifier,
    responds: List<Pair<MeetingModel, List<RespondModel>>>,
    callback: RespondCallback? = null,
) {
    val sentResponds =
        arrayListOf<RespondModel>()
    responds.forEach {
        it.second.forEach { respond ->
            if(respond.type == SEND)
                sentResponds.add(respond)
        }
    }
    if(sentResponds.isNotEmpty())
        LazyColumn(modifier.fillMaxWidth()) {
            items(sentResponds) {
                Respond(
                    it, callback,
                    Modifier.padding(bottom = 12.dp)
                )
            }
        }
    else EmptyScreen(
        stringResource(
            R.string.profile_hasnt_received_responds
        ), R.drawable.broken_heart
    )
}

@Composable
private fun ReceivedResponds(
    modifier: Modifier = Modifier,
    responds: List<Pair<MeetingModel, List<RespondModel>>>,
    respondsStates: List<Boolean>,
    callback: RespondCallback? = null,
) {
    val listOfResponds =
        arrayListOf<RespondModel>()
    val list =
        arrayListOf<Pair<MeetingModel, List<RespondModel>>>()
    responds.forEach { pair ->
        listOfResponds.clear()
        pair.second.forEach {
            if(it.type == RECEIVED)
                listOfResponds.add(it)
        }; if(listOfResponds.isNotEmpty())
        list.add(Pair(pair.first, listOfResponds))
    }
    if(list.isNotEmpty())
        RespondsListContent(
            list, respondsStates,
            modifier, callback
        )
    else EmptyScreen(
        stringResource(
            R.string.profile_hasnt_sent_responds
        ), R.drawable.broken_heart
    )
}