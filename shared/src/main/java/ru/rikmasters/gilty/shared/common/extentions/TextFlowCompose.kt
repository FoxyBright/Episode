package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Clip
import androidx.compose.ui.unit.*
import androidx.compose.ui.unit.IntOffset.Companion.Zero
import androidx.compose.ui.unit.TextUnit.Companion.Unspecified
import ru.rikmasters.gilty.shared.common.extentions.TextFlowContent.Obstacle
import ru.rikmasters.gilty.shared.common.extentions.TextFlowContent.Text
import ru.rikmasters.gilty.shared.common.extentions.TextFlowObstacleAlignment.BottomEnd
import ru.rikmasters.gilty.shared.common.extentions.TextFlowObstacleAlignment.TopEnd
import ru.rikmasters.gilty.shared.common.extentions.TextFlowObstacleAlignment.TopStart
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.max

@Composable
fun TextFlow(
    text: String,
    modifier: Modifier = Modifier,
    obstacleAlignment:
    TextFlowObstacleAlignment = TopStart,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = Unspecified,
    overflow: TextOverflow = Clip,
    softWrap: Boolean = true,
    maxLines: Int = MAX_VALUE,
    onTextLayout: (
        TextLayoutResult?,
        TextLayoutResult?
    ) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
    obstacleContent: @Composable () -> Unit = {},
) {
    TextFlow(
        AnnotatedString(text), modifier,
        obstacleAlignment, color,
        fontSize, fontStyle, fontWeight,
        fontFamily, letterSpacing,
        textDecoration, textAlign,
        lineHeight, overflow,
        softWrap, maxLines, onTextLayout,
        style, obstacleContent,
    )
}

@Composable
fun TextFlow(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    obstacleAlignment:
    TextFlowObstacleAlignment = TopStart,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = Unspecified,
    overflow: TextOverflow = Clip,
    softWrap: Boolean = true,
    maxLines: Int = MAX_VALUE,
    onTextLayout: (
        TextLayoutResult?,
        TextLayoutResult?
    ) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
    obstacleContent: @Composable () -> Unit = {},
) {
    SubcomposeLayout(modifier) { constraints ->
        val looseConstraints = constraints
            .copy(minWidth = 0, minHeight = 0)
        val obstaclePlaceables = subcompose(
            Obstacle, obstacleContent
        ).map { it.measure(looseConstraints) }
        val maxObstacleWidth = obstaclePlaceables
            .maxOfOrNull { it.width } ?: 0
        val maxObstacleHeight = obstaclePlaceables
            .maxOfOrNull { it.height } ?: 0
        val textPlaceable = subcompose(Text) {
            TextFlowCanvas(
                text,
                IntSize(
                    maxObstacleWidth,
                    maxObstacleHeight
                ),
                obstacleAlignment,
                constraints, color,
                fontSize, fontStyle,
                fontWeight, fontFamily,
                letterSpacing, textDecoration,
                textAlign, lineHeight, overflow,
                softWrap, maxLines, onTextLayout, style,
            )
        }.first().measure(looseConstraints)
        layout(
            textPlaceable.width,
            max(
                if(obstacleAlignment == BottomEnd)
                    maxObstacleHeight + maxObstacleHeight
                else maxObstacleHeight,
                textPlaceable.height
            )
        ) {
            obstaclePlaceables.forEach {
                it.place(
                    when(obstacleAlignment) {
                        TopStart -> Zero
                        TopEnd -> IntOffset(constraints.maxWidth - maxObstacleWidth, 0)
                        BottomEnd -> IntOffset(
                            constraints.maxWidth - maxObstacleWidth,
                            maxObstacleHeight
                        )
                    }
                )
            }; textPlaceable.place(0, 0)
        }
    }
}

enum class TextFlowObstacleAlignment { TopStart, TopEnd, BottomEnd }

private enum class TextFlowContent { Obstacle, Text }

