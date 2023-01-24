package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.viewmodel.TagsViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchScreen

@Composable
fun TagsScreen(vm:TagsViewModel) {
    
    val nav= get<NavState>()
    val online = remember { mutableStateOf(MEETING.isOnline) }
    TagSearchScreen(online.value, { nav.navigate("detailed") })
    { nav.navigate("detailed") }
}