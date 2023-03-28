package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.auth.login.*
import ru.rikmasters.gilty.feature.login.R
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.shared.R as SR

private val loginMethods = listOf(
    Google("", ""),
    Apple("", ""),
    Vk("", ""),
)

@Preview
@Composable
private fun LoginPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            LoginContent(
                LoginState(
                    "79543422455",
                    false,
                    DemoCountry,
                    loginMethods
                )
            )
        }
    }
}

@Preview
@Composable
private fun ExternalPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            LoginContent(
                LoginState(
                    "79543422455",
                    false,
                    DemoCountry,
                    loginMethods,
                    true,
                    "Google"
                )
            )
        }
    }
}

interface LoginCallback {
    
    fun onBack()
    fun onNext()
    fun loginWith(method: LoginMethod) {}
    fun privatePolicy() {}
    fun termsOfApp() {}
    fun onPhoneChange(text: String)
    fun onClear()
    fun changeCountry()
}

@Immutable
data class LoginState(
    val phone: String,
    val isNextActive: Boolean,
    val country: Country,
    val methods: List<LoginMethod>,
    val externalScreen: Boolean = false,
    val method: String? = null,
)

@Composable
fun LoginContent(
    state: LoginState,
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null,
) {
    val external = if(state.externalScreen)
        null else "external"
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorScheme.background),
        SpaceBetween
    ) {
        Column(
            Modifier.weight(1f),
            Top, CenterHorizontally,
        ) {
            external?.let { Logo(Modifier.weight(1f)) }
            
            if(external.isNullOrBlank())
                TopBar(state.method) { callback?.onBack() }
            
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 36.dp)) {
                PhoneField(Modifier, state.phone, state.country, callback)
                NextButton(Modifier, state.isNextActive) { callback?.onNext() }
            }
            
            external?.let { LoginMethodsButtons(Modifier, state.methods, callback) }
        }
        external?.let {
            Spacer(Modifier.weight(0.1f))
            ConfirmationPolicy(
                Modifier.fillMaxWidth(),
                callback
            )
            Spacer(Modifier.weight(0.1f))
        }
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier
) {
    Image(
        painterResource(SR.drawable.ic_logo),
        stringResource(SR.string.gilty_logo),
        modifier
            .heightIn(64.dp, 210.dp)
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun TopBar(
    method: String?,
    onBack: (() -> Unit)? = null
) {
    ActionBar(
        stringResource(R.string.login_external_bar),
        Modifier, stringResource(
            R.string.login_external_bar_details,
            (method ?: "")
        )
    ) { onBack?.let { it() } }
}

@Composable
private fun LoginMethodsButtons(
    modifier: Modifier = Modifier,
    methods: List<LoginMethod>,
    callback: LoginCallback? = null
) {
    Box(
        modifier
            .height(290.dp)
            .padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(
            methods.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                Modifier, Top, CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.alternative_separator),
                    Modifier.padding(top = 20.dp),
                    style = typography.labelSmall,
                    color = colorScheme.onTertiary
                )
                Spacer(Modifier.height(8.dp))
                methods.forEach {
                    LoginMethodButton(it) { method ->
                        callback?.loginWith(method)
                    }
                }
            }
        }
    }
}

private val LoginMethod.icon
    @Composable get() = painterResource(
        when(this) {
            is Apple -> R.drawable.ic_apple
            is Google -> R.drawable.ic_google
            is Vk -> R.drawable.ic_vk
        }
    )

@Composable
private fun LoginMethodButton(
    method: LoginMethod,
    modifier: Modifier = Modifier,
    onClick: (LoginMethod) -> Unit
) {
    Button(
        { onClick(method) },
        modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .padding(horizontal = 16.dp),
        colors = buttonColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(3f / 4f),
            verticalAlignment = CenterVertically
        ) {
            Image(
                method.icon,
                null,
                Modifier
                    .padding(end = 18.dp)
                    .padding(vertical = 10.dp),
                colorFilter = if(method.name != "Apple")
                    null else tint(colorScheme.tertiary)
            )
            Text(
                stringResource(R.string.login_via, method.name),
                style = typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun ConfirmationPolicy(
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null
) {
    val textStyle = SpanStyle(ThemeExtra.colors.policyAgreeColor)
    val linkStyle = SpanStyle(
        colorScheme.tertiary,
        textDecoration = Underline
    )
    val typography = typography.labelSmall.copy(fontWeight = SemiBold)
    val annotatedText = buildAnnotatedString {
        val terms = stringResource(R.string.terms)
        val policy = stringResource(R.string.privacy_police)
        val text = stringResource(
            R.string.privacy_police_agree,
            terms, policy
        )
        withStyle(textStyle) {
            append(text)
        }
        text.indexOf(terms).let {
            addStyle(linkStyle, it, it + terms.length)
            addStringAnnotation("terms", "", it, it + terms.length)
        }
        text.indexOf(policy).let {
            addStyle(linkStyle, it, it + policy.length)
            addStringAnnotation("policy", "", it, it + policy.length)
        }
    }
    ClickableText(annotatedText, modifier.padding(horizontal = 16.dp), typography) {
        annotatedText.getStringAnnotations(
            "terms", it, it
        ).firstOrNull()?.let { callback?.termsOfApp() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.privatePolicy() }
    }
}
