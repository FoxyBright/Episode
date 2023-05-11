package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.tag.GSearchBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun SearchActionBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        )
        SearchActionBar(
            SearchState(("Country"),
                (false), (""), {})
        )
    }
}

@Preview
@Composable
private fun SearchActionBarExpandedPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        )
        SearchActionBar(
            SearchState(("Country"),
                (true), (""), {})
        )
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
            fadeIn(), fadeOut()
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
        Text(
            state.name ?: "", style = typography.labelLarge,
            color = colorScheme.tertiary
        )
        IconButton({
            state.state = true
            state.onExpandSearch?.let { it(true) }
        }) {
            Icon(
                painterResource(R.drawable.magnifier),
                (state.placeHolder
                    ?: stringResource(R.string.search_placeholder)),
                Modifier.size(22.dp),
                colorScheme.tertiary
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: SearchState,
) {
    GSearchBar(
        value = state.text,
        modifier = modifier,
        label = state.placeHolder
            ?: stringResource(R.string.search_placeholder),
        isOnline = state.online,
        fullWidth = false,
        onBack = {
            state.state = false
            state.onExpandSearch?.let { it(false) }
        },
        onTextChange = { state.onChangeText(it) },
        errorActive = false
    )
}