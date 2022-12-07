package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CountryBottomSheetPreview() {
    GiltyTheme {
        CountryBottomSheetContent(
            CountryBottomSheetState(
                (""), (false), listOf()
            )
        )
    }
}

interface CountryBottomSheetCallBack {
    fun onSearchTextChange(text: String) {}
    fun onSearchStateChange() {}
    fun onCountrySelect(country: Country) {}
}

data class CountryBottomSheetState(
    val text: String,
    val searchState: Boolean,
    val Countries: List<Country>
)

@Composable
fun CountryBottomSheetContent(
    state: CountryBottomSheetState,
    modifier: Modifier = Modifier,
    callback: CountryBottomSheetCallBack? = null
) {
    Column(
        modifier
            .fillMaxHeight(0.8f)
            .padding(16.dp, 20.dp)
    ) {
        SearchActionBar(SearchState(
            stringResource(R.string.login_search_name),
            state.searchState, state.text,
            { callback?.onSearchTextChange(it) }
        ){ callback?.onSearchStateChange() })
        if (state.Countries.isNotEmpty())
            LazyColumn(
                Modifier
                    .padding(top = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                itemsIndexed(state.Countries) { index, item ->
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
                                    painterResource(item.flag),
                                    item.country,
                                    Modifier.size(24.dp)
                                )
                                Text(
                                    item.country,
                                    Modifier.padding(start = 16.dp),
                                    MaterialTheme.colorScheme.tertiary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                Text(
                                    item.code,
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
                    if (index < state.Countries.size - 1) Divider(Modifier.padding(start = 54.dp))
                }
            }
    }
}