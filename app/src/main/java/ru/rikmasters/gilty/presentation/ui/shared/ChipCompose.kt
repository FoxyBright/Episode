package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ChipsPreview() {
    GiltyTheme {
        val list = remember { mutableStateListOf(true, false, false) }
        LazyRow(Modifier.fillMaxWidth()) {
            itemsIndexed(list) { i, it ->
                GiltyChip(
                    Modifier.padding(end = 12.dp),
                    "Чип ", it
                ) {
                    for (i1 in 0..list.lastIndex) {
                        list[i1] = false
                    }
                    list[i] = true
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiltyChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {

    val backgroundColorTarget = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        ThemeExtra.colors.cardBackground

    val borderColorTarget = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        ThemeExtra.colors.chipGray

    val textColorTarget = if (isSelected)
        Color.White
    else
        ThemeExtra.colors.mainTextColor

    val backgroundColor by animateColorAsState(backgroundColorTarget)
    val borderColor by animateColorAsState(borderColorTarget)
    val textColor by animateColorAsState(textColorTarget)

    Surface(
        onClick,
        modifier = modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.large,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        interactionSource = MutableInteractionSource()
    ) {
        Text(
            text,
            Modifier.padding(vertical = 6.dp, horizontal = 16.dp),
            textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}