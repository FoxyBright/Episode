package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.CityModel

@Composable
fun CitiesScreen(vm: FiltersBsViewModel, alpha: Float) {
    
    val scope = rememberCoroutineScope()
    
    val searchState by vm.searchCityState.collectAsState()
    val cities by vm.cityList.collectAsState()
    val search by vm.searchCity.collectAsState()
    val selected by vm.city.collectAsState()
    
    LaunchedEffect(Unit) { vm.getCities() }
    
    Use<FiltersBsViewModel>(LoadingTrait) {
        CitiesContent(
            CitiesState(
                selected, cities, search,
                searchState,alpha
            ), Modifier, object: CitiesCallback {
                
                override fun onSelectCity(city: CityModel) {
                    scope.launch {
                        vm.changeCity(city)
                        vm.navigate(0)
                    }
                }
                
                override fun onSearchStateChange(state: Boolean) {
                    scope.launch { vm.changeSearchCountryState(state) }
                }
                
                override fun onSearchChange(query: String) {
                    scope.launch { vm.changeSearchQuery(query) }
                }
            }
        )
    }
}