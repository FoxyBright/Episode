package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.auth.login.*
import ru.rikmasters.gilty.feature.login.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
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
            external?.let {
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.3f)
                        .padding(horizontal = 16.dp),
                    TopCenter
                ) {
                    Image(
                        painterResource(SR.drawable.ic_logo),
                        (null), Modifier.padding(top = maxHeight / 2)
                    )
                }
            }
            
            if(external.isNullOrBlank())
                TopBar(state.method) { callback?.onBack() }
            
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 36.dp)
            ) {
                PhoneField(
                    Modifier, state.phone,
                    state.country, callback
                )
                NextButton(
                    Modifier,
                    state.isNextActive
                ) { callback?.onNext() }
            }
            
            external?.let {
                LoginMethodsButtons(
                    Modifier,
                    state.methods,
                    callback
                )
            }
        }
        external?.let {
            ConfirmationPolicy(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                callback
            )
            Spacer(Modifier.weight(0.05f))
        }
    }
}

@Composable
private fun TopBar(
    method: String?,
    onBack: (() -> Unit)? = null,
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
    callback: LoginCallback? = null,
) {
    Box(modifier) {
        AnimatedVisibility(
            methods.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                Modifier, Top,
                CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.alternative_separator),
                    Modifier.padding(vertical = 20.dp),
                    style = typography.labelSmall,
                    color = colorScheme.onTertiary
                )
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
@OptIn(ExperimentalMaterial3Api::class)
private fun LoginMethodButton(
    method: LoginMethod,
    modifier: Modifier = Modifier,
    onClick: (LoginMethod) -> Unit,
) {
    Card(
        onClick = { onClick(method) },
        modifier = modifier
            .padding(bottom = 12.dp)
            .padding(horizontal = 16.dp),
        shape = CircleShape,
        colors = cardColors(
            containerColor = colorScheme
                .primaryContainer,
        )
    ) {
        Box(Modifier.fillMaxWidth(), Center)
        { ExternalLabel(method) }
    }
}

@Composable
private fun ExternalLabel(method: LoginMethod) {
    Row(
        Modifier.fillMaxWidth(0.65f),
        verticalAlignment = CenterVertically
    ) {
        Image(
            method.icon, (null),
            Modifier.padding(end = 20.dp),
            colorFilter = if(method.name != "Apple")
                null else tint(colorScheme.tertiary)
        )
        Text(
            stringResource(R.string.login_via, method.name),
            Modifier.padding(vertical = 17.dp),
            style = typography.bodyMedium.copy(
                colorScheme.tertiary,
                16.dp.toSp(), Bold,
            ),
        )
    }
}

@Composable
private fun ConfirmationPolicy(
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null,
) {
    val textStyle = SpanStyle(colors.policyAgreeColor)
    
    val linkStyle = SpanStyle(
        colorScheme.tertiary,
        textDecoration = Underline
    )
    
    val typography = typography
        .labelSmall
        .copy(fontWeight = SemiBold)
    
    val annotatedText = buildAnnotatedString {
        val terms = stringResource(R.string.terms)
        val policy = stringResource(R.string.privacy_police)
        val text = stringResource(
            R.string.privacy_police_agree,
            terms, policy
        )
        withStyle(textStyle) { append(text) }
        text.indexOf(terms).let {
            addStyle(linkStyle, it, it + terms.length)
            addStringAnnotation(
                tag = "terms",
                annotation = "",
                start = it,
                end = it + terms.length
            )
        }
        text.indexOf(policy).let {
            addStyle(
                linkStyle,
                start = it,
                end = it + policy.length
            )
            addStringAnnotation(
                tag = "policy",
                annotation = "",
                start = it,
                end = it + policy.length
            )
        }
    }
    ClickableText(
        annotatedText,
        modifier.padding(horizontal = 16.dp),
        typography
    ) {
        annotatedText.getStringAnnotations(
            "terms", it, it
        ).firstOrNull()?.let { callback?.termsOfApp() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.privatePolicy() }
    }
}
