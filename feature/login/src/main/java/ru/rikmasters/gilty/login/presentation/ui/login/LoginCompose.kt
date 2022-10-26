package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.login.presentation.model.CountryList
import ru.rikmasters.gilty.login.presentation.model.DemoCountry
import ru.rikmasters.gilty.login.presentation.ui.PhoneTextField
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.SearchActionBar
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun LoginPreview() {
    GiltyTheme {
        LoginContent(
            LoginState(
                rememberCoroutineScope(), "9543422455", DemoCountry
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
    fun onCountryChange(country: Country) {}
}

data class LoginState(
    val scope: CoroutineScope,
    val phone: String,
    val country: Country
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
                        .background(ThemeExtra.colors.cardBackground),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val asm = get<AppStateModel>()
                    Image(
                        painterResource(state.country.flag),
                        stringResource(R.string.login_select_country),
                        Modifier
                            .padding(start = 10.dp)
                            .size(20.dp)
                            .clickable
                            {
                                state.scope.launch {
                                    asm.bottomSheetState.expand {
                                        CountryBottomSheetContent {
                                            callback?.onCountryChange(it)
                                            state.scope.launch { asm.bottomSheetState.collapse() }
                                        }
                                    }
                                }
                            }
                    )
                    PhoneTextField(
                        state.phone,
                        state.country.code,
                        Modifier.fillMaxWidth(),
                        onClear = { callback?.onPhoneChange("") },
                        onValueChanged = { callback?.onPhoneChange(it) })
                }
            }
            Button(
                { callback?.onNext() },
                Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                true,
                MaterialTheme.shapes.large,
                ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primary,
                    disabledContainerColor = ThemeExtra.colors.notActive
                ),
                contentPadding = PaddingValues(vertical = 18.dp)
            ) {
                Text(
                    stringResource(R.string.next_button),
                    style = ThemeExtra.typography.button
                )
            }
            Text(
                stringResource(R.string.login_alternative_separator),
                Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.labelSmall,
                color = ThemeExtra.colors.secondaryTextColor
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
                    color = ThemeExtra.colors.transparentBtnTextColor
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
        ThemeExtra.colors.mainTextColor,
        textDecoration = TextDecoration.Underline
    )
    val annotatedText = buildAnnotatedString {
        withStyle(textStyle) { append(stringResource(R.string.login_privacy_police_agree)) }
        pushStringAnnotation("terms", "")
        withStyle(linkStyle) { append(stringResource(R.string.login_terms)) }; pop()
        withStyle(textStyle) { append(stringResource(R.string.login_connector_terms_and_privacy_police)) }
        pushStringAnnotation("policy", "")
        withStyle(linkStyle) { append(stringResource(R.string.login_privacy_police)) }; pop()
        withStyle(textStyle) { append(stringResource(R.string.login_application)) }
    }
    ClickableText(annotatedText, modifier, ThemeExtra.typography.SubHeadSb) {
        annotatedText.getStringAnnotations(
            "terms", it, it
        ).firstOrNull()?.let { callback?.privatePolicy() }
        annotatedText.getStringAnnotations(
            "policy", it, it
        ).firstOrNull()?.let { callback?.termsOfApp() }
    }
}

@Composable
private fun CountryBottomSheetContent(onSelect: (country: Country) -> Unit) {
    var searchState by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp, 20.dp)) {
        SearchActionBar(SearchState(
            stringResource(R.string.login_search_name),
            searchState, searchText, { searchText = it },
            { searchState = it }
        ))
        LazyColumn(
            Modifier
                .padding(top = 10.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(ThemeExtra.colors.cardBackground)
        ) {
            itemsIndexed(CountryList) { index, item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onSelect(item) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(item.flag),
                        item.country,
                        Modifier.size(24.dp)
                    )
                    Text(
                        item.country,
                        Modifier.padding(start = 16.dp),
                        style = ThemeExtra.typography.Body1Medium
                    )
                }
                if (index < CountryList.size - 1) Divider(Modifier.padding(start = 54.dp))
            }
        }
    }
}
