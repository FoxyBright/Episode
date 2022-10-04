package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
fun LoginPreview() {
    GiltyTheme {
        LoginContent(Modifier)
    }
}

interface LoginCallback {
    fun onNext() {}
    fun googleLogin() {}
//    fun onOps(){}
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    loginCallback: LoginCallback? = null
) {
    Content(modifier) {
        Image(
            painterResource(R.drawable.ic_logo),
            stringResource(R.string.logo),
            Modifier.padding(top = 132.dp)
        )
        var phone by remember { mutableStateOf("") }
        val region by remember { mutableStateOf("+7") }
        PhoneTextField(
            phone,
            region,
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp), { phone = it }
        ) { phone = it }
        Button(
            { loginCallback?.onNext() },
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
            stringResource(R.string.or),
            Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.labelSmall,
            color = ThemeExtra.colors.secondaryTextColor
        )
        Button(
            { loginCallback?.googleLogin() },
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
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
@ReadOnlyComposable
private fun createOpsAnnotatedString() = buildAnnotatedString {
    append(stringResource(R.string.ops_agree))
    append(' ')
    pushStringAnnotation(
        stringResource(R.string.privacy_police),
        stringResource(R.string.privacy_police)
    )
}