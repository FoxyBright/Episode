package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Buttons
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.TagModel
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
                (true), (false),
                (false), (false)
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class DetailedState(
    val time: String,
    val date: String,
    val description: String,
    val tagList: List<TagModel>,
    val meetPlace: Pair<String, String>?,
    val hideMeetPlace: Boolean,
    val alert: Boolean,
    val online: Boolean,
    val isActive: Boolean,
)

interface DetailedCallback {
    
    fun onBack() {}
    fun onClose() {}
    fun onNext() {}
    fun onDateClick() {}
    fun onTimeClick() {}
    fun onTagsClick() {}
    fun onTagDelete(tag: TagModel) {}
    fun onDescriptionChange(text: String) {}
    fun onDescriptionClear() {}
    fun onMeetPlaceClick() {}
    fun onHideMeetPlaceClick() {}
    fun onCloseAlert(state: Boolean) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailedContent(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ClosableActionBar(
                title = stringResource(
                    R.string.add_meet_detailed_title
                ),
                modifier = Modifier
                    .padding(bottom = 10.dp),
                details = null,
                onClose = {
                    callback?.onCloseAlert(
                        state = true
                    )
                }
            ) { callback?.onBack() }
        },
        bottomBar = {
            Buttons(
                modifier = Modifier,
                online = state.online,
                enabled = state.isActive,
                activeDash = 2
            ) { callback?.onNext() }
        }
    ) {
        Content(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(top = it.calculateTopPadding()),
            state = state,
            callback = callback
        )
    }
    CloseAddMeetAlert(
        state = state.alert,
        online = state.online,
        cancel = {
            callback?.onCloseAlert(false)
        },
        success = {
            callback?.onCloseAlert(false)
            callback?.onClose()
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier,
    state: DetailedState,
    callback: DetailedCallback? = null,
) {
    LazyColumn(modifier.fillMaxSize()) {
        item {
            Element(
                tags(state, callback),
                Modifier.padding(top = 18.dp)
            )
        }
        item {
            Element(
                description(state, callback),
                Modifier.padding(top = 28.dp)
            )
        }
        item {
            Element(
                additionally(state, callback),
                Modifier.padding(top = 8.dp)
            )
        }
        if(!state.online) item {
            MeetPlace(
                state, Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                callback
            )
        }
        if(!state.online) item {
            HideMeetPlace(
                state, Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 16.dp),
                callback
            )
        }
        itemSpacer(60.dp)
    }
}
