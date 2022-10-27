package ru.rikmasters.gilty.mainscreen.presentation.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList

@Composable
fun MainScreen() {
    var grid by remember { mutableStateOf(false) }
    var switcher by remember { mutableStateOf(listOf(true, false)) }
    MainContent(
        MainContentState(
            grid, switcher, DemoMeetingList, rememberCoroutineScope()
        ),
        callback = object : MainContentCallback {
            override fun onTodayChange() {
                switcher = switcher.reversed()
            }

            override fun onStyleChange() {
                grid = !grid
            }
        })
}