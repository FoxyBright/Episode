package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme {
        RespondsListContent(
            listOf(
                Pair(
                    DemoMeetingModel,
                    listOf(
                        DemoReceivedRespondsModel,
                        DemoReceivedRespondModelWithoutPhoto
                    )
                ),
                Pair(DemoMeetingModel, listOf(DemoSendRespondsModel))
            ), listOf(true, false),
            Modifier
                .background(colorScheme.background)
                .padding(16.dp)
        )
    }
}

interface RespondCallback {
    
    fun onTabChange(tab: Int) {}
    fun onBack() {}
    fun onCancelClick(respond: RespondModel) {}
    fun onRespondClick(meet: MeetingModel) {}
    fun onAcceptClick(respond: RespondModel) {}
    fun onImageClick(image: AvatarModel) {}
    fun onArrowClick(index: Int) {}
}

@Composable
fun RespondsListContent(
    responds: List<Pair<MeetingModel, List<RespondModel>>>,
    respondsStates: List<Boolean>,
    modifier: Modifier = Modifier,
    callback: RespondCallback? = null,
) {
    LazyColumn(modifier) {
        itemsIndexed(responds) { i, it ->
            GroupList(
                i, it.first, it.second,
                Modifier, respondsStates[i],
                callback
            )
        }
    }
}

@Composable
private fun GroupList(
    index: Int,
    meet: MeetingModel,
    responds: List<RespondModel>,
    modifier: Modifier = Modifier,
    state: Boolean = false,
    callback: RespondCallback?,
) {
    Column(modifier) {
        Row(verticalAlignment = CenterVertically) {
            RowActionBar(
                meet.title,
                responds.size.toString(),
                Modifier.clickable { callback?.onArrowClick(index) }
            )
            IconButton({ callback?.onArrowClick(index) }) {
                Icon(
                    if(state) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    (null), Modifier.size(24.dp),
                    colorScheme.onTertiary
                )
            }
        }
        if(state) Column {
            responds.forEach {
                Respond(
                    it, callback,
                    Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}


