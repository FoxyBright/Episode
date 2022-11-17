package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Element
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.PriceTextField
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.ConditionsSelect
import ru.rikmasters.gilty.shared.common.MeetingType
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.CrossButton
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun ConditionPreview() {
    GiltyTheme {
        ConditionContent(
            ConditionState(
                (false), (true), listOf(), listOf(),
                ("100"), TextFieldColors(), (null)
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
    val priceColors: TextFieldColors,
    val focus: FocusState?
)

interface ConditionsCallback {
    fun onBack() {}
    fun onNext() {}
    fun onClose() {}
    fun onOnlineClick() {}
    fun onHiddenClick() {}
    fun onPriceChange(price: String) {}
    fun onClear() {}
    fun onMeetingTypeSelect(it: Int) {}
    fun onConditionSelect(it: Int) {}
    fun onPriceFocus(state: FocusState) {}
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
                .align(Alignment.TopEnd)
        ) { callback?.onClose() }
    }
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
        if (pay)
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
                val enabled =
                    (state.conditionList.contains(true)
                            && state.meetingTypes.contains(true))
                            && if (pay) state.text.isNotEmpty() else true
                GradientButton(
                    Modifier, stringResource(R.string.next_button), enabled
                ) { callback?.onNext() }
                Dashes((5), (2), Modifier.padding(top = 16.dp))
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
            stringResource(R.string.meeting_only_online_meetings_button),
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
        ConditionsSelect(state.conditionList)
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
            { callback?.onClear() }, state.priceColors,
            state.focus, { callback?.onPriceFocus(it) }
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
                Modifier.fillMaxWidth(), state.hiddenPhoto
            ) { callback?.onHiddenClick() }
            Text(
                stringResource(R.string.add_meet_conditions_hidden_label),
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp),
                MaterialTheme.colorScheme.onTertiary,
                style = typography.headlineSmall
            )
        }
    }
}