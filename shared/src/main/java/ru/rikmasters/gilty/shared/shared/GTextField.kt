package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun GTextFieldPlaceHolder() {
    GiltyTheme {
        GTextField(
            (""), {}, Modifier.padding(20.dp),
            placeholder = { Placeholder() },
            colors = PreviewColors(),
            clear = {}
        )
    }
}

@Preview
@Composable
private fun GTextFieldText() {
    GiltyTheme {
        GTextField(
            ("Текст"), {},
            Modifier.padding(20.dp),
            label = { Label() },
            colors = PreviewColors(),
        )
    }
}

@Preview
@Composable
private fun GTextFieldError() {
    GiltyTheme {
        GTextField(
            ("Текст"), {}, Modifier.padding(20.dp),
            label = { Label() },
            colors = PreviewColors(),
            isError = true,
            errorBottomText = "Ошибка при вводе текста"
        )
    }
}

@Preview
@Composable
private fun GTextFieldManyText() {
    GiltyTheme {
        GTextField(
            ("Если в текстбоксе очень много текста, " +
                    "то он его переносит на следующую строку"),
            {}, Modifier.padding(20.dp), label = { Label() },
            colors = PreviewColors(),
        )
    }
}

@Composable
private fun Label() {
    Text(
        "Поле", Modifier,
        style = typography.bodyMedium
    )
}

@Composable
private fun Placeholder() {
    Text(
        "Введите текст", Modifier,
        style = typography.bodyMedium
    )
}

@Composable
fun TextFieldLabel(
    label: Boolean = false,
    text: String? = null
): @Composable (() -> Unit) {
    return {
        text?.let {
            Text(
                it, Modifier, style = if(label)
                    typography.headlineSmall
                else typography.bodyMedium
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = typography.bodyMedium,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? =
        { Icon(painterResource(R.drawable.ic_trailing), (null)) },
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource =
        remember { MutableInteractionSource() },
    shape: Shape = shapes.large,
    colors: TextFieldColors = TextFieldColors(),
    clear: (() -> Unit)? = null,
    errorBottomText: String? = null
) {
    Column(modifier) {
        Box(Modifier, Alignment.TopEnd) {
            TextField(
                value, { onValueChange(it) },
                Modifier.fillMaxWidth(), enabled, readOnly,
                textStyle, label, placeholder, leadingIcon,
                trailingIcon, supportingText, isError,
                visualTransformation, keyboardOptions,
                keyboardActions, singleLine, maxLines,
                interactionSource, shape, colors
            ); if(value.isNotEmpty() && !isError)
            clear?.let {
                IconButton({ it() }) {
                    if(value.isNotEmpty()) {
                        Icon(
                            painterResource(R.drawable.ic_close),
                            stringResource(R.string.clear),
                            Modifier.size(24.dp), colorScheme.scrim
                        )
                    }
                }
            }
        }
        errorBottomText?.let {
            if(isError)
                Text(
                    it, Modifier.padding(start = 16.dp, top = 4.dp),
                    colorScheme.primary, style = typography.titleSmall
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldColors() = TextFieldDefaults.textFieldColors(
    textColor = colorScheme.tertiary,
    containerColor = colorScheme.primaryContainer,
    unfocusedLabelColor = colorScheme.onTertiary,
    disabledLabelColor = colorScheme.onTertiary,
    focusedLabelColor = colorScheme.tertiary,
    disabledTrailingIconColor = Transparent,
    focusedTrailingIconColor = Transparent,
    unfocusedTrailingIconColor = Transparent,
    focusedIndicatorColor = Transparent,
    unfocusedIndicatorColor = Transparent,
    disabledIndicatorColor = Transparent,
    errorIndicatorColor = Transparent,
    placeholderColor = colorScheme.onTertiary,
    disabledPlaceholderColor = Transparent,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextFieldColors() = TextFieldDefaults.textFieldColors(
    textColor = colorScheme.tertiary,
    containerColor = Transparent,
    unfocusedLabelColor = colorScheme.onTertiary,
    disabledLabelColor = colorScheme.onTertiary,
    focusedLabelColor = colorScheme.tertiary,
    disabledTrailingIconColor = Transparent,
    focusedTrailingIconColor = Transparent,
    unfocusedTrailingIconColor = Transparent,
    focusedIndicatorColor = Transparent,
    unfocusedIndicatorColor = Transparent,
    disabledIndicatorColor = Transparent,
    errorIndicatorColor = Transparent,
    placeholderColor = colorScheme.onTertiary,
    disabledPlaceholderColor = Transparent,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewColors() = TextFieldDefaults.textFieldColors(
    textColor = colorScheme.tertiary,
    containerColor = colorScheme.primaryContainer,
    unfocusedLabelColor = Color(0xFF98989F),
    disabledLabelColor = Color(0xFF98989F),
    focusedLabelColor = Color(0xFF000000),
    disabledTrailingIconColor = Transparent,
    focusedTrailingIconColor = Transparent,
    unfocusedTrailingIconColor = Transparent,
    focusedIndicatorColor = Transparent,
    unfocusedIndicatorColor = Transparent,
    disabledIndicatorColor = Transparent,
    errorIndicatorColor = Transparent,
    placeholderColor = Color(0xFF98989F),
    disabledPlaceholderColor = Transparent,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DescriptionColors(online: Boolean) =
    TextFieldDefaults.textFieldColors(
        textColor = colorScheme.tertiary,
        cursorColor = if(online) colorScheme.secondary
        else colorScheme.primary,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.onTertiary,
        focusedLabelColor = colorScheme.tertiary,
        disabledTrailingIconColor = Transparent,
        focusedTrailingIconColor = Transparent,
        unfocusedTrailingIconColor = Transparent,
        focusedIndicatorColor = Transparent,
        unfocusedIndicatorColor = Transparent,
        disabledIndicatorColor = Transparent,
        errorIndicatorColor = Transparent,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = Transparent,
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriceFieldColors(online: Boolean = false) =
    TextFieldDefaults.textFieldColors(
        textColor = if(online) colorScheme.secondary
        else colorScheme.primary,
        cursorColor = if(online) colorScheme.secondary
        else colorScheme.primary,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.onTertiary,
        focusedLabelColor = colorScheme.tertiary,
        disabledTrailingIconColor = Transparent,
        focusedTrailingIconColor = Transparent,
        unfocusedTrailingIconColor = Transparent,
        focusedIndicatorColor = Transparent,
        unfocusedIndicatorColor = Transparent,
        disabledIndicatorColor = Transparent,
        errorIndicatorColor = Transparent,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = Transparent,
    )