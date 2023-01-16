package ru.rikmasters.gilty.login.presentation.ui.login.external

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.login.presentation.ui.login.country.CountryBs
import ru.rikmasters.gilty.login.viewmodel.CountryBsViewModel
import ru.rikmasters.gilty.login.viewmodel.LoginViewModel

@Composable
fun ExternalScreen(
    vm: LoginViewModel,
    loginMethod: String,
    token: String
) {
   
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val phone by vm.phone.collectAsState()
    val country by vm.country.collectAsState()
    
    val isNextActive = remember(phone, country) {
        val targetLength =
            country.phoneMask.count { it == '#' } +
                    country.clearPhoneDial.length
        phone.length == targetLength
    }
    
    ExternalContent(
        ExternalState(
            loginMethod, phone,
            country, isNextActive
        ), Modifier, object: ExternalCallback {
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    vm.sendCode()
                    nav.navigate("code")
                }
            }
            
            override fun changeCountry() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<CountryBsViewModel>(vm.scope) {
                            CountryBs(it)
                        }
                    }
                }
            }
            
            override fun onPhoneChange(text: String) {
                scope.launch { vm.changePhone(text) }
            }
            
            override fun onClear() {
                scope.launch { vm.clearPhone() }
            }
        }
    )
}