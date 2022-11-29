package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Element
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun DetailedPreview() {
    GiltyTheme {
        DetailedContent(
            DetailedState(
                (""), (""), (""),
                listOf(), (null),
                (true), (false)
            )
        )
    }
}

data class DetailedState(
    val time: String,
    val date: String,
    val description: String,
    val tagList: List<String>,
    val meetPlace: Pair<String, String>?,
    val hideMeetPlace: Boolean,
    val alert: Boolean,
)

interface DetailedCallback {
    fun onBack() {}
    fun onClose() {}
    fun onNext() {}
    fun onDateClick() {}
    fun onTimeClick() {}
    fun onTagsClick() {}
    fun onTagDelete(tag: Int) {}
    fun onDescriptionChange(text: String) {}
    fun onDescriptionClear() {}
    fun onMeetPlaceClick() {}
    fun onHideMeetPlaceClick() {}
    fun onCloseAlert(it: Boolean) {}
}

@Composable
fun DetailedContent(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null
) {
    Column(modifier.fillMaxSize()) {
        ClosableActionBar(
            stringResource(R.string.add_meet_detailed_title),
            (null), Modifier.padding(bottom = 10.dp),
            { callback?.onCloseAlert(true) }
        ) { callback?.onBack() }
        Content(
            Modifier.fillMaxSize(),
            state, callback
        )
    }
    GAlert(state.alert, { callback?.onCloseAlert(false) },
        stringResource(R.string.add_meet_exit_alert_title),
        Modifier, stringResource(R.string.add_meet_exit_alert_details),
        success = Pair(stringResource(R.string.exit_button))
        { callback?.onCloseAlert(false); callback?.onClose() },
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onCloseAlert(false) })
}

@Composable
private fun Content(
    modifier: Modifier,
    state: DetailedState,
    callback: DetailedCallback? = null
) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            Element(
                Tags(state, callback),
                Modifier.padding(top = 18.dp)
            )
        }
        item {
            Element(
                Description(state, callback),
                Modifier.padding(top = 28.dp)
            )
        }
        item {
            Element(
                Additionally(state, callback),
                Modifier.padding(top = 8.dp)
            )
        }
        item {
            MeetPlace(
                state, Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                callback
            )
        }
        item {
            HideMeetPlace(
                state, Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                callback
            )
        }
        item {
            Column(
                Modifier
                    .padding(bottom = 48.dp, top = 28.dp)
                    .padding(horizontal = 16.dp),
                Arrangement.Center, Alignment.CenterHorizontally
            ) {
                val enabled = state.date.isNotEmpty()
                        && state.time.isNotEmpty()
                        && state.tagList.isNotEmpty()
                        && state.time.isNotEmpty()
                        && state.description.isNotEmpty()
                        && state.meetPlace != null
                GradientButton(
                    Modifier, stringResource(R.string.next_button), enabled
                ) { callback?.onNext() }
                Dashes((5), (3), Modifier.padding(top = 16.dp))
            }
        }
    }
}
