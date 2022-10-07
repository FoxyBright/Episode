package ru.rikmasters.gilty.presentation.ui.presentation.profile

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.shared.DividerBold
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.shared.NumberPicker
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

interface AgeBottomSheetComposeCallback {
    fun onSaveClick(value: Int){}
    fun onDownDrag(){}
}

@Composable
fun AgeBottomSheetCompose(
    modifier: Modifier = Modifier,
    state: Boolean = false,
    callback: AgeBottomSheetComposeCallback? = null
) {
    val sizeTransition = updateTransition(
        remember {
            MutableTransitionState(state).apply {
                targetState = !state
            }
        }, ""
    ).animateDp(
        { tween(durationMillis = 500) },
        "",
        { if (state) 320.dp else 0.dp },
    ).value
    Box(
        modifier
            .height(sizeTransition)
            .background(ThemeExtra.colors.cardBackground)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < 6) callback?.onDownDrag()
                }
            }
    ) {
        DividerBold(
            Modifier
                .width(40.dp)
                .align(Alignment.TopCenter)
                .padding(vertical = 10.dp)
                .clip(CircleShape)
        )
        var pickerValue by remember { mutableStateOf(18) }
        NumberPicker(
            Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 122.dp, top = 50.dp),
            value = pickerValue,
            onValueChange = { pickerValue = it },
            range = 18..100
        )
        GradientButton(
            { callback?.onSaveClick(pickerValue) },
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button), true
        )
    }
}

@Preview
@Composable
private fun AgeBottomSheetPreview() {
    GiltyTheme {
        AgeBottomSheetCompose(state = true)
    }
}