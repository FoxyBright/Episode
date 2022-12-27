package ru.rikmasters.gilty.login.presentation.ui.login

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.LoginViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.model.login.Countries
import ru.rikmasters.gilty.shared.model.login.Country
import ru.rikmasters.gilty.shared.model.login.DemoCountry


@Composable
fun LoginScreen(vm: LoginViewModel) {
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var selectCountry by remember { mutableStateOf(DemoCountry) }
    val scope = rememberCoroutineScope()
    val mask = "${selectCountry.code} ### ###-##-##"
    val transform by remember(mask)
    { mutableStateOf(textMask(mask)) }
    var searchText by remember { mutableStateOf("") }
    var searchState by remember { mutableStateOf(false) }
    val countries = Countries()
    LaunchedEffect(Unit) {
        vm.loadLoginMethods()
    }
    val methods by vm.loginMethods.collectAsState()
    LoginContent(LoginState(
        transform, mask, phone,
        selectCountry, Countries(),
        mask.count { it == '#' },
        methods.toList()
    ),
        Modifier, object : LoginCallback {
            override fun onPhoneChange(text: String) {
                phone = text
            }

            override fun onClear() {
                phone = ""
            }

            override fun loginWith(method: LoginMethod) {
                val toast = context.resources.getString(R.string.login_google_toast)
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            }

            override fun privatePolicy() {
                val toast = context.resources.getString(R.string.login_policy_toast)
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            }

            override fun termsOfApp() {
                val toast = context.resources.getString(R.string.login_terms_toast)
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            }

            override fun openCountryBottomSheet() {
                scope.launch {
                    asm.bottomSheet.expand {
                        CountryBottomSheetContent(
                            CountryBottomSheetState(searchText, searchState, countries),
                            Modifier, object : CountryBottomSheetCallBack {
                                override fun onSearchTextChange(text: String) {
                                    searchText = text
                                }

                                override fun onSearchStateChange() {
                                    searchState = !searchState
                                }

                                override fun onCountrySelect(country: Country) {
                                    selectCountry = country
                                    scope.launch {
                                        asm.bottomSheet.collapse()
                                    }
                                }
                            })
                    }
                }
            }

            override fun onNext() {
                nav.navigate("registration/code")
            }
        })
}