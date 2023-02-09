package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
            RespondsListState(
                listOf(
                    Pair(
                        DemoMeetingModel,
                        listOf(
                            DemoReceivedRespondsModel,
                            DemoReceivedShortRespondModelWithoutPhoto
                        )
                    ),
                    Pair(
                        DemoMeetingModel,
                        listOf(DemoSendRespondsModel)
                    )
                ), listOf(0, 1)
            ), Modifier
                .background(colorScheme.background)
                .padding(16.dp)
        )
    }
}

data class RespondsListState(
    val responds: List<Pair<MeetingModel, List<ShortRespondModel>>>,
    val groupStates: List<Int>,
)

interface RespondsListCallback {
    
    fun onTabChange(tab: Int) {}
    fun onBack() {}
    fun onCancelClick(respond: ShortRespondModel) {}
    fun onRespondClick(meet: MeetingModel) {}
    fun onAcceptClick(respond: ShortRespondModel) {}
    fun onImageClick(image: AvatarModel) {}
    fun onArrowClick(index: Int) {}
}

@Composable
fun RespondsListContent(
    state: RespondsListState,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    LazyColumn(modifier) {
        itemsIndexed(state.responds) { i, it ->
            GroupList(
                i, it.first, it.second, Modifier,
                state.groupStates.contains(i), callback
            )
        }
    }
}

@Composable
private fun GroupList(
    index: Int,
    meet: MeetingModel,
    responds: List<ShortRespondModel>,
    modifier: Modifier = Modifier,
    state: Boolean = false,
    callback: RespondsListCallback?,
) {
    Column(modifier) {
        Row(verticalAlignment = CenterVertically) {
            RowActionBar(
                meet.title, Modifier.clickable(
                    MutableInteractionSource(), (null)
                ) {
                    callback?.onArrowClick(index)
                }, responds.size.toString()
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
                    it, Modifier.padding(bottom = 6.dp),
                    callback
                )
            }
        }
    }
}


