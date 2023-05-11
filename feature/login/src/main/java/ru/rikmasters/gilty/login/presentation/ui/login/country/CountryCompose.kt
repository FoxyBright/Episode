package ru.rikmasters.gilty.login.presentation.ui.login.country

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.feature.login.R
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.*
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
    val countries: List<Country>,
)

@Composable
fun CountryBottomSheetContent(
    state: CountryBottomSheetState,
    modifier: Modifier = Modifier,
    callback: CountryCallBack? = null,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(vertical = 28.dp)
    ) {
        SearchBar(state, Modifier, callback)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            Modifier
                .padding(horizontal = 16.dp)
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            itemsIndexed(state.countries) { index, country ->
                CountryRow(
                    country = country,
                    index = index,
                    size = state.countries.size,
                ) { callback?.onCountrySelect(country) }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SearchBar(
    state: CountryBottomSheetState,
    modifier: Modifier = Modifier,
    callback: CountryCallBack?,
) {
    SearchActionBar(
        SearchState(
            stringResource(R.string.search_name),
            (state.searchState || state.text.isNotBlank()),
            state.text, { callback?.onSearchTextChange(it) },
        ) { callback?.onSearchStateChange() },
        modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryRow(
    country: Country,
    index: Int, size: Int,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = modifier,
        colors = cardColors(
            containerColor = colorScheme
                .primaryContainer
        ), shape = lazyItemsShapes(index, size)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp, 16.dp),
            SpaceBetween,
            CenterVertically
        ) {
            CountryLabel(
                country.flag,
                country.name,
                Modifier.weight(1f)
            )
            CountryDial(country.phoneDial)
        }
    }
    if(index < size - 1) GDivider(
        Modifier.padding(start = 54.dp)
    )
}

@Composable
private fun CountryLabel(
    flag: ImageBitmap,
    name: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        Arrangement.Start,
        CenterVertically
    ) {
        Image(
            flag, (null),
            Modifier.size(24.dp)
        )
        Text(
            name, Modifier.padding(start = 16.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium
        )
    }
}

@Composable
private fun CountryDial(
    dial: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.width(IntrinsicSize.Max),
        Arrangement.Start,
        CenterVertically
    ) {
        Text(
            dial, Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colorScheme.scrim,
            style = typography.bodyMedium
        )
        Icon(
            Filled.KeyboardArrowRight,
            (null), Modifier.size(28.dp),
            colorScheme.scrim
        )
    }
}