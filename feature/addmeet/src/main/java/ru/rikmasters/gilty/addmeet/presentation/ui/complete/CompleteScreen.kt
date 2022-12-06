package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ONLINE
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.*
import java.util.UUID

@Composable
fun CompleteScreen(nav: NavState = get()) {
    val meet = FullMeetingModel(
        id = UUID.randomUUID(),
        title = "Поход в кино",
        condition = ConditionType.MEMBER_PAY,
        category = DemoShortCategoryModel,
        duration = "2 часа",
        type = MeetType.GROUP,
        dateTime = "2022-11-28T20:00:54.140Z",
        organizer = DemoOrganizerModel,
        isOnline = ONLINE,
        tags = DemoTagList,
        description = "Описание вечеринки",
        isPrivate = false,
        memberCount = 4,
        requirements = ListDemoMeetingRequirementModel,
        place = "Москва-сити",
        address = "Москва, ул. Пушкина 42",
        hideAddress = false,
        price = 600
    )
    val context = LocalContext.current
    CompleteContent(meet, Modifier, ONLINE, object: CompleteCallBack {
        override fun onShare() {
            Toast.makeText(
                context,
                "ВААААУ, Типа поделился) Съешь пирожок с полки",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        override fun onClose() {
            nav.navigateAbsolute("main/meetings")
        }
    })
}