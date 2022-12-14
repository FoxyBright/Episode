package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.login.Country
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CountryBSPreview() {
    GiltyTheme {
        CountryBottomSheetContent(
            CountryBSState(
                (""), (false), listOf()
            )
        )
    }
}

interface CountryBSCallBack {
    
    fun onSearchTextChange(text: String) {}
    fun onSearchStateChange() {}
    fun onItemSelect(index: Int) {}
}

data class CountryBSState(
    val text: String,
    val searchState: Boolean,
    val countries: List<Country>? = null,
    val cities: List<String>? = null
)

@Composable
fun CountryBottomSheetContent(
    state: CountryBSState,
    modifier: Modifier = Modifier,
    callback: CountryBSCallBack? = null
) {
    Column(
        modifier
            .fillMaxHeight(0.8f)
            .padding(16.dp, 20.dp)
    ) {
        SearchActionBar(SearchState(
            stringResource(R.string.login_select_country),
            state.searchState, state.text,
            { callback?.onSearchTextChange(it) }
        ) { callback?.onSearchStateChange() })
        LazyColumn(
            Modifier
                .padding(top = 10.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            when {
                !state.countries.isNullOrEmpty() -> {
                    itemsIndexed(state.countries) { index, item ->
                        if(index < state.countries.size - 1)
                            Divider(Modifier.padding(start = 54.dp))
                    }
                }
                
                !state.cities.isNullOrEmpty() -> {
                }
            }
        }
    }
}

@Composable
private fun Item(
    text: String,
    image: Int? = null,
    label: String? = null,
    onSelect: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        verticalAlignment = CenterVertically
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween, CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                image?.let {
                    Image(
                        painterResource(it), (null),
                        Modifier.size(24.dp)
                    )
                }
                Text(
                    text, Modifier.padding(start = 16.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                label?.let {
                    Text(
                        it, Modifier.padding(start = 16.dp, end = 8.dp),
                        MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}