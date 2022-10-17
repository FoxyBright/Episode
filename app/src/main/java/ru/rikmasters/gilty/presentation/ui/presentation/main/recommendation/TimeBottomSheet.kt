package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ru.rikmasters.gilty.presentation.ui.shared.ScrollTimePicker
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

interface TimeBottomSheetComposeCallback {
    fun onSaveClick(value: String)
}

private const val EMPTY_TIME = "00:00"
private const val START_TIME = "00"

@Composable
fun AgeBottomSheetCompose(
    modifier: Modifier = Modifier,
    state: Boolean = false,
    callback: TimeBottomSheetComposeCallback? = null
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
                    //Здесь событие на свертывание
                    if (dragAmount < 6) callback?.onSaveClick("")
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
        var time = EMPTY_TIME
        Row(Modifier.fillMaxWidth()) {
            ScrollTimePicker(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 122.dp, top = 50.dp),
                mutableStateOf(START_TIME),
                mutableStateOf(START_TIME)
            ) { h, m -> time = "$h:$m" }
        }
        GradientButton(
            { callback?.onSaveClick(time) },
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
fun AgeBottomSheetPreview() {
    GiltyTheme {
        AgeBottomSheetCompose(state = true)
    }
}