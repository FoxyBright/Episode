package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

data class BottomSheetComposeState(
    val width: Dp,
    val expand: MutableState<Boolean>,
    val search: SearchState? = null,
    val content: @Composable () -> Unit
)

@Composable
fun BottomSheetCompose(
    state: BottomSheetComposeState,
    modifier: Modifier = Modifier,
    onDownDrag: (() -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(Expand(state.expand.value, state.width))
            .background(ThemeExtra.colors.searchCardBackground)
    ) {
        DividerBold(
            Modifier
                .width(40.dp)
                .align(Alignment.TopCenter)
                .padding(vertical = 10.dp)
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < 6 && onDownDrag != null) onDownDrag()
                    }
                }
        )
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < 6 && onDownDrag != null) onDownDrag()
                    }
                }, Alignment.TopCenter
        ) {
            Column(
                Modifier.padding(top = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.search != null)
                    SearchActionBar(state.search, Modifier.padding(horizontal = 16.dp))
                state.content()
            }
        }
    }
}

@Composable
private fun Expand(expand: Boolean, width: Dp): Dp {
    return updateTransition(
        remember {
            MutableTransitionState(expand).apply { targetState = !expand }
        }, ""
    ).animateDp(
        { tween(500) }, "",
        { if (expand) width else 0.dp },
    ).value
}

@Preview
@Composable
private fun BottomSheetPreview() {
    GiltyTheme {
        BottomSheetCompose(BottomSheetComposeState(400.dp, mutableStateOf(true)) {
            LazyColumn {
                items(15) {
                    Text("Something content")
                }
            }
        })
    }
}

@Preview
@Composable
private fun BottomSheetWithSearchPreview() {
    GiltyTheme {
        val text = remember { mutableStateOf("") }
        val state = remember { mutableStateOf(false) }
        val searchState = SearchState(
            "Country", state.value, text.value,
            onChangeText = { text.value = it },
            onExpandSearch = { state.value = it }
        )
        BottomSheetCompose(BottomSheetComposeState(
            400.dp, mutableStateOf(false), searchState
        ) {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(15) {
                    Text("Something content")
                }
            }
        })
    }
}