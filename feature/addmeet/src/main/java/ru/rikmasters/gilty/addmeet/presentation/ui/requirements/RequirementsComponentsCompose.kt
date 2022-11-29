package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.onNull
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GiltyTab
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun RequirementsList(
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
        CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                label, Modifier, MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (text.isNotBlank())
                    Text(
                        text, Modifier, MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun MemberCountInput(
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
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldColors(),
            label = if (text.isNotEmpty()) TextFieldLabel(
                (true), stringResource(R.string.requirements_member_count_place_holder)
            ) else null, placeholder = TextFieldLabel(
                (false), stringResource(R.string.requirements_member_count_place_holder)
            ), textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ), visualTransformation = textMask(
                numberMask(text.length)
            )
        )
    }
}
