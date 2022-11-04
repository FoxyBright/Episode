package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList

@Composable
fun MainScreen(nav: NavState = get()) {
    var grid by remember {
        mutableStateOf(false)
    }
    val stateList = remember {
        mutableStateListOf(
            ACTIVE, NEW, INACTIVE,
            NEW, INACTIVE
        )
    }
    var switcher by remember {
        mutableStateOf(listOf(true, false))
    }
    MainContent(
        MainContentState(
            grid, switcher,
            DemoMeetingList,
            rememberCoroutineScope(),
            stateList
        ), Modifier, object : MainContentCallback {
            override fun onNavBarSelect(point: Int) {
                repeat(stateList.size) {
                    if (it == point) stateList[it] = ACTIVE
                    else if (stateList[it] != NEW)
                        stateList[it] = INACTIVE
                }
            }

            override fun onTodayChange() {
                switcher = switcher.reversed()
            }

            override fun onRespond(avatar: String) {
                nav.navigateAbsolute(
                    "main/reaction?avatar=$avatar"
                )
            }

            override fun onStyleChange() {
                grid = !grid
            }
        })
}