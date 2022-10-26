package ru.rikmasters.gilty.login.presentation.ui.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.*
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
                Array(4)
                { FocusRequester() }
            )
        )
    }
}

data class CodeState(
    val code: String,
    val focuses: Array<FocusRequester>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CodeState
        if (!focuses.contentEquals(other.focuses)) return false
        return true
    }

    override fun hashCode(): Int {
        return focuses.contentHashCode()
    }
}

interface CodeCallback : NavigationInterface {
    fun onCodeChange(index: Int, it: String)
}

@Composable
fun CodeContent(
    state: CodeState,
    modifier: Modifier = Modifier,
    callback: CodeCallback? = null
) {
    Surface(modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ActionBar(
                stringResource(R.string.confirm_number_title),
                stringResource(R.string.confirm_number_subtitle)
            ) { callback?.onBack() }
            DigitCode(Modifier.padding(5.dp), state.code, state.focuses)
            { index, it -> callback?.onCodeChange(index, it) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DigitCode(
    modifier: Modifier = Modifier,
    code: String,
    focuses: Array<FocusRequester>,
    onChange: (Int, String) -> Unit
) {
    Row {
        focuses.forEachIndexed { index, focus ->
            TextField(
                if (code.length > index) code[index].toString() else "",
                { onChange(index, it) },
                modifier
                    .padding(10.dp)
                    .clip(MaterialTheme.shapes.large)
                    .size(60.dp)
                    .focusRequester(focus),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = ThemeExtra.typography.CodeText,
                colors = TextFieldColors(),
                singleLine = true
            )
        }
    }
}

//@Composable
//private fun ButtonTimer(
//    modifier: Modifier = Modifier,
//    //timer: Observable<Int>,
//    onResend: () -> Unit,
//) {
//    // TODO таймер на повторную отправку кода
//    val sec = 0 //by timer.subscribeAsState(180)
//    Button(
//        onResend,
//        modifier
//            .fillMaxWidth(),
//        sec <= 0,
//        MaterialTheme.shapes.large,
//        ButtonDefaults.buttonColors(Color.Transparent),
//        contentPadding = PaddingValues(vertical = 18.dp)
//    ) {
//        Text(
//            (if (sec > 0)
//                stringResource(R.string.call_again, "${sec / 60}:${sec % 60}")
//            else
//                stringResource(R.string.call_again)
//                    ).uppercase(),
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.primary
//        )
//    }
//}