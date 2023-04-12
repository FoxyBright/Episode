package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
import androidx.core.text.isDigitsOnly
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.numberMask
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.textFieldLabel

@Composable
fun PriceTextField(
    value: String,
    onChange: (String) -> Unit,
    online: Boolean,
) {
    var focus by remember { mutableStateOf(false) }
    GTextField(
        value, {
            if(it.isDigitsOnly()
                || it.isBlank()
            ) onChange(onNull(it))
        },
        Modifier
            .fillMaxWidth()
            .onFocusChanged { focus = it.isFocused },
        colors = priceFieldColors(focus, online),
        label = if(value.isNotEmpty()) textFieldLabel(
            true,
            stringResource(R.string.add_meet_conditions_price_description)
        ) else null, placeholder = textFieldLabel(
            false,
            stringResource(R.string.add_meet_conditions_price_description)
        ), singleLine = true,
        isError = value.isNotEmpty() && try {
            value.toInt() > 1_000_000
        } catch(e: Exception) {
            true
        },
        errorBottomText = "До 1 000 000 ₽",
        keyboardOptions = KeyboardOptions(
            keyboardType = NumberPassword
        ), visualTransformation =
        transformationOf(
            numberMask(value.length),
            if(focus || value.isBlank())
                "" else " ₽"
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun priceFieldColors(
    focus: Boolean,
    online: Boolean = false,
) = TextFieldDefaults.textFieldColors(
    textColor = if(focus) colorScheme.tertiary
    else if(online) colorScheme.secondary
    else colorScheme.primary,
    cursorColor = if(online) colorScheme.secondary
    else colorScheme.primary,
    containerColor = colorScheme.primaryContainer,
    unfocusedLabelColor = colorScheme.onTertiary,
    disabledLabelColor = colorScheme.scrim,
    focusedLabelColor = colorScheme.tertiary,
    disabledTrailingIconColor = Transparent,
    focusedTrailingIconColor = Transparent,
    unfocusedTrailingIconColor = Transparent,
    focusedIndicatorColor = Transparent,
    unfocusedIndicatorColor = Transparent,
    disabledIndicatorColor = Transparent,
    errorIndicatorColor = Transparent,
    errorLabelColor = colorScheme.primary,
    placeholderColor = colorScheme.onTertiary,
    disabledPlaceholderColor = colorScheme.scrim,
    disabledTextColor = colorScheme.scrim,
)

fun onNull(text: String): String =
    if(text.isNotEmpty()
        && text.first() == '0'
    ) text.substring(1, text.length)
    else text

