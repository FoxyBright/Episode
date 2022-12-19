package ru.rikmasters.gilty.shared.shared

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

data class SearchState(
    val name: String? = null,
    var state: Boolean,
    val text: String,
    val onChangeText: (it: String) -> Unit,
    val online: Boolean = false,
    val onExpandSearch: ((Boolean) -> Unit)? = null
)

@Preview
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchActionBar(
    state: SearchState,
    modifier: Modifier = Modifier
) {
    val width = SearchExpand(state.state)
    Box(Modifier.fillMaxWidth()) {
        Box(
            modifier
                .height(60.dp)
                .width(width)
                .align(Alignment.CenterEnd)
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
                    Modifier.fillMaxWidth(),
                    shape = shapes.large,
                    colors = SearchColors(state.online),
                    placeholder = {
                        Text(
                            stringResource(R.string.login_search_placeholder),
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
        }
        AnimatedVisibility(!state.state) {
            Row(
                Modifier
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
                        stringResource(R.string.login_search_placeholder),
                        Modifier.size(22.dp),
                        colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchExpand(expand: Boolean): Dp {
    return updateTransition(
        remember {
            MutableTransitionState(expand).apply { targetState = !expand }
        }, ""
    ).animateDp(
        { tween(400) }, "",
        { if(expand) Resources.getSystem().displayMetrics.widthPixels.dp else 0.dp },
    ).value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchColors(online: Boolean) = TextFieldDefaults.textFieldColors(
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