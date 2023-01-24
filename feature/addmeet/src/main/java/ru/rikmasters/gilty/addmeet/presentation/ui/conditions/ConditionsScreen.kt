package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.ConditionViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.*
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.PERSONAL
import ru.rikmasters.gilty.shared.model.meeting.*
import java.util.UUID.randomUUID

var MEETING: MeetingModel = MeetingModel(
    id = randomUUID().toString(),
    title = DemoTag.title,
    condition = FREE,
    category = DemoCategoryModel,
    duration = "",
    type = GROUP,
    dateTime = "1970-01-01T00:00:00Z",
    organizer = DemoOrganizerModel,
    isOnline = false,
    tags = DemoTagList,
    description = "",
    isPrivate = false,
    memberCount = 4,
    requirements = DemoMeetingRequirementModel,
    place = "",
    address = "",
    hideAddress = false,
    price = 0
)

@Composable
fun ConditionsScreen(vm: ConditionViewModel) {
    
    val nav= get<NavState>()
    var text by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }
    var online by remember { mutableStateOf(MEETING.isOnline) }
    val hiddenPhoto = remember { mutableStateOf(false) }
    var restrictChat by remember { mutableStateOf(false) }
    val meetingTypes =
        remember { mutableStateListOf(false, false, false) }
    val conditionList =
        remember { mutableStateListOf(false, false, false, false, false) }
    val state = ConditionState(
        online, hiddenPhoto.value,
        meetingTypes, conditionList,
        text, alert, restrictChat
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
        })
}