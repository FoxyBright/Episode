package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun DetailedPreview() {
    GiltyTheme {
        DetailedContent(
            DetailedState(
                (""), (""), (""),
                listOf(), (null),
                (true), (false), (false)
            ), Modifier.background(colorScheme.background)
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
    val online: Boolean
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
    CloseAddMeetAlert(
        state.alert,
        { callback?.onCloseAlert(false) },
        { callback?.onCloseAlert(false);callback?.onClose() })
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
        if(!MEETING.isOnline) item {
            MeetPlace(
                state, Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                callback
            )
        }
        if(!MEETING.isOnline) item {
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
                val enabled = /*state.date.isNotEmpty()
                        && state.time.isNotEmpty()
                        && state.tagList.isNotEmpty()
                        && state.time.isNotEmpty()
                        && state.description.isNotEmpty()
                        && state.meetPlace != null*/ true
                GradientButton(
                    Modifier, stringResource(R.string.next_button),
                    enabled, state.online
                ) { callback?.onNext() }
                Dashes(
                    (4), (2), Modifier.padding(top = 16.dp),
                    if(state.online) colorScheme.secondary
                    else colorScheme.primary
                )
            }
        }
    }
}
