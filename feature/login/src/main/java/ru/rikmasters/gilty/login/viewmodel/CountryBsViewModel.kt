package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.CountryManager

@OptIn(FlowPreview::class)
class CountryBsViewModel(
    
    private val countryManager: CountryManager

): ViewModel() {
    
    private val loginVm: LoginViewModel by inject()
    
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    
    suspend fun changeQuery(query: String) {
        _query.emit(query)
    }
    
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries = _countries
        .combine(loginVm.country) { list, current -> // Изменение порядка
            val default = countryManager.defaultCountry
            (if(default == current) listOf(default) else listOf(default, current)) +
                    (list - default - current)
        }
        .combine(query.debounce(250)) { list, str -> // Поиск
            list.filter {
                it.name.contains(str, true)
                        || it.code.contains(str, true)
                        || it.phoneDial.contains(str, true)
                        || it.clearPhoneDial.contains(str, true)
            }
        }.state(_countries.value)
    
    suspend fun loadCountries() = singleLoading {
        _countries.emit(
            countryManager.getCountries()
        )
    }
    
    suspend fun select(country: Country) {
        loginVm.selectCountry(country)
    }
    
}