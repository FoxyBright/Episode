package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
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
        if(state.memberCount.toInt() > 1) {
            GiltyTab(
                listOf(
                    stringResource(R.string.requirements_filter_all),
                    stringResource(R.string.requirements_filter_personal)
                ), state.selectedTabs,
                Modifier, state.online
            ) { callback?.onTabClick(it) }
            if(state.selectedTabs.last()) LazyRow(
                Modifier.fillMaxWidth()
            ) {
                items(state.memberCount.toInt()) {
                    GiltyChip(
                        Modifier.padding(top = 4.dp, end = 8.dp),
                        "${it + 1}", selected[it], state.online
                    ) { callback?.onMemberClick(it) }
                }
            }
        }
        Item(
            stringResource(R.string.sex), state.gender,
            ThemeExtra.shapes.mediumTopRoundedShape,
            state.online, Modifier.padding(top = 8.dp),
        ) { callback?.onGenderClick() }
        Divider(Modifier.padding(start = 16.dp))
        Item(
            stringResource(R.string.personal_info_age_placeholder), state.age,
            ThemeExtra.shapes.zero, state.online
        ) { callback?.onAgeClick() }
        Divider(Modifier.padding(start = 16.dp))
        Item(
            stringResource(R.string.orientation_title), state.orientation,
            ThemeExtra.shapes.mediumBottomRoundedShape,
            state.online
        ) { callback?.onOrientationClick() }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Item(
    label: String,
    text: String,
    shape: Shape,
    online: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier.fillMaxWidth(),
        (true), shape,
        cardColors(colorScheme.primaryContainer)
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
                if(text.isNotBlank())
                    Text(
                        text, Modifier,
                        if(online) colorScheme.secondary
                        else colorScheme.primary,
                        style = typography.bodyMedium,
                    )
                Icon(
                    Filled.KeyboardArrowRight,
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun MemberCountInput(
    text: String,
    online: Boolean,
    onChange: ((String) -> Unit)? = null
): FilterModel {
    return FilterModel(stringResource(R.string.requirements_member_count_label)) {
        GTextField(
            text, {
                if(it.matches(Regex("^\\d+\$"))
                    || it.isEmpty()
                ) onChange?.let { text -> text(onNull(it)) }
            }, Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = RequirementsCountColor(online),
            label = if(text.isNotEmpty()) TextFieldLabel(
                (true), stringResource(R.string.requirements_member_count_place_holder)
            ) else null, placeholder = TextFieldLabel(
                (false), stringResource(R.string.requirements_member_count_place_holder)
            ), textStyle = typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = NumberPassword
            ), visualTransformation = textMask(
                numberMask(text.length)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequirementsCountColor(online: Boolean) =
    TextFieldDefaults.textFieldColors(
        textColor = colorScheme.tertiary,
        cursorColor = if(online) colorScheme.secondary
        else colorScheme.primary,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.onTertiary,
        focusedLabelColor = if(online) colorScheme.secondary
        else colorScheme.tertiary,
        disabledTrailingIconColor = Color.Transparent,
        focusedTrailingIconColor = Color.Transparent,
        unfocusedTrailingIconColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = Color.Transparent,
    )
