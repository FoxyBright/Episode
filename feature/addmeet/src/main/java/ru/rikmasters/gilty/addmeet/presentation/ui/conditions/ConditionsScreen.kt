package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.shared.PriceFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldColors

@Composable
fun ConditionsScreen(nav: NavState = get()) {
    var text by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    val online = remember { mutableStateOf(false) }
    val hiddenPhoto = remember { mutableStateOf(false) }
    val meetingTypes =
        remember { mutableStateListOf(false, false, false) }
    val conditionList =
        remember { mutableStateListOf(false, false, false, false, false) }
    val unfocusedColor = PriceFieldColors()
    val focusedColor = TextFieldColors()
    var focus by remember { mutableStateOf<FocusState?>(null) }
    var colors by remember { mutableStateOf(focusedColor) }
    val state = ConditionState(
        online.value, hiddenPhoto.value,
        meetingTypes, conditionList,
        text, colors, focus, alert
    )
    ConditionContent(state, Modifier, object : ConditionsCallback {
        override fun onOnlineClick() {
            online.value = !online.value
        }

        override fun onCloseAlert(it: Boolean) {
            alert = it
        }

        override fun onPriceChange(price: String) {
            text = price
        }

        override fun onClose() {
            nav.navigateAbsolute("main/meetings")
        }

        override fun onClear() {
            text = ""
        }

        override fun onHiddenClick() {
            hiddenPhoto.value = !hiddenPhoto.value
        }

        override fun onBack() {
            nav.navigate("category")
        }

        override fun onNext() {
            nav.navigate("detailed")
        }

        override fun onConditionSelect(it: Int) {
            repeat(conditionList.size) { item -> conditionList[item] = it == item }
        }

        override fun onMeetingTypeSelect(it: Int) {
            repeat(meetingTypes.size) { item -> meetingTypes[item] = it == item }
        }

        override fun onPriceFocus(state: FocusState) {
            focus = state
            if (state.isFocused) {
                text = text.filter { it.isDigit() }
                colors = focusedColor
            } else {
                text = if (text.isNotEmpty())
                    text.filter { it.isDigit() } + " ₽"
                else text
                colors = unfocusedColor
            }
        }
    })
}