@Composable
@Suppress("DEPRECATION")
@OptIn(ExperimentalTextApi::class)
private fun TextFlowCanvas(
    text: AnnotatedString,
    obstacleSize: IntSize,
    obstacleAlignment: TextFlowObstacleAlignment,
    constraints: Constraints,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = Unspecified,
    overflow: TextOverflow = Clip,
    softWrap: Boolean = true,
    maxLines: Int = MAX_VALUE,
    onTextLayout: (
        TextLayoutResult?,
        TextLayoutResult?
    ) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse { LocalContentColor.current }
    }
    val mergedStyle = style.merge(
        TextStyle(
            textColor, fontSize,
            fontWeight, fontStyle,
            textAlign = textAlign,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            letterSpacing = letterSpacing,
            platformStyle = PlatformTextStyle((false)),
        )
    )
    val textMeasurer = rememberTextMeasurer()
    val result = textMeasurer.measureTextFlow(
        text, obstacleSize, constraints.maxWidth,
        constraints.maxHeight, overflow, softWrap,
        maxLines, mergedStyle, obstacleAlignment
    )
    onTextLayout(result.topTextResult, result.bottomTextResult)
    val canvasSize = with(LocalDensity.current) {
        DpSize(
            constraints.constrainWidth(
                obstacleSize.width
                        + result.topTextResult.size.width
            ).toDp(),
            constraints.constrainHeight(
                result.topTextResult.size.height
                        + result.bottomTextResult.size.height
            ).toDp()
        )
    }
    Canvas(Modifier.size(canvasSize)) {
        translate(
            left = if(obstacleAlignment == TopStart)
                obstacleSize.width.toFloat() else 0f
        ) {
            result.topTextResult.multiParagraph
                .paint(drawContext.canvas)
        }
        translate(
            top = result.topTextResult.size.height.toFloat()
        ) {
            result.bottomTextResult.multiParagraph
                .paint(drawContext.canvas)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun TextMeasurer.measureTextFlow(
    text: AnnotatedString,
    obstacleSize: IntSize,
    layoutWidth: Int,
    layoutHeight: Int,
    overflow: TextOverflow,
    softWrap: Boolean,
    maxLines: Int,
    mergedStyle: TextStyle,
    obstacleAlignment: TextFlowObstacleAlignment
): TextFlowCanvasLayoutResult {
    var topBlock = measure(text)
    var topBlockVisibleLineCount = 0
    var topBlockLastCharIndex = -1
    if(obstacleSize.height > 0) {
        topBlock = measure(
            text, mergedStyle,
            constraints = Constraints(
                maxWidth = if(obstacleAlignment != BottomEnd)
                    layoutWidth - obstacleSize.width
                else layoutWidth,
                maxHeight = MAX_VALUE
            ),
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
        )
        val lastVisibleLineIndex = topBlock
            .lastVisibleLineIndex(obstacleSize.height) //  <------------- BEDA BEDA BEDA
        topBlockVisibleLineCount = lastVisibleLineIndex + 1
        topBlockLastCharIndex = topBlock.getOffsetForPosition(
            Offset(
                topBlock.getLineRight(lastVisibleLineIndex),
                topBlock.getLineTop(lastVisibleLineIndex)
            )
        )
        topBlock = measure(
            text, mergedStyle,
            constraints = Constraints(
                maxWidth = if(obstacleAlignment != BottomEnd)
                    layoutWidth - obstacleSize.width
                else layoutWidth,
                maxHeight = topBlock
                    .getLineBottom(lastVisibleLineIndex).toInt()
            ),
            overflow = overflow,
            softWrap = softWrap,
            maxLines = topBlockVisibleLineCount,
        )
    }
    var bottomBlock = measure(
        text.subSequence(
            topBlockLastCharIndex + 1,
            text.length
        )
    )
    if(topBlockVisibleLineCount < maxLines
        && topBlockLastCharIndex < text.length
    ) {
        bottomBlock = measure(
            text.subSequence(
                topBlockLastCharIndex + 1,
                text.length
            ),
            mergedStyle,
            constraints = Constraints(
                maxWidth = if(obstacleAlignment != BottomEnd)
                    layoutWidth
                else layoutWidth - obstacleSize.width,
                maxHeight = MAX_VALUE
            ),
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines - topBlockVisibleLineCount,
        )
    }
    return TextFlowCanvasLayoutResult(topBlock, bottomBlock)
}

private fun TextLayoutResult.lastVisibleLineIndex(
    height: Int
): Int {
    for(it in 0..lineCount) {
        if(getLineBottom(it) > height) return it
    }
    repeat(lineCount) {
        if(getLineBottom(it) > height) return it
    }; return lineCount - 1
}

private data class TextFlowCanvasLayoutResult(
    val topTextResult: TextLayoutResult,
    val bottomTextResult: TextLayoutResult,
)