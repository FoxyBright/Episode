package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class RequirementsState(
    val private: Boolean,
    val memberCount: String,
    val gender: String,
    val age: String,
    val orientation: String,
    val selectedTabs: List<Boolean>,
    val selectedMember: List<Boolean>,
    val alert: Boolean,
    val online: Boolean
)

interface RequirementsCallback {
    
    fun onHideMeetPlaceClick() {}
    fun onCountChange(text: String) {}
    fun onClose() {}
    fun onBack() {}
    fun onNext() {}
    fun onGenderClick() {}
    fun onAgeClick() {}
    fun onOrientationClick() {}
    fun onTabClick(it: Int) {}
    fun onMemberClick(it: Int) {}
    fun onCloseAlert(it: Boolean) {}
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
                listOf(true), listOf(true),
                (false), (false)
            ), Modifier.background(colorScheme.background)
        )
    }
}

@Composable
fun RequirementsContent(
    state: RequirementsState,
    modifier: Modifier = Modifier,
    callback: RequirementsCallback? = null
) {
    Column(
        modifier.fillMaxSize()
    ) {
        ClosableActionBar(
            stringResource(R.string.requirements_title),
            (null), Modifier.padding(bottom = 10.dp),
            { callback?.onCloseAlert(true) }
        ) { callback?.onBack() }
        if(MEETING.type != PERSONAL)
            Element(
                MemberCountInput(state.memberCount, state.online)
                { callback?.onCountChange(it) },
                Modifier.padding(bottom = 12.dp)
            )
        CheckBoxCard(
            stringResource(R.string.requirements_private_check),
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state.private, online = state.online
        ) { callback?.onHideMeetPlaceClick() }
        Text(
            stringResource(R.string.requirements_private_check_label),
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 32.dp, bottom = 12.dp),
            colorScheme.onTertiary,
            style = typography.headlineSmall
        )
        if(state.memberCount.isNotBlank()
            && !state.private
        ) RequirementsList(
            state,
            Modifier.padding(horizontal = 16.dp),
            callback
        )
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 48.dp, top = 28.dp)
                .padding(horizontal = 16.dp),
            Arrangement.Center, Alignment.CenterHorizontally
        ) {
            val enabled = /*(state.memberCount.isNotEmpty()
                    && state.memberCount.toInt() != 0
                    && state.gender.isNotEmpty()
                    && state.age.isNotEmpty()
                    && state.orientation.isNotEmpty())
                    || state.private*/ true
            GradientButton(
                Modifier, stringResource(R.string.add_meet_publish_button),
                enabled, state.online
            ) { callback?.onNext() }
            Dashes(
                (4), (3), Modifier.padding(top = 16.dp),
                if(state.online) colorScheme.secondary
                else colorScheme.primary
            )
        }
    }
    CloseAddMeetAlert(
        state.alert,
        { callback?.onCloseAlert(false) },
        { callback?.onCloseAlert(false); callback?.onClose() })
}