package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Element
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.onNull
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GiltyTab
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

data class RequirementsState(
    val private: Boolean,
    val memberCount: String,
    val gender: String,
    val age: String,
    val orientation: String,
    val selectedTabs: List<Boolean>,
    val selectedMember: List<Boolean>,
)

interface RequirementsCallback {
    fun onHideMeetPlaceClick()
    fun onCountChange(text: String)
    fun onClose()
    fun onBack()
    fun onNext()
    fun onGenderClick()
    fun onAgeClick()
    fun onOrientationClick()
    fun onTabClick(it: Int)
    fun onMemberClick(it: Int)
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
                listOf(true), listOf(true)
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
            { callback?.onClose() }
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
        ) List(state, Modifier.padding(horizontal = 16.dp), callback)
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
}

@Composable
private fun List(
    state: RequirementsState,
    modifier: Modifier = Modifier,
    callback: RequirementsCallback? = null
) {
    Column(modifier) {
        val selected = state.selectedMember
        if (state.memberCount.toInt() > 1) {
            GiltyTab(
                listOf(
                    stringResource(R.string.requirements_filter_all),
                    stringResource(R.string.requirements_filter_personal)
                ), state.selectedTabs
            ) { callback?.onTabClick(it) }
            if (state.selectedTabs.last()) LazyRow(
                Modifier.fillMaxWidth()
            ) {
                items(state.memberCount.toInt()) {
                    GiltyChip(
                        Modifier.padding(top = 4.dp, end = 8.dp),
                        "${it + 1}", selected[it]
                    ) { callback?.onMemberClick(it) }
                }
            }
        }
        Item(
            stringResource(R.string.sex), state.gender,
            ThemeExtra.shapes.mediumTopRoundedShape,
            Modifier.padding(top = 8.dp)
        ) { callback?.onGenderClick() }
        Divider(Modifier.padding(start = 16.dp))
        Item(
            stringResource(R.string.personal_info_age_placeholder), state.age,
            ThemeExtra.shapes.zero
        ) { callback?.onAgeClick() }
        Divider(Modifier.padding(start = 16.dp))
        Item(
            stringResource(R.string.orientation_title), state.orientation,
            ThemeExtra.shapes.mediumBottomRoundedShape
        ) { callback?.onOrientationClick() }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Item(
    label: String,
    text: String,
    shape: Shape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier.fillMaxWidth(), (true), shape,
        CardDefaults.cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween,
            CenterVertically
        ) {
            Text(
                label, Modifier, colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Row(verticalAlignment = CenterVertically) {
                if (text.isNotBlank())
                    Text(
                        text, Modifier, colorScheme.primary,
                        style = typography.bodyMedium,
                    )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun MemberCountInput(
    text: String,
    onChange: ((String) -> Unit)? = null
): FilterModel {
    return FilterModel(stringResource(R.string.requirements_member_count_label)) {
        GTextField(
            text, {
                if (it.matches(Regex("^\\d+\$"))
                    || it.isEmpty()
                ) onChange?.let { text -> text(onNull(it)) }
            }, Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = TextFieldColors(),
            label = if (text.isNotEmpty()) TextFieldLabel(
                (true), stringResource(R.string.requirements_member_count_place_holder)
            ) else null, placeholder = TextFieldLabel(
                (false), stringResource(R.string.requirements_member_count_place_holder)
            ), textStyle = typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ), visualTransformation = textMask(
                numberMask(text.length)
            )
        )
    }
}