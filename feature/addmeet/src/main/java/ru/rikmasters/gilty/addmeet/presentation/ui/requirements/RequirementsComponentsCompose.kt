package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.onNull
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.numberMask
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun RequirementsList(
    state: RequirementsState,
    modifier: Modifier = Modifier,
    callback: RequirementsCallback? = null,
) {
    Column(modifier) {
        val selected = state.selectedMember
        if(state.memberCount.toInt() > 1
            || state.meetType != PERSONAL
        ) {
            GiltyTab(
                listOf(
                    stringResource(R.string.requirements_filter_all),
                    stringResource(R.string.requirements_filter_personal)
                ),
                state.selectTab,
                Modifier, state.online,
            ) { callback?.onTabClick(it) }
            if(state.selectTab == 1) LazyRow(
                Modifier.fillMaxWidth()
            ) {
                items(state.memberCount.toInt()) {
                    GiltyChip(
                        Modifier.padding(top = 4.dp, end = 8.dp),
                        "${it + 1}", (it == selected), state.online
                    ) { callback?.onMemberClick(it) }
                }
            }
        }
        CardRow(
            stringResource(R.string.sex), state.gender ?: "",
            ThemeExtra.shapes.mediumTopRoundedShape,
            state.online, Modifier.padding(top = 8.dp),
        ) { callback?.onGenderClick() }
        Divider(Modifier.padding(start = 16.dp))
        CardRow(
            stringResource(R.string.personal_info_age_placeholder),
            state.age ?: "",
            ThemeExtra.shapes.zero, state.online
        ) { callback?.onAgeClick() }
        Divider(Modifier.padding(start = 16.dp))
        CardRow(
            stringResource(R.string.orientation_title),
            state.orientation ?: "",
            ThemeExtra.shapes.mediumBottomRoundedShape,
            state.online
        ) { callback?.onOrientationClick() }
    }
}

@Composable
fun responds(
    isOnline: Boolean,
    withoutRespond: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) = FilterModel(stringResource(R.string.profile_responds_label)) {
    TrackCard(
        stringResource(R.string.add_meet_without_respond_title),
        stringResource(R.string.add_meet_without_respond_label),
        isOnline, withoutRespond, modifier
    ) { onClick?.let { it() } }
}

@Composable
fun TrackCard(
    title: String,
    label: String,
    isOnline: Boolean,
    withoutRespond: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(modifier) {
        CheckBoxCard(
            title, Modifier.fillMaxWidth(),
            withoutRespond, online = isOnline
        ) { onClick?.let { it() } }
        Text(
            label, Modifier
                .fillMaxWidth()
                .padding(
                    top = 4.dp, start = 16.dp,
                    bottom = 12.dp
                ), colorScheme.onTertiary,
            style = typography.headlineSmall
        )
    }
}

@Composable
fun memberCountInput(
    text: String,
    isOnline: Boolean,
    memberLimited: Boolean,
    modifier: Modifier = Modifier,
    onMemberLimit: (() -> Unit)? = null,
    onChange: ((String) -> Unit)? = null,
) = FilterModel(stringResource(R.string.requirements_member_count_label)) {
    Column(modifier) {
        TrackCard(
            stringResource(R.string.add_meet_member_limit_title),
            stringResource(R.string.add_meet_member_limit_label),
            isOnline, memberLimited
        ) { onMemberLimit?.let { it() } }
        GTextField(
            text, {
                if(it.matches(Regex("^\\d+\$"))
                    || it.isEmpty()
                ) onChange?.let { text -> text(onNull(it)) }
            }, Modifier.fillMaxWidth(), memberLimited,
            isError = try {
                text.toInt() < 2
            } catch(_: Exception) {
                false
            }, shape = shapes.medium,
            errorBottomText = stringResource(R.string.add_meet_member_limit_error),
            colors = descriptionColors(isOnline),
            label = if(text.isNotEmpty()) textFieldLabel(
                (true), stringResource(R.string.requirements_member_count_place_holder)
            ) else null, placeholder = textFieldLabel(
                (false), stringResource(R.string.requirements_member_count_place_holder)
            ), textStyle = typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = NumberPassword
            ), visualTransformation = transformationOf(
                numberMask(text.length)
            )
        )
    }
}
