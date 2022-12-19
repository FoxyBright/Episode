package ru.rikmasters.gilty.login.presentation.ui.code

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.*
import ru.rikmasters.gilty.shared.common.BackBlur
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(showBackground = true)
@Composable
private fun CodePreview() {
    GiltyTheme {
        CodeContent(
            CodeState(
                "1234",
                listOf(), 60, false
            )
        )
    }
}

data class CodeState(
    val code: String,
    val focuses: List<FocusRequester>,
    val sec: Int,
    val blur: Boolean
)

interface CodeCallback : NavigationInterface {
    fun onCodeChange(index: Int, it: String)
    fun onCodeSend() {}
    fun onBlur() {}
}

@Composable
fun CodeContent(
    state: CodeState,
    modifier: Modifier = Modifier,
    callback: CodeCallback? = null
) {
    Box(modifier.background(colorScheme.background)) {
        Column(Modifier.fillMaxSize(), Top, CenterHorizontally) {
            ActionBar(
                stringResource(R.string.confirm_number_title),
                stringResource(R.string.confirm_number_subtitle)
            ) { callback?.onBack() }
            DigitCode(Modifier.padding(5.dp), state.code, state.focuses)
            { index, it -> callback?.onCodeChange(index, it) }
            ButtonTimer(Modifier.padding(top = 20.dp), state.sec)
            { callback?.onCodeSend() }
        }
    }
    if (state.blur)
        BackBlur(
            Modifier
                .fillMaxSize()
                .clickable { callback?.onBlur() }
        ) {
            Column(
                Modifier.align(Center),
                horizontalAlignment = CenterHorizontally
            ) {
                Image(
                    painterResource(R.drawable.ic_bad),
                    null, Modifier.size(40.dp)
                )
                Text(
                    stringResource(R.string.code_is_bad_code_notification),
                    Modifier.padding(top = 16.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium,
                    fontWeight = SemiBold
                )
            }
        }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DigitCode(
    modifier: Modifier = Modifier,
    code: String,
    focuses: List<FocusRequester>,
    onChange: (Int, String) -> Unit
) {
    Row {
        focuses.forEachIndexed { index, focus ->
            TextField(
                if (code.length > index)
                    code[index].toString() else "",
                { onChange(index, it) }, modifier
                    .padding(10.dp)
                    .clip(shapes.large)
                    .size(60.dp)
                    .focusRequester(focus),
                keyboardOptions =
                KeyboardOptions(keyboardType = Number),
                textStyle = ThemeExtra.typography.CodeText,
                colors = TextFieldColors(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun ButtonTimer(
    modifier: Modifier = Modifier,
    sec: Int,
    onResend: () -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { if (sec <= 0) onResend() },
        Center
    ) {
        Text(
            (if (sec > 0) "${stringResource(R.string.call_again)} \n$sec сек"
            else stringResource(R.string.call_again)),
            Modifier.padding(6.dp),
            if (sec > 0) colorScheme.primary
            else colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = SemiBold,
            textAlign = TextAlign.Center
        )
    }
}