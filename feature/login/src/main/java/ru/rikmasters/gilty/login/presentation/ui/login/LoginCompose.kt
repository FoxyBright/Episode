package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.model.login.Countries
import ru.rikmasters.gilty.shared.model.login.Country
import ru.rikmasters.gilty.shared.model.login.DemoCountry
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun LoginPreview() {
    GiltyTheme {
        val mask = "+7 ### ###-##-##"
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            LoginContent(
                LoginState(
                    textMask(mask), mask,
                    ("9543422455"), DemoCountry,
                    Countries(), (10)
                )
            )
        }
    }
}

interface LoginCallback {
    
    fun onNext() {}
    fun onGoogleLogin() {}
    fun onAppleLogin() {}
    fun onVkLogin() {}
    fun onPrivatePolicyClick() {}
    fun onTermsOfAppClick() {}
    fun onPhoneChange(text: String) {}
    fun onClear() {}
    fun onCountrySelector() {} 
    fun onKeyboardDone() {}
}

data class LoginState(
    val transform: VisualTransformation,
    val mask: String,
    val phone: String,
    val country: Country,
    val allCountries: List<Country>,
    val size: Int
)

@Composable
fun LoginContent(
    state: LoginState,
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(colorScheme.background),
        Center
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            Top, CenterHorizontally,
        ) {
            Image(
                painterResource(R.drawable.ic_logo),
                stringResource(R.string.gilty_logo),
                Modifier.padding(bottom = 90.dp)
            )
            Box(Modifier.clip(shapes.extraSmall)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(colorScheme.onPrimaryContainer),
                    Start, CenterVertically
                ) {
                    Image(
                        painterResource(state.country.flag),
                        stringResource(R.string.login_select_country),
                        Modifier
                            .padding(start = 12.dp)
                            .padding(vertical = 20.dp)
                            .size(20.dp)
                            .clickable
                            { callback?.onCountrySelector() }
                    ); PhoneTextField(
                    state.phone, state.transform,
                    Modifier.fillMaxWidth(), state.size,
                    { callback?.onClear() },
                    { callback?.onPhoneChange(it) })
                { callback?.onKeyboardDone() }
                }
            }; Buttons(Modifier.padding(top = 20.dp), callback)
        }
        ConfirmationPolicy(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .padding(horizontal = 16.dp),
            callback
        )
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null,
    active: Boolean = true
) {
    Column(modifier, Top, CenterHorizontally) {
        GradientButton(
            Modifier, stringResource(R.string.next_button), active,
        ) { callback?.onNext() }
        Text(
            stringResource(R.string.login_alternative_separator),
            Modifier.padding(vertical = 20.dp),
            style = typography.labelSmall,
            color = colorScheme.onTertiary
        )
        AlternativeButton(
            stringResource(R.string.google_login),
            R.drawable.ic_google,
        ) { callback?.onGoogleLogin() }
        AlternativeButton(
            stringResource(R.string.apple_login),
            R.drawable.ic_apple,
            Modifier.padding(vertical = 12.dp),
            ColorFilter.tint(colorScheme.tertiary)
        ) { callback?.onAppleLogin() }
        AlternativeButton(
            stringResource(R.string.vk_login),
            R.drawable.ic_vk,
        ) { callback?.onVkLogin() }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AlternativeButton(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier,
    filter: ColorFilter? = null,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier, (true), CircleShape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Box(Modifier.fillMaxWidth(), Center) {
            Row(
                Modifier
                    .width(250.dp)
                    .padding(16.dp),
                Start,
                CenterVertically
            ) {
                Image(
                    painterResource(icon),
                    (null), Modifier.size(24.dp),
                    colorFilter = filter
                )
                Text(
                    text, Modifier.padding(start = 20.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium,
                    fontWeight = Bold
                )
            }
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
        withStyle(textStyle) { append(stringResource(R.string.login_privacy_police_agree)) }
        pushStringAnnotation("terms", "")
        withStyle(linkStyle) { append(stringResource(R.string.login_terms)) }
        pop(); withStyle(textStyle) { append(stringResource(R.string.login_connector_terms_and_privacy_police)) }
        pushStringAnnotation("policy", "")
        withStyle(linkStyle) { append("\n" + stringResource(R.string.login_privacy_police)) }
        pop(); withStyle(textStyle) { append(stringResource(R.string.login_application)) }
    }
    ClickableText(annotatedText, modifier, typography) {
        annotatedText.getStringAnnotations(
            "terms", it, it
        ).firstOrNull()?.let { callback?.onTermsOfAppClick() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.onPrivatePolicyClick() }
    }
}
