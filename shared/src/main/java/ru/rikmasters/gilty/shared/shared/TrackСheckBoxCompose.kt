package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun GreenTrackCheckBoxActivePreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    TrackCheckBox(
        checkBoxState,
        Modifier.padding(10.dp),
        Color(0xFF35C65A)
    ) { checkBoxState = it }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    sliderSize: Int = 24,
    onCheckedChange: (Boolean) -> Unit
) {
    val padding = (sliderSize / 6)
    val moveDp by animateDpAsState(
        if (checked) sliderSize.dp
        else (sliderSize / 3).dp
    )
    val borderColor by animateColorAsState(
        if (checked) Color.Transparent
        else ThemeExtra.colors.secondaryTrackCheckBox
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
            else ThemeExtra.colors.mainTrackCheckBox
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
                    .clip(CircleShape),
                true,
                RoundedCornerShape(50),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor)
            ) {}
        }
    }
}
