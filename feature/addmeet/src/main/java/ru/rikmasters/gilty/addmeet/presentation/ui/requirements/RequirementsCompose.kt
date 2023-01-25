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
import ru.rikmasters.gilty.addmeet.presentation.ui.components.Buttons
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
            ), Modifier.background(colorScheme.background)
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
    Scaffold(modifier, {
        ClosableActionBar(
            stringResource(R.string.requirements_title),
            (null), Modifier.padding(bottom = 10.dp),
            { callback?.onCloseAlert(true) }
        ) { callback?.onBack() }
    }, {
        Buttons(
            Modifier, state.online,
            state.isActive, (3)
        ) { callback?.onNext() }
    }) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(top = it.calculateTopPadding())
        ) {
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
                    state,
                    Modifier.padding(horizontal = 16.dp),
                    callback
                )
            }
            item {
                Element(responds(state.online, state.withoutRespond) {
                    callback?.onWithoutRespondClick()
                })
            }
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        }
    }
    CloseAddMeetAlert(
        state.alert, {
            callback?.onCloseAlert(false)
        }, {
            callback?.onCloseAlert(false)
            callback?.onClose()
        })
}