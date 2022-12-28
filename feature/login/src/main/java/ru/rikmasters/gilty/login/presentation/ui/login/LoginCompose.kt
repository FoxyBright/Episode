package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun LoginPreview() {
    GiltyTheme {
        LoginContent(
            LoginState(
                "79543422455",
                false,
                DemoCountry,
                listOf(
                    Google(""),
                    Apple(""),
                    Vk(""),
                )
            )
        )
    }
}

interface LoginCallback {
    fun onNext()
    fun loginWith(method: LoginMethod)
    fun privatePolicy()
    fun termsOfApp()
    fun onPhoneChange(text: String)
    fun onClear()
    fun changeCountry()
}

@Immutable
data class LoginState(
    val phone: String,
    val isNextActive: Boolean,
    val country: Country,
    val methods: List<LoginMethod>
)

@Composable
fun LoginContent(
    state: LoginState,
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null,
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorScheme.background),
        verticalArrangement = SpaceBetween
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = CenterHorizontally,
        ) {
            Logo(Modifier.weight(1f))
            PhoneField(Modifier, state, callback)
            NextButton(Modifier, state.isNextActive) { callback?.onNext() }
            LoginMethodsButtons(Modifier, state.methods, callback)
        }
        Spacer(Modifier.weight(0.1f))
        ConfirmationPolicy(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            callback
        )
        Spacer(Modifier.weight(0.1f))
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier
) {
    Image(
        painterResource(R.drawable.ic_logo),
        stringResource(R.string.gilty_logo),
        modifier.heightIn(64.dp, 210.dp)
    )
}

@Composable
private fun PhoneField(
    modifier: Modifier = Modifier,
    state: LoginState,
    callback: LoginCallback? = null
) {
    Box(
        modifier.clip(MaterialTheme.shapes.extraSmall)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.onPrimaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                state.country.flag,
                stringResource(R.string.login_select_country),
                Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
                    .clickable
                    { callback?.changeCountry() }
            )
            PhoneTextField(
                state.phone,
                state.country,
                Modifier.fillMaxWidth(),
                { callback?.onClear() },
                { callback?.onPhoneChange(it) }
            )
        }
    }
}

@Composable
private fun NextButton(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onClick: () -> Unit
) {
    GradientButton(
        modifier.padding(top = 32.dp),
        stringResource(R.string.next_button),
        isActive,
        onClick = onClick
    )
}

@Composable
private fun LoginMethodsButtons(
    modifier: Modifier = Modifier,
    methods: List<LoginMethod>,
    callback: LoginCallback? = null
) {
    Box(modifier.height(290.dp)) {
        AnimatedVisibility(
            methods.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                Modifier, Top, CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.login_alternative_separator),
                    Modifier.padding(top = 20.dp),
                    style = typography.labelSmall,
                    color = colorScheme.onTertiary
                )
                Spacer(Modifier.height(8.dp))
                methods.forEach {
                    LoginMethodButton(it) {
                        callback?.loginWith(it)
                    }
                }
            }
        }
    }
}


private val LoginMethod.name
@Composable get() = when(this) {
    is Apple -> stringResource(R.string.login_via_apple)
    is Google -> stringResource(R.string.login_via_google)
    is Vk -> stringResource(R.string.login_via_vk)
}

private val LoginMethod.icon
    @Composable get() = when(this) {
        is Apple -> painterResource(R.drawable.ic_apple)
        is Google -> painterResource(R.drawable.ic_google)
        is Vk -> painterResource(R.drawable.ic_vk)
    }

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
            .padding(top = 12.dp),
        colors = ButtonDefaults.buttonColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(3f / 4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                method.icon,
                null,
                Modifier
                    .padding(end = 18.dp)
                    .padding(vertical = 15.dp)
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
        ).firstOrNull()?.let { callback?.termsOfApp() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.privatePolicy() }
    }
}
