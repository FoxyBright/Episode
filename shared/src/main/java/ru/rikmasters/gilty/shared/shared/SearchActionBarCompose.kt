package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun SearchActionBarPreview() {
    GiltyTheme {
        val state = remember { mutableStateOf(false) }
        val text = remember { mutableStateOf("") }
        SearchActionBar(SearchState("Country", state.value, text.value,
            onChangeText = { text.value = it },
            onExpandSearch = { state.value = it }
        ))
    }
}

data class SearchState(
    val name: String? = null,
    var state: Boolean,
    val text: String,
    val onChangeText: (it: String) -> Unit,
    val online: Boolean = false,
    val placeHolder: String? = null,
    val onExpandSearch: ((Boolean) -> Unit)? = null,
)

@Composable
fun SearchActionBar(
    state: SearchState,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            !state.state,
            Modifier.height(60.dp),
            fadeIn(),
            fadeOut()
        ) { LabelBar(Modifier, state) }
        AnimatedVisibility(
            state.state,
            Modifier.height(60.dp),
            slideInHorizontally { it / 2 } + fadeIn(),
            slideOutHorizontally { it / 2 } + fadeOut()
        ) { SearchBar(Modifier, state) }
    }
}

@Composable
private fun LabelBar(
    modifier: Modifier = Modifier,
    state: SearchState,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Text(state.name ?: "", style = typography.labelLarge)
        IconButton({
            state.state = true
            state.onExpandSearch?.let { it(true) }
        }) {
            Icon(
                painterResource(R.drawable.magnifier),
                (state.placeHolder ?: stringResource(R.string.search_placeholder)),
                Modifier.size(22.dp),
                colorScheme.tertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: SearchState,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Box(
        modifier
            .clip(shapes.extraSmall)
            .background(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton({
                state.state = false
                state.onExpandSearch?.let { it(false) }
            }) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    stringResource(R.string.action_bar_button_back),
                    Modifier.size(20.dp),
                    ThemeExtra.colors.policyAgreeColor
                )
            }
            TextField(
                state.text,
                { state.onChangeText(it) },
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = shapes.large,
                colors = searchColors(state.online),
                placeholder = {
                    Text(
                        (state.placeHolder ?: stringResource(R.string.search_placeholder)),
                        color = colorScheme.onTertiary,
                        style = typography.bodyMedium,
                        fontWeight = Bold
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                textStyle = typography.bodyMedium
                    .copy(fontWeight = Bold),
                singleLine = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun searchColors(online: Boolean) = textFieldColors(
    textColor = colorScheme.tertiary,
    cursorColor = if(online) colorScheme.secondary
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