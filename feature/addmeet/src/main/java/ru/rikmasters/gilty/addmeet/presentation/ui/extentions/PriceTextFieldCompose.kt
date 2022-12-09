package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.shared.GTextField
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DescriptionColors(online: Boolean) =
    TextFieldDefaults.textFieldColors(
        textColor = MaterialTheme.colorScheme.tertiary,
        cursorColor = if(online) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
        disabledLabelColor = MaterialTheme.colorScheme.onTertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        disabledTrailingIconColor = Color.Transparent,
        focusedTrailingIconColor = Color.Transparent,
        unfocusedTrailingIconColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colorScheme.onTertiary,
        disabledPlaceholderColor = Color.Transparent,
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriceFieldColors(online: Boolean = false) =
    TextFieldDefaults.textFieldColors(
        textColor = if(online) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.primary,
        cursorColor = if(online) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
        disabledLabelColor = MaterialTheme.colorScheme.onTertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        disabledTrailingIconColor = Color.Transparent,
        focusedTrailingIconColor = Color.Transparent,
        unfocusedTrailingIconColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colorScheme.onTertiary,
        disabledPlaceholderColor = Color.Transparent,
    )