package ru.rikmasters.gilty.shared.shared.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.searchColors

@Composable
fun TagSearch(
    value: String,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false,
    add: Boolean = false,
    onBack: (() -> Unit)? = null,
    onTextChange: ((String) -> Unit)? = null,
    onEnterAction: ((String) -> Unit)? = null,
) {
    val manager = LocalFocusManager.current
    Column {
        Row(
            modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(
                    horizontal = if(add) 0.dp
                    else 16.dp
                )
                .background(
                    colorScheme.primaryContainer,
                    if(add) RectangleShape
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
            GTextField(
                value = value,
                { onTextChange?.let { t -> t(it) } },
                Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                shape = shapes.large,
                colors = searchColors(isOnline),
                placeholder = {
                    Text(
                        stringResource(
                            if(add) R.string.meeting_filter_add_tag_text_holder
                            else R.string.search_placeholder
                        ), color = colorScheme.onTertiary,
                        style = typography.bodyMedium,
                        fontWeight = Bold
                    )
                },
                keyboardOptions = Default.copy(
                    imeAction = Done,
                    keyboardType = Text,
                    capitalization = Sentences
                ),
                keyboardActions = KeyboardActions {
                    manager.clearFocus()
                    onEnterAction?.let { it(value) }
                },
                textStyle = typography.bodyMedium
                    .copy(fontWeight = Bold),
                singleLine = true,
            )
        }
        Text(
            if(value.contains(Regex("[^A-Za-zА-Я-а-яёЁ\\d]")))
                stringResource(R.string.bad_tag_error)
            else "", Modifier
                .padding(
                    start = 32.dp,
                    bottom = 4.dp
                ), colorScheme.primary,
            style = typography.headlineSmall
        )
    }
}