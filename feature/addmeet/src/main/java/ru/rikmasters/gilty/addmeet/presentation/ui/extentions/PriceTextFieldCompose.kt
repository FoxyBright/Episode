package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.PriceFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel

@Composable
fun PriceTextField(
    value: String,
    onChange: (String) -> Unit,
    onClear: () -> Unit,
    online: Boolean,
) {
    GTextField(
        value, {
            if(it.isDigitsOnly()
                || it.isBlank()
            ) onChange(onNull(it))
        }, Modifier.fillMaxWidth(),
        colors = PriceFieldColors(online),
        clear = onClear, label =
        if(value.isNotEmpty()) TextFieldLabel(
            true, stringResource(R.string.add_meet_conditions_price_description)
        ) else null, placeholder = TextFieldLabel(
            false, stringResource(R.string.add_meet_conditions_price_description)
        ), singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ), visualTransformation =
        textMask(numberMask(value.length), if(value.isNotBlank()) " ₽" else "")
    
    )
}

fun onNull(text: String): String =
    if(text.isNotEmpty()
        && text.first() == '0'
    ) text.substring(1, text.length)
    else text

