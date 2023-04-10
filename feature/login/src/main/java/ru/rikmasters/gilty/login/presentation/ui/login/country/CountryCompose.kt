package ru.rikmasters.gilty.login.presentation.ui.login.country

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.feature.login.R
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CountryPreview() {
    GiltyTheme {
        CountryBottomSheetContent(
            CountryBottomSheetState(
                "", false,
                (0..20).map { DemoCountry }
            )
        )
    }
}

interface CountryCallBack {
    fun onSearchTextChange(text: String)
    fun onSearchStateChange()
    fun onCountrySelect(country: Country)
}

data class CountryBottomSheetState(
    val text: String,
    val searchState: Boolean,
    val countries: List<Country>
)

@Composable
fun CountryBottomSheetContent(
    state: CountryBottomSheetState,
    modifier: Modifier = Modifier,
    callback: CountryCallBack? = null
) {
    Column(
        modifier
            .padding(16.dp, 20.dp)
    ) {
        SearchActionBar(SearchState(
            stringResource(R.string.search_name),
            state.searchState, state.text,
            { callback?.onSearchTextChange(it) }
        ){ callback?.onSearchStateChange() })
        if (state.countries.isNotEmpty())
            LazyColumn(
                Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                itemsIndexed(
                    state.countries,
                    { _, it -> it.code },
                    { _, _ -> 0 }
                ) { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { callback?.onCountrySelect(item) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Image(
                                    item.flag,
                                    item.name,
                                    Modifier.size(24.dp)
                                )
                                Text(
                                    item.name,
                                    Modifier.padding(start = 16.dp),
                                    MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                Text(
                                    item.phoneDial,
                                    Modifier.padding(start = 16.dp, end = 8.dp),
                                    MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(
                                    Icons.Filled.KeyboardArrowRight,
                                    (null), Modifier, MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    if (index < state.countries.size - 1) GDivider(Modifier.padding(start = 54.dp))
                }
            }
    }
}