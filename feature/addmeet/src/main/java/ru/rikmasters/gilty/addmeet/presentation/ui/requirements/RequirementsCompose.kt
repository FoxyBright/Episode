package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Element
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
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
                listOf(true), listOf(true), (false)
            )
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
        if (!state.private)
            Element(
                MemberCountInput(state.memberCount)
                { callback?.onCountChange(it) },
                Modifier.padding(bottom = 12.dp)
            )
        CheckBoxCard(
            stringResource(R.string.requirements_private_check),
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state.private
        ) { callback?.onHideMeetPlaceClick() }
        Text(
            stringResource(R.string.requirements_private_check_label),
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 32.dp, bottom = 12.dp),
            colorScheme.onTertiary,
            style = typography.headlineSmall
        )
        if (state.memberCount.isNotBlank()
            && !state.private
        ) RequirementsList(state, Modifier.padding(horizontal = 16.dp), callback)
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 48.dp, top = 28.dp)
                .padding(horizontal = 16.dp),
            Arrangement.Center, Alignment.CenterHorizontally
        ) {
            val enabled = (state.memberCount.isNotEmpty()
                    && state.memberCount.toInt() != 0
                    && state.gender.isNotEmpty()
                    && state.age.isNotEmpty()
                    && state.orientation.isNotEmpty())
                    || state.private
            GradientButton(
                Modifier, stringResource(R.string.add_meet_publish_button), enabled
            ) { callback?.onNext() }
            Dashes((5), (4), Modifier.padding(top = 16.dp))
        }
    }
    GAlert(state.alert, { callback?.onCloseAlert(false) },
        stringResource(R.string.add_meet_exit_alert_title),
        Modifier, stringResource(R.string.add_meet_exit_alert_details),
        success = Pair(stringResource(R.string.exit_button))
        { callback?.onCloseAlert(false); callback?.onClose() },
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onCloseAlert(false) })
}