package ru.rikmasters.gilty.login.presentation.ui.login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.util.composable.getActivity
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.core.web.openInWeb
import ru.rikmasters.gilty.login.presentation.ui.login.country.CountryBs
import ru.rikmasters.gilty.login.viewmodel.LoginViewModel
import ru.rikmasters.gilty.login.viewmodel.bottoms.CountryBsViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(vm: LoginViewModel) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
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
    val back = colorScheme.background
    LaunchedEffect(activity.intent) {
        asm.systemUi.setNavigationBarColor(back)
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
            openInWeb(context, "https://www.google.ru")
        }
        
        override fun termsOfApp() {
            openInWeb(context, "https://www.google.ru")
        }
        
        override fun changeCountry() {
            vm.scope.openBS<CountryBsViewModel>(scope){
                CountryBs(it)
            }
        }
        
        override fun onNext() {
            scope.launch {
                vm.sendCode()?.let {
                    Toast.makeText(
                        context, it,
                        Toast.LENGTH_SHORT
                    ).show()
                } ?: vm.getSendCode()?.let {
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