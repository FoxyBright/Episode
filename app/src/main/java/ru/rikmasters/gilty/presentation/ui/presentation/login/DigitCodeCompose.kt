package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

@Preview
@Composable
private fun DigitCodePreview() {
    GiltyTheme {
        val code by remember { mutableStateOf("123") }
        DigitCode(
            Modifier.padding(32.dp), 4, code
        )
    }
}

@Composable
fun DigitCode(
    modifier: Modifier = Modifier,
    length: Int,
    enteredCode: String,
    onCodeComplete: ((String) -> Unit)? = null
) {
    var code by remember { mutableStateOf(enteredCode) }
    val requesters = remember {
        Array(length) { FocusRequester() }
    }
    val onChange = { i: Int, char: String ->
        if (char.length == length) {
            code = char
        } else if (char.length < 2) {
            if (char == "") {
                code = code.substring(0, code.lastIndex)
                if (i - 1 >= 0)
                    requesters[i - 1].requestFocus()
            } else {
                code += char
                if (i + 1 < length)
                    requesters[i + 1].requestFocus()
            }
        }
        if (code.length == length && onCodeComplete != null)
            onCodeComplete(code)
    }
    Row(
        modifier
            .fillMaxWidth(),
        Arrangement.SpaceBetween
    ) {
        requesters.forEachIndexed { i, it ->
            Digit(
                if (code.length > i) code[i].toString() else "",
                Modifier, it
            ) { onChange(i, it) }
        }
    }
}

@Composable
private fun Digit(
    value: String,
    modifier: Modifier = Modifier,
    requester: FocusRequester,
    onChange: (String) -> Unit
) {
    BasicTextField(
        value, onChange,
        modifier
            .size(57.dp, 60.dp)
            .background(
                Color.White,
                RoundedCornerShape(14.dp)
            )
            .focusRequester(requester)
            .padding(20.dp, 12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        textStyle = MaterialTheme.typography.titleLarge,
        singleLine = true
    )
}