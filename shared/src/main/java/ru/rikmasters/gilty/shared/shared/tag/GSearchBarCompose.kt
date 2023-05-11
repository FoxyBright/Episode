package ru.rikmasters.gilty.shared.shared.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R

@Composable
fun GSearchBar(
    value: String,
    modifier: Modifier = Modifier,
    label: String,
    isOnline: Boolean = false,
    fullWidth: Boolean = false,
    onBack: (() -> Unit)? = null,
    onTextChange: ((String) -> Unit)? = null,
    errorActive: Boolean = true,
    errorText: String = "",
    onEnterAction: ((String) -> Unit)? = null,
) {
    val height = with(LocalDensity.current) { 48.sp.toDp() }
    Column {
        Row(
            modifier
                .fillMaxWidth()
                .heightIn(48.dp, height)
                .padding(
                    horizontal = if(fullWidth)
                        0.dp else 16.dp
                )
                .background(
                    colorScheme.primaryContainer,
                    if(fullWidth) RectangleShape
                    else shapes.extraSmall
                ), Start, CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_back),
                (null), Modifier
                    .padding(start = 22.dp)
                    .size(28.dp)
                    .clickable(
                        MutableInteractionSource(),
                        (null)
                    ) { onBack?.let { it() } },
                colorScheme.onTertiary
            )
            SimpleTextField(
                value = value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                label = label,
                isOnline = isOnline,
                onTextChange = onTextChange,
                onEnterAction = onEnterAction
            )
        }
        val rule = value.contains(
            Regex("[^A-Za-zА-Я-а-яёЁ\\d]")
        )
        if(errorActive) Text(
            text = if(rule) errorText else "",
            modifier = Modifier
                .padding(
                    start = 32.dp,
                    bottom = 4.dp
                ),
            color = colorScheme.primary,
            style = typography.headlineSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: String,
    isOnline: Boolean,
    onTextChange: ((String) -> Unit)?,
    onEnterAction: ((String) -> Unit)?,
) {
    val iS = remember { MutableInteractionSource() }
    val manager = LocalFocusManager.current
    val vT = VisualTransformation.None
    val font = typography.bodyMedium
        .copy(colorScheme.tertiary, 17.sp, W700)
    BasicTextField(
        value = value, modifier = modifier,
        onValueChange = {
            onTextChange?.let { c -> c(it) }
        },
        textStyle = font,
        keyboardOptions = Default.copy(
            imeAction = Done,
            keyboardType = Text,
            capitalization = Sentences
        ),
        keyboardActions = KeyboardActions {
            manager.clearFocus()
            onEnterAction?.let { it(value) }
        },
        cursorBrush = SolidColor(
            if(isOnline) colorScheme.secondary
            else colorScheme.primary
        )
    ) { innerTextField ->
        TextFieldDecorationBox(
            value = value,
            visualTransformation = vT,
            innerTextField = innerTextField,
            contentPadding = PaddingValues(vertical = 2.dp),
            placeholder = {
                Text(
                    label, Modifier,
                    colorScheme.onTertiary,
                    style = font
                )
            },
            shape = shapes.large,
            singleLine = true,
            enabled = true,
            interactionSource = iS,
            colors = textFieldColors(
                textColor = colorScheme.tertiary,
                cursorColor = if(isOnline) colorScheme.secondary
                else colorScheme.primary,
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
        )
    }
}