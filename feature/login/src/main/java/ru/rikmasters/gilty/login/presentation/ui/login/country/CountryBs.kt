package ru.rikmasters.gilty.login.presentation.ui.login.country

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.login.viewmodel.bottoms.CountryBsViewModel
import ru.rikmasters.gilty.shared.country.Country

@Composable
fun CountryBs(vm: CountryBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val countries by vm.countries.collectAsState()
    val searchText by vm.query.collectAsState()
    
    var searchState by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { vm.loadCountries() }
    
    Use(vm, LoadingTrait) {
        CountryBottomSheetContent(
            CountryBottomSheetState(searchText, searchState, countries),
            Modifier, object: CountryCallBack {
                
                override fun onCountrySelect(country: Country) {
                    scope.launch {
                        vm.select(country)
                        asm.bottomSheet.collapse()
                    }
                }
                
                override fun onSearchTextChange(text: String) {
                    scope.launch { vm.changeQuery(text) }
                }
                
                override fun onSearchStateChange() {
                    scope.launch {
                        vm.changeQuery("")
                        searchState = !searchState
                    }
                }
            }
        )
    }
}