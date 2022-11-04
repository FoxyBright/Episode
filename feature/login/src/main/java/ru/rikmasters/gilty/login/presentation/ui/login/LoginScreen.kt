package ru.rikmasters.gilty.login.presentation.ui.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.presentation.model.Countries
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.login.presentation.model.DemoCountry
import ru.rikmasters.gilty.shared.R


@Composable
fun LoginScreen(nav: NavState = get()) {
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var selectCountry by remember { mutableStateOf(DemoCountry) }
    val scope = rememberCoroutineScope()
    val mask = "${selectCountry.code} ### ###-##-##"
    val transform by remember(mask)
    { mutableStateOf(phoneTransform(mask)) }
    var searchText by remember { mutableStateOf("") }
    var searchState by remember { mutableStateOf(false) }
    val countries = Countries()
    LoginContent(LoginState(
        transform, mask, phone,
        selectCountry, Countries()
    ),
        Modifier, object : LoginCallback {
            override fun onPhoneChange(text: String) {
                phone = text
            }

            override fun googleLogin() {
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
                    asm.bottomSheetState.expand {
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
                                        asm.bottomSheetState.collapse()
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