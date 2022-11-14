package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.absoluteValue

fun textMask(mask: String) = object : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (mask.indices.filter { mask[it] != '#' }.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }; out += char; maskIndex++
        }
        return TransformedText(AnnotatedString(out), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val offsetValue = offset.absoluteValue
                if (offsetValue == 0) return 0
                var numberOfHashtags = 0
                val masked = mask.takeWhile {
                    if (it == '#') numberOfHashtags++
                    numberOfHashtags < offsetValue
                }
                return masked.length + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                return mask.take(offset.absoluteValue).count { it == '#' }
            }
        })
    }
}

fun numberMask(size: Int): String {
    var mask = ""
    val check = check(size)
    repeat((size - check.length) / 3)
    { mask += "### " }; return "$check $mask"
}

private fun check(size: Int): String {
    repeat(size) {
        when (size) {
            1 + 3 * (it) -> return "#"
            2 + 3 * (it) -> return "##"
        }
    }; return "###"
}