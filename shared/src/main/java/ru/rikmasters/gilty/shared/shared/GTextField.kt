package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
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
            colors = previewColors(),
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
            colors = previewColors(),
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
            colors = previewColors(),
            isError = true,
            errorBottomText = "Ошибка при вводе текста",
            clear = {}
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
            colors = previewColors(),
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
fun textFieldLabel(
    label: Boolean = false,
    text: String? = null,
    labelFont: TextStyle = typography.headlineSmall,
    holderFont: TextStyle = typography.bodyMedium,
): @Composable (() -> Unit) {
    return {
        text?.let {
            Text(
                it, Modifier,
                style = if(label) labelFont
                else holderFont
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
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Done, keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource =
        remember { MutableInteractionSource() },
    shape: Shape = shapes.large,
    colors: TextFieldColors =
        ru.rikmasters.gilty.shared.shared.textFieldColors(),
    clear: (() -> Unit)? = null,
    errorBottomText: String? = null,
    textOffset: Boolean = false,
    containerColor: Color = colorScheme.primaryContainer,
) {
    Column(modifier) {
        Card(
            Modifier.fillMaxWidth(),
            shapes.large,
            cardColors(containerColor),
            border = if(isError)
                BorderStroke(1.dp, colorScheme.primary)
            else null
        ) {
            Box(Modifier, TopEnd) {
                TextField(
                    value, { onValueChange(it) },
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            end = clear?.let { 40.dp } ?: 0.dp
                        )
                        .offset(
                            y = if((maxLines > 1
                                        && !singleLine
                                        && value.isBlank())
                                || textOffset
                            ) 4.dp else 0.dp
                        ), enabled, readOnly,
                    textStyle, label, placeholder, leadingIcon,
                    trailingIcon, supportingText, isError,
                    visualTransformation, keyboardOptions,
                    keyboardActions, singleLine, maxLines,
                    interactionSource, shape, colors
                )
                if(value.isNotEmpty()) clear?.let {
                    val single = singleLine || maxLines == 1
                    IconButton(
                        { it() }, Modifier
                            .align(
                                if(single) CenterEnd
                                else TopEnd
                            )
                            .padding(
                                top = if(single) 0.dp
                                else 4.dp
                            )
                    ) {
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
        }
        errorBottomText?.let {
            if(isError) Text(
                it, Modifier.padding(
                    top = 5.dp,
                    start = 16.dp
                ), colorScheme.primary,
                style = typography.headlineSmall
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun textFieldColors() = textFieldColors(
    textColor = colorScheme.tertiary,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun transparentTextFieldColors() = textFieldColors(
    textColor = colorScheme.tertiary,
    containerColor = Transparent,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun previewColors() = textFieldColors(
    textColor = colorScheme.tertiary,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun descriptionColors(online: Boolean) =
    textFieldColors(
        textColor = colorScheme.tertiary,
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