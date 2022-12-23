package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.PriceTextField
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.ConditionsSelect
import ru.rikmasters.gilty.shared.common.MeetingType
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun ConditionPreview() {
    GiltyTheme {
        ConditionContent(
            ConditionState(
                (false), (true), listOf(),
                listOf(), ("100"),
                (false), (false)
            )
        )
    }
}

data class ConditionState(
    val online: Boolean,
    val hiddenPhoto: Boolean,
    val meetingTypes: List<Boolean>,
    val conditionList: List<Boolean>,
    val text: String,
    val alert: Boolean,
    val restrictChat: Boolean,
)

interface ConditionsCallback {
    
    fun onBack() {}
    fun onNext() {}
    fun onClose() {}
    fun onOnlineClick() {}
    fun onHiddenClick() {}
    fun onRestrictClick() {}
    fun onPriceChange(price: String) {}
    fun onClear() {}
    fun onMeetingTypeSelect(it: Int) {}
    fun onConditionSelect(it: Int) {}
    fun onCloseAlert(it: Boolean) {}
}

@Composable
fun ConditionContent(
    state: ConditionState,
    modifier: Modifier = Modifier,
    callback: ConditionsCallback? = null
) {
    Box(modifier) {
        Column(Modifier.fillMaxSize()) {
            ClosableActionBar(
                stringResource(R.string.add_meet_conditions_continue),
                (null), Modifier.padding(bottom = 10.dp),
                { callback?.onClose() }
            ) { callback?.onBack() }
            Conditions(
                Modifier.fillMaxSize(),
                state, callback
            )
        }
        CrossButton(
            Modifier
                .padding(top = 32.dp, end = 16.dp)
                .size(20.dp)
                .align(TopEnd)
        ) { callback?.onCloseAlert(true) }
    }
    CloseAddMeetAlert(
        state.alert,
        { callback?.onCloseAlert(false) },
        { callback?.onCloseAlert(false); callback?.onClose() })
}

@Composable
private fun Conditions(
    modifier: Modifier = Modifier,
    state: ConditionState,
    callback: ConditionsCallback?
) {
    val pay = state.conditionList.component4()
    LazyColumn(modifier) {
        item {
            Element(
                Type(state, callback),
                Modifier.padding(top = 18.dp)
            )
        }
        item {
            Element(
                Conditions(state, callback),
                Modifier.padding(top = 28.dp)
            )
        }
        if(pay)
            item {
                Element(
                    Price(state, callback),
                    Modifier.padding(top = 28.dp)
                )
            }
        item {
            Element(
                Additional(state, callback),
                Modifier.padding(top = 28.dp)
            )
        }
        item {
            Column(
                Modifier
                    .padding(bottom = 48.dp, top = 28.dp)
                    .padding(horizontal = 16.dp),
                Arrangement.Center, Alignment.CenterHorizontally
            ) {
                val enabled = /*(state.conditionList.contains(true)
                        && state.meetingTypes.contains(true))
                        && if (pay) state.text.isNotEmpty() else */true
                GradientButton(
                    Modifier, stringResource(R.string.next_button),
                    enabled, state.online
                ) { callback?.onNext() }
                Dashes(
                    (4), (1), Modifier.padding(top = 16.dp),
                    if(state.online) colorScheme.secondary
                    else colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun Type(
    state: ConditionState,
    callback: ConditionsCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.meeting_filter_meet_type)) {
        MeetingType(
            state.online,
            state.meetingTypes,
            stringResource(R.string.meeting_only_online_meetings_button), state.online,
            { callback?.onOnlineClick() },
            { it, _ -> callback?.onMeetingTypeSelect(it) }
        )
    }
}

@Composable
private fun Conditions(
    state: ConditionState,
    callback: ConditionsCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.meeting_terms)) {
        ConditionsSelect(state.conditionList, state.online)
        { it, _ -> callback?.onConditionSelect(it) }
    }
}

@Composable
private fun Price(
    state: ConditionState,
    callback: ConditionsCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.add_meet_conditions_price)) {
        PriceTextField(state.text,
            { callback?.onPriceChange(it) },
            { callback?.onClear() }, state.online,
        )
    }
}

@Composable
private fun Additional(
    state: ConditionState,
    callback: ConditionsCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.add_meet_conditions_additionally)) {
        Column {
            CheckBoxCard(
                stringResource(R.string.add_meet_conditions_hidden),
                Modifier.fillMaxWidth(), state.hiddenPhoto,
                online = state.online
            ) { callback?.onHiddenClick() }
            Text(
                stringResource(R.string.add_meet_conditions_hidden_label),
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp),
                colorScheme.onTertiary,
                style = typography.headlineSmall
            )
            if(state.online) {
                CheckBoxCard(
                    stringResource(R.string.add_meet_restrict_chat),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    state.restrictChat,
                    online = true
                ) { callback?.onRestrictClick() }
                Text(
                    stringResource(R.string.add_meet_restrict_chat_label),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 16.dp),
                    colorScheme.onTertiary,
                    style = typography.headlineSmall
                )
            }
        }
    }
}