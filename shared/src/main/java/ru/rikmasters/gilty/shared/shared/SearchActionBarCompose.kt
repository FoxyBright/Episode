package ru.rikmasters.gilty.shared.shared

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val name: String = "",
    var state: Boolean,
    val text: String,
    val onChangeText: (it: String) -> Unit,
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
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                    shape = MaterialTheme.shapes.large,
                    colors = TransparentTextFieldColors(),
                    placeholder = {
                        Text(
                            stringResource(R.string.login_search_placeholder),
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = Bold
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = Bold),
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
                Text(state.name, style = MaterialTheme.typography.titleLarge)
                IconButton({
                    state.state = true
                    state.onExpandSearch?.let { it(true) }
                }) {
                    Icon(
                        painterResource(R.drawable.magnifier),
                        stringResource(R.string.login_search_placeholder),
                        Modifier.size(22.dp),
                        MaterialTheme.colorScheme.tertiary
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
        { if (expand) Resources.getSystem().displayMetrics.widthPixels.dp else 0.dp },
    ).value
}
