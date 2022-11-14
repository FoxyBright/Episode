package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.numberMask
import ru.rikmasters.gilty.shared.common.extentions.textMask
import ru.rikmasters.gilty.shared.shared.GTextField

@Composable
fun PriceTextField(
    value: String,
    onChange: (String) -> Unit,
    onClear: () -> Unit,
    colors: TextFieldColors,
    focus: FocusState?,
    onFocused: (FocusState) -> Unit
) {
    GTextField(
        value, {
            if (it.length <= 15
                && it.matches(Regex("^\\d+\$"))
            ) onChange(onNull(it))
        },
        Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocused(it) },
        colors = colors, clear = onClear, label =
        if (value.isNotEmpty()) label(true) else null,
        placeholder = label(), singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ), visualTransformation =
        if (focus != null) {
            if (focus.isFocused) {
                textMask(numberMask(value.length))
            } else textMask(numberMask(value.length - 2))
        } else textMask(numberMask(value.length))
    )
}

private fun onNull(text: String): String =
    if (text.first() == '0'
        && text.isNotEmpty()
    ) text.substring(1, text.length)
    else text

@Composable
private fun label(
    label: Boolean = false
): @Composable (() -> Unit) {
    return {
        Text(
            stringResource(R.string.add_meet_conditions_price_description),
            Modifier, style = if (label)
                MaterialTheme.typography.headlineSmall
            else MaterialTheme.typography.bodyMedium
        )
    }
}