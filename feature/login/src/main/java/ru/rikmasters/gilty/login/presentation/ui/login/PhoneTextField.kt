package ru.rikmasters.gilty.login.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        PhoneTextField(
            "9105152312",
            "+7",
            Modifier.padding(32.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    region: String,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    onValueChanged: ((String) -> Unit)? = null
) {
    val phoneMask = "$region ### ###-##-##"
    val transformation by remember{ mutableStateOf(visualTransformationOf(phoneMask)) }
    TextField(
        value,
        { text ->
            if (text.length <= phoneMask.count { it == '#' } && onValueChanged != null)
                onValueChanged(text)
        },
        modifier,
        shape = MaterialTheme.shapes.large,
        colors = TextFieldColors(),
        label = {
            Text(
                stringResource(R.string.phone_number),
                color = ThemeExtra.colors.secondaryTextColor
            )
        },
        trailingIcon = {
            IconButton({ if (onClear != null) onClear() }) {
                if (value.isNotEmpty()) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        stringResource(R.string.clear),
                        tint = ThemeExtra.colors.grayIcon
                    )
                }
            }
        },
        textStyle = ThemeExtra.typography.Body1Medium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        visualTransformation = transformation
    )
}
