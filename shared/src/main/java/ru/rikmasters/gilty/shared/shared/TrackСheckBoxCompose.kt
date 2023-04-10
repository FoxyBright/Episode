package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun GreenTrackCheckBoxActivePreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    TrackCheckBox(
        Modifier.padding(10.dp),
        checkBoxState,
        Color(0xFF35C65A)
    ) { checkBoxState = it }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    activeColor: Color = colorScheme.primary,
    sliderSize: Int = 26,
    onCheckedChange: (Boolean) -> Unit
) {
    val padding = (sliderSize / 6)
    val moveDp by animateDpAsState(
        if (checked) sliderSize.dp
        else (sliderSize / 3).dp
    )
    val borderColor by animateColorAsState(
        if (checked) Transparent
        else colors.secondaryTrackCheckBox,
        tween(200)
    )
    Surface(
        { onCheckedChange(!checked) },
        modifier
            .width((sliderSize * 2 + padding).dp)
            .height((sliderSize + padding * 2).dp),
        true,
        RoundedCornerShape(50),
        animateColorAsState(
            if (checked) activeColor
            else colors.mainTrackCheckBox,
            tween(200)
        ).value,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Box(
            Modifier
                .padding(
                    start = if (checked) moveDp else 8.dp,
                    end = (sliderSize / 6).dp
                )
                .padding(
                    vertical = animateDpAsState(
                        if (checked) (sliderSize / 6).dp
                        else (sliderSize / 3).dp
                    ).value
                )
        ) {
            Surface(
                { onCheckedChange(!checked) },
                Modifier
                    .size(
                        animateDpAsState(
                            if (checked) sliderSize.dp
                            else (sliderSize / 2 + padding).dp
                        ).value
                    )
                    .clip(CircleShape), (true),
                RoundedCornerShape(50),
                color = White,
                border = BorderStroke(1.dp, borderColor)
            ) {}
        }
    }
}
