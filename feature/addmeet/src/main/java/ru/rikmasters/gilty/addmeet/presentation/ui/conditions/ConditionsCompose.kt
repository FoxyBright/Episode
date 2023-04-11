package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Buttons
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
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
                (true), (true), listOf(2),
                listOf(3), (true), ("1000"),
                (false), (true)
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class ConditionState(
    val online: Boolean,
    val hiddenPhoto: Boolean,
    val meetType: List<Int>,
    val condition: List<Int>,
    val chatForbidden: Boolean,
    val price: String,
    val alert: Boolean,
    val isActive: Boolean,
)

interface ConditionsCallback {
    
    fun onBack() {}
    fun onNext() {}
    fun onClose() {}
    fun onOnlineClick() {}
    fun onHiddenClick() {}
    fun onForbiddenClick() {}
    fun onPriceChange(price: String) {}
    fun onClear() {}
    fun onMeetingTypeSelect(type: Int) {}
    fun onConditionSelect(condition: Int) {}
    fun onCloseAlert(state: Boolean) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ConditionContent(
    state: ConditionState,
    modifier: Modifier = Modifier,
    callback: ConditionsCallback? = null,
) {
    Scaffold(modifier, {
        ClosableActionBar(
            stringResource(R.string.add_meet_conditions_continue),
            Modifier.padding(bottom = 10.dp), (null),
            { callback?.onCloseAlert(true) }
        ) { callback?.onBack() }
    }, {
        Buttons(
            Modifier, state.online,
            state.isActive, (1)
        ) { callback?.onNext() }
    }) {
        Content(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(top = it.calculateTopPadding()),
            state, callback
        )
    }
    CloseAddMeetAlert(
        state.alert, state.online, {
            callback?.onCloseAlert(false)
        }, {
            callback?.onCloseAlert(false)
            callback?.onClose()
        })
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: ConditionState,
    callback: ConditionsCallback?,
) {
    val pay = state.condition.contains(3)
    LazyColumn(modifier) {
        itemSpacer(10.dp)
        item {
            Element(
                type(state, callback),
                Modifier.padding(top = 18.dp)
            )
        }
        item {
            Element(
                conditions(state, callback),
                Modifier.padding(top = 28.dp)
            )
        }
        if(pay)
            item {
                Element(
                    price(state, callback),
                    Modifier.padding(top = 28.dp)
                )
            }
        item {
            Element(
                additional(state, callback),
                Modifier.padding(top = 28.dp)
            )
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

@Composable
private fun type(
    state: ConditionState,
    callback: ConditionsCallback? = null,
) = FilterModel(stringResource(R.string.meeting_filter_meet_type)) {
    MeetingType(
        state.online,
        state.meetType,
        stringResource(R.string.meeting_make_online_button),
        state.online,
        { callback?.onOnlineClick() },
        { callback?.onMeetingTypeSelect(it) }
    )
}

@Composable
private fun conditions(
    state: ConditionState,
    callback: ConditionsCallback? = null,
) = FilterModel(stringResource(R.string.meeting_terms)) {
    ConditionsSelect(state.condition, state.online)
    { callback?.onConditionSelect(it) }
}

@Composable
private fun price(
    state: ConditionState,
    callback: ConditionsCallback? = null,
) = FilterModel(stringResource(R.string.add_meet_conditions_price)) {
    PriceTextField(
        state.price, { callback?.onPriceChange(it) },
        { callback?.onClear() }, state.online,
    )
}

@Composable
private fun additional(
    state: ConditionState,
    callback: ConditionsCallback? = null,
) = FilterModel(stringResource(R.string.add_meet_conditions_additionally)) {
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
                state.chatForbidden,
                online = true
            ) { callback?.onForbiddenClick() }
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