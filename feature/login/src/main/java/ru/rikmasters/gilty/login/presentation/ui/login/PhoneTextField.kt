package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        val mask = "+7 ### ###-##-##"
        PhoneTextField(
            "9105152312",
            phoneTransform(mask),
            mask, Modifier.padding(32.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    transform: VisualTransformation,
    mask: String,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    onValueChanged: ((String) -> Unit)? = null
) {
    TextField(
        value,
        { text ->
            if (text.length <= mask.count { it == '#' } && onValueChanged != null)
                onValueChanged(text)
        },
        modifier,
        shape = MaterialTheme.shapes.large,
        colors = TextFieldColors(),
        label = {
            Text(
                stringResource(R.string.phone_number),
                Modifier, MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingIcon = {
            IconButton({ if (onClear != null) onClear() }) {
                if (value.isNotEmpty()) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        stringResource(R.string.clear),
                        tint = MaterialTheme.colorScheme.scrim
                    )
                }
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        ),
        visualTransformation = transform
    )
}