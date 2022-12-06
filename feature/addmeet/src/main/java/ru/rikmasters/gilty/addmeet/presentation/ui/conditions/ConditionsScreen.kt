package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.Color.Companion.Transparent
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

var ONLINE: Boolean = false

@Composable
fun ConditionsScreen(nav: NavState = get()) {
    var text by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    var online by remember { mutableStateOf(ONLINE) }
    val hiddenPhoto = remember { mutableStateOf(false) }
    val meetingTypes =
        remember { mutableStateListOf(false, false, false) }
    val conditionList =
        remember { mutableStateListOf(false, false, false, false, false) }
    val unfocusedColor = PriceFieldColors(online)
    val focusedColor = TextFieldColors()
    var focus by remember { mutableStateOf<FocusState?>(null) }
    var colors by remember { mutableStateOf(focusedColor) }
    val state = ConditionState(
        online, hiddenPhoto.value,
        meetingTypes, conditionList,
        text, colors, focus, alert
    )
    ConditionContent(state, Modifier,
        object: ConditionsCallback {
            override fun onOnlineClick() {
                online = !online
                ONLINE = online
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
                if(state.isFocused) {
                    text = text.filter { it.isDigit() }
                    colors = focusedColor
                } else {
                    text = if(text.isNotEmpty())
                        text.filter { it.isDigit() } + " ₽"
                    else text
                    colors = unfocusedColor
                }
            }
        })
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PriceFieldColors(online: Boolean = false): TextFieldColors {
    val textColor by animateColorAsState(
        if(online) colorScheme.secondary
        else colors.priceTextFieldText
    )
    return TextFieldDefaults.textFieldColors(
        textColor = textColor,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.onTertiary,
        focusedLabelColor = colorScheme.tertiary,
        disabledTrailingIconColor = Transparent,
        focusedTrailingIconColor = Transparent,
        unfocusedTrailingIconColor = Transparent,
        focusedIndicatorColor = Transparent,
        unfocusedIndicatorColor = Transparent,
        disabledIndicatorColor = Transparent,
        errorIndicatorColor = Transparent,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = Transparent,
    )
}