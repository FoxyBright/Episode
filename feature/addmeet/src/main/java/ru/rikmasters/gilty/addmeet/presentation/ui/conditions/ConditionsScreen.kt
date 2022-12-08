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
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.*
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import java.util.UUID

var MEETING: FullMeetingModel = FullMeetingModel(
    id = UUID.randomUUID(),
    title = DemoTag.title,
    condition = FREE,
    category = DemoShortCategoryModel,
    duration = "",
    type = GROUP,
    dateTime = "1970-01-01T00:00:00Z",
    organizer = DemoOrganizerModel,
    isOnline = false,
    tags = DemoTagList,
    description = "",
    isPrivate = false,
    memberCount = 4,
    requirements = ListDemoMeetingRequirementModel,
    place = "",
    address = "",
    hideAddress = false,
    price = 0
)

@Composable
fun ConditionsScreen(nav: NavState = get()) {
    var text by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    var online by remember { mutableStateOf(MEETING.isOnline) }
    val hiddenPhoto = remember { mutableStateOf(false) }
    var restrictChat by remember { mutableStateOf(false) }
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
        text, colors, focus, alert, restrictChat
    )
    ConditionContent(state, Modifier,
        object: ConditionsCallback {
            override fun onOnlineClick() {
                online = !online
                MEETING.isOnline = online
            }
            
            override fun onRestrictClick() {
                restrictChat = !restrictChat
            }
            
            override fun onCloseAlert(it: Boolean) {
                alert = it
            }
            
            override fun onPriceChange(price: String) {
                text = price
                MEETING.price = if(price.isBlank())
                    0 else price.toInt()
            }
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onClear() {
                text = ""
                MEETING.price = 0
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
                repeat(conditionList.size) { item ->
                    conditionList[item] = it == item
                }
                MEETING.condition = when(it) {
                    0 -> FREE
                    1 -> DIVIDE
                    2 -> ORGANIZER_PAY
                    3 -> MEMBER_PAY
                    else -> NO_MATTER
                }
            }
            
            override fun onMeetingTypeSelect(it: Int) {
                repeat(meetingTypes.size) { item ->
                    meetingTypes[item] = it == item
                }
                MEETING.type = when(it) {
                    0 -> PERSONAL
                    1 -> GROUP
                    else -> ANONYMOUS
                }
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