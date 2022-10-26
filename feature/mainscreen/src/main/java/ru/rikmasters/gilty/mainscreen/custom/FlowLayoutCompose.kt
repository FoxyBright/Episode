package ru.rikmasters.gilty.mainscreen.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.math.max

@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    data class FlowContent(val placeable: Placeable, val x: Int, val y: Int)
    Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        var y = 0
        var x = 0
        var rowMaxY = 0
        val flowContents = mutableListOf<FlowContent>()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        placeables.forEach { placeable ->
            if (placeable.width + x > constraints.maxWidth) {
                x = 0
                y += rowMaxY
                rowMaxY = 0
            }
            rowMaxY = max(placeable.height + verticalSpacingPx, rowMaxY)
            flowContents.add(FlowContent(placeable, x, y))
            x += placeable.width + horizontalSpacingPx
        }
        y += rowMaxY
        layout(constraints.maxWidth, y) { flowContents.forEach { it.placeable.place(it.x, it.y) } }
    }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun PreviewFlowRow() {
    GiltyTheme {
        Surface {
            FlowLayout(Modifier.padding(8.dp), 8.dp, 8.dp) {
                repeat(10) {
                    Box(
                        Modifier
                            .width((80 + it * 2).dp)
                            .height(40.dp)
                            .background(Color.Blue)
                    )
                }
            }
        }
    }
}