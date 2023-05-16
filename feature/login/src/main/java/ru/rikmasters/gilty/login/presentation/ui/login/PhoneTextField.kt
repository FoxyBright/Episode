package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
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

@Preview
@Composable
private fun PhoneTextFieldPreview() {
    GiltyTheme {
        PhoneTextField(
            "79105152312",
            DemoCountry, Modifier
                .padding(16.dp)
                .background(colorScheme.background),
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
    onValueChanged: (String) -> Unit,
) {
    val focusManager =
        LocalFocusManager.current
    var focused by remember {
        mutableStateOf(false)
    }
    
    val dial = country.phoneDial
    
    val mask = remember(country.phoneMask, dial) {
        country.phoneMask.replace(
            dial, dial.replace(
                Regex("\\d"), "#"
            )
        )
    }
    val length = remember(mask) {
        mask.count { it == '#' }
    }
    val transform = remember(mask) {
        transformationOf(mask)
    }
    
    GTextField(
        value = value,
        onValueChange = { text ->
            if(
                text.length <= country.clearPhoneDial.length
                || text.substring(
                    0, country.clearPhoneDial.length
                ) != country.clearPhoneDial
            ) onClear()
            else if(text.length <= length)
                onValueChanged(text)
        },
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
        },
        shape = MaterialTheme.shapes.large,
        colors = transparentTextFieldColors(),
        label = if(value.isNotEmpty())
            textFieldLabel(
                true, stringResource(R.string.phone_number)
            ) else null,
        placeholder = textFieldLabel(
            false, stringResource(R.string.phone_number)
        ),
        textStyle = typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = NumberPassword
        ),
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        },
        visualTransformation = transform,
        clear = if(focused) onClear else null,
        singleLine = true,
        containerColor = Transparent
    )
}