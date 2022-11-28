package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

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

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun StringPreview() {
    GiltyTheme {
        val list = remember { mutableStateListOf(true, false) }
        val textList = listOf("Сегодня", "Потом")
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            list.forEachIndexed { i, it ->
                GiltyString(
                    Modifier.padding(end = 12.dp),
                    textList[i], it
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
        colorScheme.primary
    else
        colorScheme.primaryContainer

    val borderColorTarget = if (isSelected)
        colorScheme.primary
    else
        colors.chipGray

    val textColorTarget = if (isSelected)
        Color.White
    else
        colorScheme.tertiary

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
            text, Modifier.padding(16.dp, 6.dp), textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun GiltyString(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier
            .clip(CircleShape)
            .clickable { onClick() }) {
        Text(
            text,
            Modifier.padding(4.dp),
            animateColorAsState(
                if (isSelected) colorScheme.tertiary
                else colorScheme.onTertiary, tween(600)
            ).value, style = MaterialTheme.typography.titleLarge.copy(
                fontSize = animateIntAsState(
                    if (isSelected) 28 else 18, tween(200)
                ).value.sp
            )
        )
    }
}

@Preview
@Composable
private fun GiltyTab() {
    val list = remember { mutableStateListOf(true, false, false, false) }
    GiltyTheme {
        GiltyTab(listOf("Ко всем", "К каждому", "Ко мне"), list) {
            repeat(list.size) { index ->
                list[index] = it == index
            }
        }
    }
}

@Composable
fun GiltyTab(
    tabs: List<String>,
    isSelected: List<Boolean>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterVertically
    ) {
        repeat(tabs.size) {
            GiltyTabElement(
                tabs[it], LazyRowItemsShapes(it, tabs.size),
                Modifier
                    .fillMaxWidth()
                    .weight(1f), isSelected[it]
            ) { onClick(it) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GiltyTabElement(
    text: String, shape: Shape,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier.fillMaxWidth(), (true),
        shape, CardDefaults.cardColors(
            animateColorAsState(
                if (isSelected) colors.tabActive
                else colors.tabInactive, tween(400)
            ).value
        ),
        border = BorderStroke(1.dp, colorScheme.onTertiary)
    ) {
        Box(Modifier.fillMaxWidth(), Center) {
            Text(
                text, Modifier.padding(10.dp),
                colorScheme.tertiary, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}