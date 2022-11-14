package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.login.presentation.model.Countries
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.login.presentation.model.DemoCountry
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun LoginPreview() {
    GiltyTheme {
        val mask = "+7 ### ###-##-##"
        LoginContent(
            LoginState(
                textMask(mask), mask,
                "9543422455", DemoCountry,
                Countries(), 10
            )
        )
    }
}

interface LoginCallback {
    fun onNext() {}
    fun googleLogin() {}
    fun privatePolicy() {}
    fun termsOfApp() {}
    fun onPhoneChange(text: String) {}
    fun onClear() {}
    fun openCountryBottomSheet() {}
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
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(R.drawable.ic_logo),
                stringResource(R.string.gilty_logo),
                Modifier.padding(top = 132.dp)
            )
            Box(
                Modifier
                    .padding(top = 80.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onPrimaryContainer),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(state.country.flag),
                        stringResource(R.string.login_select_country),
                        Modifier
                            .padding(start = 10.dp)
                            .size(20.dp)
                            .clickable
                            { callback?.openCountryBottomSheet() }
                    )
                    PhoneTextField(
                        state.phone, state.transform,
                        Modifier.fillMaxWidth(), state.size,
                        { callback?.onClear() },
                        { callback?.onPhoneChange(it) })
                }
            }
            GradientButton(
                Modifier.padding(top = 32.dp),
                stringResource(R.string.next_button),
                (state.phone.length > 9),
            ) { callback?.onNext() }
            Text(
                stringResource(R.string.login_alternative_separator),
                Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Button(
                { callback?.googleLogin() },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Image(
                    painterResource(R.drawable.ic_google),
                    stringResource(R.string.google_login),
                    Modifier.padding(end = 8.dp)
                )
                Text(
                    stringResource(R.string.google_login),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        ConfirmationPolicy(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 60.dp, bottom = 60.dp),
            callback
        )
    }
}

@Composable
private fun ConfirmationPolicy(modifier: Modifier = Modifier, callback: LoginCallback? = null) {
    val textStyle = SpanStyle(ThemeExtra.colors.policyAgreeColor)
    val linkStyle = SpanStyle(
        MaterialTheme.colorScheme.tertiary,
        textDecoration = TextDecoration.Underline
    )
    val typography = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
    val annotatedText = buildAnnotatedString {
        withStyle(textStyle) { append(stringResource(R.string.login_privacy_police_agree)) }
        pushStringAnnotation("terms", "")
        withStyle(linkStyle) { append(stringResource(R.string.login_terms)) }; pop()
        withStyle(textStyle) { append(stringResource(R.string.login_connector_terms_and_privacy_police)) }
        pushStringAnnotation("policy", "")
        withStyle(linkStyle) { append(stringResource(R.string.login_privacy_police)) }; pop()
        withStyle(textStyle) { append(stringResource(R.string.login_application)) }
    }
    ClickableText(annotatedText, modifier, typography) {
        annotatedText.getStringAnnotations(
            "terms", it, it
        ).firstOrNull()?.let { callback?.termsOfApp() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.privatePolicy() }
    }
}
