package ru.rikmasters.gilty.login.presentation.ui.login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.util.composable.getActivity
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.web.openInWeb
import ru.rikmasters.gilty.login.presentation.ui.login.country.CountryBs
import ru.rikmasters.gilty.login.viewmodel.CountryBsViewModel
import ru.rikmasters.gilty.login.viewmodel.LoginViewModel
import ru.rikmasters.gilty.shared.R


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(vm: LoginViewModel) {
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val phone by vm.phone.collectAsState()
    val country by vm.country.collectAsState()
    val methods by vm.loginMethods.collectAsState()
    val externalLogin by vm.externalLogin.collectAsState()
    val loginMethod by vm.loginMethod.collectAsState()
    
    val isNextActive = remember(phone, country) {
        val targetLength =
            country.phoneMask.count { it == '#' } +
                    country.clearPhoneDial.length
        phone.length == targetLength
    }
    
    val activity = getActivity()
    LaunchedEffect(activity.intent) {
        activity.intent?.data?.let {
            val handle = vm.handle(it)
            if(handle.first)
                activity.intent = null
            if(handle.second)
                nav.navigate("main/meetings")
            else
                vm.changeLoginScreen()
        }
    }
    
    LaunchedEffect(Unit) {
        vm.loadLoginMethods()
    }
    
    val callback = object: LoginCallback {
        
        override fun onBack() {
            scope.launch {
                vm.changeLoginScreen()
                vm.setLoginMethod("")
            }
        }
        
        override fun onPhoneChange(text: String) {
            scope.launch { vm.changePhone(text) }
        }
        
        override fun onClear() {
            scope.launch { vm.clearPhone() }
        }
        
        override fun loginWith(method: LoginMethod) {
            scope.launch {
                openInWeb(context, method.url)
                vm.setLoginMethod(method.name)
            }
        }
        
        override fun privatePolicy() {
            val toast = context.resources.getString(R.string.login_policy_toast)
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
        
        override fun termsOfApp() {
            val toast = context.resources.getString(R.string.login_terms_toast)
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
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
        
        override fun onNext() {
            scope.launch {
                vm.sendCode()
                vm.getSendCode()?.let {
                    nav.navigate("registration/code")
                }
            }
        }
    }
    LoginContent(
        LoginState(
            phone, isNextActive,
            country, methods.toList(),
            externalLogin, loginMethod
        ), Modifier, callback
    )
}