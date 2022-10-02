package ru.rikmasters.gilty.presentation.ui.presentation.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme


@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
@Composable
private fun DigitCodePreview() {
    GiltyTheme() {

        DigitCode(
            Modifier.padding(32.dp),
            4,
        ) { Log.d("no_tag", "Code: $it") }
    }
}

@Composable
fun DigitCode(
    modifier: Modifier = Modifier,
    length: Int,
    onCodeComplete: (String) -> Unit
) {

    var code by remember { mutableStateOf("") }

    val requesters = remember {
        Array(length) { FocusRequester() }
    }

    val onChange = { i: Int, char: String ->
        if(char.length == length) {
            code = char
        } else if(char.length < 2) {

            if (char == "") {
                code = code.substring(0, code.lastIndex)
                if(i-1 >= 0)
                    requesters[i-1].requestFocus()
            } else {
                code += char
                if(i+1 < length)
                    requesters[i+1].requestFocus()
            }
        }

        if(code.length == length)
            onCodeComplete(code)
    }

    Row(
        modifier
            .fillMaxWidth(),
        Arrangement.SpaceBetween
    ) {
        requesters.forEachIndexed { i, it ->
            Digit(
                if(code.length > i) code[i].toString() else "",
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
            .padding(vertical = 11.dp, horizontal = 20.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        textStyle = MaterialTheme.typography.titleLarge,
        singleLine = true
    )
}