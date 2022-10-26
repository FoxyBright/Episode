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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.login.presentation.model.CountryList
import ru.rikmasters.gilty.login.presentation.model.DemoCountry
import ru.rikmasters.gilty.login.presentation.ui.PhoneTextField
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.BottomSheetCompose
import ru.rikmasters.gilty.shared.shared.BottomSheetComposeState
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.SearchState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun LoginPreview() {
    GiltyTheme {
        LoginContent(Modifier)
    }
}

interface LoginCallback {
    fun onNext() {}
    fun googleLogin() {}
    fun privatePolicy() {}
    fun termsOfApp() {}
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    callback: LoginCallback? = null
) {
    val bottomSheetState = remember { mutableStateOf(false) }
    val searchCountry = remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val country = remember { mutableStateOf(DemoCountry) }
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
                    Image(
                        painterResource(country.value.flag),
                        stringResource(R.string.login_select_country),
                        Modifier
                            .padding(start = 10.dp)
                            .size(20.dp)
                            .clickable
                            { bottomSheetState.value = !bottomSheetState.value }
                    )
                    PhoneTextField(
                        phone,
                        country.value.code,
                        Modifier.fillMaxWidth(),
                        onClear = { phone = "" },
                        onValueChanged = { phone = it })
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
        val searchState = remember { mutableStateOf(false) }
        BottomSheetCompose(
            BottomSheetComposeState(
                600.dp,
                bottomSheetState,
                SearchState(
                    stringResource(R.string.login_search_name),
                    searchState.value, searchCountry.value,
                    onChangeText = { searchCountry.value = it },
                    onExpandSearch = { searchState.value = it }
                ))
            {
                CountryBottomSheetContent {
                    country.value = it
                    bottomSheetState.value = !bottomSheetState.value
                }
            },
            Modifier
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .align(Alignment.BottomCenter)
                .background(ThemeExtra.colors.cardBackground)
        ) { bottomSheetState.value = false }
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
    LazyColumn(
        Modifier
            .padding(horizontal = 16.dp)
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
