package ru.rikmasters.gilty.shared.common.transform

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import ru.rikmasters.gilty.core.log.log
import kotlin.math.absoluteValue

fun transformationOf(mask: String, endChar: String = "") = object: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while(mask.indices.filter { mask[it] != '#' }.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }; out += char; maskIndex++
        }
        return TransformedText(AnnotatedString(out + endChar),
            object: OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val offsetValue = offset.absoluteValue
                    if(offsetValue == 0) return 0
                    var numberOfHashtags = 0
                    val masked = mask.takeWhile {
                        if(it == '#') numberOfHashtags++
                        numberOfHashtags < offsetValue
                    }
                    return (masked.length + 1).also {
                        log.v("originalToTransformed($offset) => $it")
                    }
                }
                
                override fun transformedToOriginal(offset: Int): Int {
                    return mask.take(offset.absoluteValue).count { it == '#' }.also {
                        log.v("transformedToOriginal($offset) => $it")
                    }
                }
            }
        )
    }
}

fun numberMask(size: Int) =
    CharArray(size) { '#' }
        .concatToString()
        .chunked(3)
        .joinToString(" ")
        .reversed()