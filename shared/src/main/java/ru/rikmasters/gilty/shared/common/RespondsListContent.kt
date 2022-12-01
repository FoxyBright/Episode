package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.broken_heart
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme {
        RespondsListContent(
            listOf(
                Pair(
                    DemoFullMeetingModel,
                    listOf(
                        DemoReceivedRespondsModel,
                        DemoReceivedRespondModelWithoutPhoto
                    )
                ),
                Pair(DemoFullMeetingModel, listOf(DemoSendRespondsModel))
            ), listOf(true, false)
        )
    }
}


interface RespondCallback {
    fun onCancelClick(respond: RespondModel) {}
    fun onRespondClick(meet: MeetingModel) {}
    fun onAcceptClick(respond: RespondModel) {}
    fun onImageClick(image: HiddenPhotoModel) {}
    fun onArrowClick(index: Int) {}
}

@Composable
fun RespondsListContent(
    responds: List<Pair<MeetingModel, List<RespondModel>>>,
    respondsStates: List<Boolean>,
    modifier: Modifier = Modifier,
    callback: RespondCallback? = null,
) {
    LazyColumn(modifier.fillMaxSize()) {
        itemsIndexed(responds) { i, it ->
            GroupList(
                i, it.first, it.second,
                respondsStates[i],
                Modifier, callback
            )
        }
    }
}

@Composable
private fun GroupList(
    index: Int,
    meet: MeetingModel,
    responds: List<RespondModel>,
    state: Boolean = false,
    modifier: Modifier = Modifier,
    callback: RespondCallback?
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
                    if (state) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    (null), Modifier.size(24.dp),
                    colorScheme.onTertiary
                )
            }
        }
        if (state) Column {
            responds.forEach {
                Respond(
                    it, callback,
                    Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyResponds(text: String) {
    Box(Modifier.fillMaxSize(), Center) {
        Column(
            Modifier.fillMaxWidth(),
            Top, CenterHorizontally
        ) {
            Icon(
                painterResource(broken_heart), (null),
                Modifier, colorScheme.onTertiary
            )
            Text(
                text, Modifier.padding(top = 26.dp),
                colorScheme.onTertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}


