package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.CityModel
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState

data class CitiesState(
    val selected: CityModel?,
    val cities: List<CityModel>,
    val search: String,
    val searchState: Boolean,
    val alpha: Float,
)

interface CitiesCallback {
    
    fun onSearchChange(query: String)
    fun onSearchStateChange(state: Boolean)
    fun onSelectCity(city: CityModel)
}

@Composable
fun CitiesContent(
    state: CitiesState,
    modifier: Modifier = Modifier,
    callback: CitiesCallback? = null,
) {
    Box(
        modifier.background(
            colorScheme.background
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .alpha(state.alpha)
                .background(colorScheme.primaryContainer)
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            SearchActionBar(
                SearchState(
                    stringResource(R.string.select_city),
                    state.searchState, state.search,
                    { callback?.onSearchChange(it) },
                ) { callback?.onSearchStateChange(it) },
                Modifier.padding(
                    top = if(state.searchState)
                        28.dp else 0.dp,
                    bottom = if(state.searchState)
                        12.dp else 0.dp,
                )
            )
            if(state.cities.isNotEmpty()) LazyColumn(
                Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.primaryContainer)
            ) {
                itemsIndexed(
                    state.cities,
                    { _, it -> it.id },
                    { _, _ -> 0 }
                ) { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { callback?.onSelectCity(item) },
                        SpaceBetween, CenterVertically
                    ) {
                        Text(
                            item.name, Modifier
                                .weight(1f)
                                .padding(16.dp),
                            colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                        if(state.selected?.id == item.id) Image(
                            painterResource(R.drawable.enabled_check_box),
                            (null), Modifier
                                .padding(end = 12.dp)
                                .size(24.dp)
                        )
                    }
                    if(index < state.cities.size - 1) GDivider(
                        Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}