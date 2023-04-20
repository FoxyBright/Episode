package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

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
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class RequirementsState(
    val private: Boolean,
    val memberCount: String,
    val gender: String?,
    val age: String?,
    val orientation: String?,
    val selectTab: Int,
    val selectedMember: Int,
    val alert: Boolean,
    val meetType: MeetType?,
    val online: Boolean,
    val isActive: Boolean,
    val withoutRespond: Boolean,
    val memberLimited: Boolean,
)

interface RequirementsCallback {
    
    fun onHideMeetPlaceClick() {}
    fun onWithoutRespondClick() {}
    fun onMemberLimit() {}
    fun onCountChange(text: String) {}
    fun onClearCount() {}
    fun onClose() {}
    fun onBack() {}
    fun onNext() {}
    fun onGenderClick() {}
    fun onAgeClick() {}
    fun onOrientationClick() {}
    fun onTabClick(tab: Int) {}
    fun onMemberClick(member: Int) {}
    fun onCloseAlert(state: Boolean) {}
}

@Preview
@Composable
fun RequirementsContent() {
    GiltyTheme {
        val matter = stringResource(R.string.condition_no_matter)
        RequirementsContent(
            RequirementsState(
                (false), ("1"),
                matter, matter, matter,
                (0), (1), (false),
                GROUP, (false), (true),
                (true), (true)
            ), Modifier.background(
                colorScheme.background
            )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RequirementsContent(
    state: RequirementsState,
    modifier: Modifier = Modifier,
    callback: RequirementsCallback? = null,
) {
    Scaffold(
        modifier,
        topBar = {
            ClosableActionBar(
                stringResource(R.string.requirements_title),
                Modifier.padding(bottom = 10.dp), (null),
                { callback?.onCloseAlert(true) }
            ) { callback?.onBack() }
        },
        bottomBar = {
            Buttons(
                Modifier, state.online,
                state.isActive, (3)
            ) { callback?.onNext() }
        }) {
        Content(
            state, Modifier.padding(
                top = it.calculateTopPadding()
            ), callback
        )
    }
    val alClose = { callback?.onCloseAlert(false) }
    CloseAddMeetAlert(
        state.alert, state.online, { alClose() },
        { alClose(); callback?.onClose() }
    )
}

@Composable
private fun Content(
    state: RequirementsState,
    modifier: Modifier,
    callback: RequirementsCallback?,
) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
    ) {
        itemSpacer(10.dp)
        if(state.meetType != PERSONAL) item {
            Element(
                memberCountInput(
                    state.memberCount, state.online,
                    state.memberLimited, Modifier,
                    { callback?.onMemberLimit() },
                    { callback?.onClearCount() }
                ) { count -> callback?.onCountChange(count) },
                Modifier.padding(bottom = 12.dp)
            )
        }
        item {
            TrackCard(
                stringResource(R.string.requirements_private_check),
                stringResource(R.string.requirements_private_check_label),
                state.online, state.private,
                Modifier.padding(horizontal = 16.dp)
            ) { callback?.onHideMeetPlaceClick() }
        }
        if(!state.private) item {
            RequirementsList(
                state, Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp),
                callback
            )
        }
        item {
            Element(
                responds(
                    state.online,
                    state.withoutRespond
                ) { callback?.onWithoutRespondClick() },
                Modifier.padding(top = 24.dp)
            )
        }
        itemSpacer(60.dp)
    }
}