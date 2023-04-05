package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
import androidx.core.text.isDigitsOnly
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.numberMask
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.priceFieldColors
import ru.rikmasters.gilty.shared.shared.textFieldLabel

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
        colors = priceFieldColors(online),
        clear = onClear, label =
        if(value.isNotEmpty()) textFieldLabel(
            true, stringResource(R.string.add_meet_conditions_price_description)
        ) else null, placeholder = textFieldLabel(
            false, stringResource(R.string.add_meet_conditions_price_description)
        ), singleLine = true,
        isError = value.isNotEmpty() && try {
            value.toInt() > 1_000_000
        } catch(e: Exception) {
            false
        },
        errorBottomText = "До 1 000 000 ₽",
        keyboardOptions = KeyboardOptions(
            keyboardType = NumberPassword
        ), visualTransformation =
        transformationOf(numberMask(value.length), if(value.isNotBlank()) " ₽" else "")
    )
}

fun onNull(text: String): String =
    if(text.isNotEmpty()
        && text.first() == '0'
    ) text.substring(1, text.length)
    else text

