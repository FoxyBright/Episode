package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.DemoCountry
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.textFieldLabel
import ru.rikmasters.gilty.shared.shared.transparentTextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        PhoneTextField(
            "9105152312",
            DemoCountry,
            Modifier.padding(32.dp),
            { }, { }
        )
    }
}

@Composable
fun PhoneTextField(
    value: String,
    country: Country,
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    val dial = country.phoneDial
    val mask = remember(country.phoneMask, dial) {
        country.phoneMask.replace(
            dial,
            dial.replace(Regex("\\d"), "#")
        )
    }
    val length = remember(mask) {
        mask.count { it == '#' }
    }
    val transform = remember(mask) {
        transformationOf(mask)
    }
    
    GTextField(
        value,
        { text ->
            if(text.length <= length)
                onValueChanged(text)
        }, modifier, shape = MaterialTheme.shapes.large,
        colors = transparentTextFieldColors(),
        label = if(value.isNotEmpty())
            textFieldLabel(
                true, stringResource(R.string.phone_number)
            )
        else null,
        placeholder = textFieldLabel(
            false, stringResource(R.string.phone_number)
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        visualTransformation = transform,
        clear = onClear,
        singleLine = true
    )
}