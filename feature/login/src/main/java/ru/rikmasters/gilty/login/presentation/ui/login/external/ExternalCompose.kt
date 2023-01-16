package ru.rikmasters.gilty.login.presentation.ui.login.external

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.login.presentation.ui.login.LoginCallback
import ru.rikmasters.gilty.login.presentation.ui.login.NextButton
import ru.rikmasters.gilty.login.presentation.ui.login.PhoneField
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ExternalLoginPreview() {
    GiltyTheme {
        ExternalContent(
            ExternalState(
                "Google",
                "9534531315",
                DemoCountry, (true)
            )
        )
    }
}

data class ExternalState(
    val loginMethod: String,
    val phone: String,
    val country: Country,
    val isNextActive: Boolean,
)

interface ExternalCallback: LoginCallback {
    
    fun onBack()
}

@Composable
fun ExternalContent(
    state: ExternalState,
    modifier: Modifier = Modifier,
    callback: ExternalCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        ActionBar(
            stringResource(R.string.login_external_bar),
            stringResource(
                R.string.login_external_bar_details,
                state.loginMethod
            )
        ) { callback?.onBack() }
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 36.dp)
        ) {
            PhoneField(
                Modifier,
                state.phone,
                state.country,
                callback
            )
            NextButton(
                Modifier,
                state.isNextActive
            ) { callback?.onNext() }
        }
    }
}