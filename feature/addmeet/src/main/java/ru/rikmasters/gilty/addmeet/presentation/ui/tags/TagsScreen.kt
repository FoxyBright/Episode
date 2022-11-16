package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchScreen

@Composable
fun TagsScreen(nav: NavState = get()) {
    TagSearchScreen({ nav.navigate("detailed") })
    { nav.navigate("detailed") }
}