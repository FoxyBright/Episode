package ru.rikmasters.gilty.addmeet.presentation.ui.tags

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun TagSearchPreview() {
    GiltyTheme { TagSearch(SearchState()) }
}

data class SearchState(
    val text: String = "",
    val online: Boolean = false,
    val onBack: (() -> Unit)? = null,
    val onCancelClick: (() -> Unit)? = null,
    val onChangeText: ((String) -> Unit)? = null,
)

@Composable
fun TagSearch(
    state: SearchState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            Modifier, Start,
            CenterVertically
        ) {
            SearchBar(
                Modifier
                    .weight(1f)
                    .height(60.dp), state
            )
            Text(
                stringResource(R.string.cancel_button),
                Modifier
                    .padding(start = 6.dp)
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { state.onCancelClick?.let { it() } },
                style = typography.bodyMedium,
                color = colorScheme.primary,
                fontWeight = SemiBold
            )
        }
        if(!state.text.let {
                it.matches(
                    Regex("[a-zA-z\\s]*")
                ) || it.isBlank()
            }
        ) Text(
            stringResource(R.string.bad_tag_error),
            Modifier.padding(start = 16.dp),
            style = typography.headlineSmall,
            color = colorScheme.primary,
        )
    }
}

@Composable
private fun SearchBar(
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
            verticalAlignment = CenterVertically
        ) {
            BackButton(state.onBack)
            SearchField(
                state.text, state.online,
                focusRequester, state.onChangeText
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchField(
    text: String,
    online: Boolean,
    focusRequester: FocusRequester,
    onChangeText: ((String) -> Unit)?,
) {
    TextField(
        text, { onChangeText?.let { t -> t(it) } },
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        shape = shapes.large,
        colors = searchColors(online),
        placeholder = {
            Text(
                stringResource(R.string.search_placeholder),
                color = colorScheme.onTertiary,
                style = typography.bodyMedium,
                fontWeight = Bold
            )
        },
        textStyle = typography.bodyMedium
            .copy(fontWeight = Bold),
        singleLine = true,
    )
}

@Composable
private fun BackButton(onBack: (() -> Unit)?) {
    IconButton({ onBack?.let { it() } }) {
        Icon(
            painterResource(R.drawable.ic_back),
            stringResource(R.string.action_bar_button_back),
            Modifier.size(20.dp),
            colors.policyAgreeColor
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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