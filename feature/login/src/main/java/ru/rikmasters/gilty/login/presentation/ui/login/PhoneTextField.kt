package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel
import ru.rikmasters.gilty.shared.shared.TransparentTextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        val mask = "+7 ### ###-##-##"
        PhoneTextField(
            "9105152312",
            textMask(mask),
            Modifier.padding(32.dp),
        )
    }
}

@Composable
fun PhoneTextField(
    value: String,
    transform: VisualTransformation,
    modifier: Modifier = Modifier,
    size: Int = 10,
    onClear: (() -> Unit)? = null,
    onValueChanged: ((String) -> Unit)? = null
) {
    GTextField(
        value,
        { text ->
            if (text.length <= size && onValueChanged != null)
                onValueChanged(text)
        }, modifier, shape = MaterialTheme.shapes.large,
        colors = TransparentTextFieldColors(),
        label = if (value.isNotEmpty()) TextFieldLabel(
            (true), stringResource(R.string.phone_number)
        ) else null, placeholder = TextFieldLabel(
            (false), stringResource(R.string.phone_number)
        ), textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ), visualTransformation = transform, clear = onClear
    )
}