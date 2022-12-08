package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.theme.Gradients.green
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
                    for(i1 in 0..list.lastIndex) {
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
                    for(i1 in 0..list.lastIndex) {
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
    online: Boolean = false,
    onClick: () -> Unit
) {
    val primary = colorScheme.primary
    val empty = colorScheme.primaryContainer
    val border = colors.chipGray
    Card(
        onClick, modifier, (true),
        shapes.large, cardColors(Transparent),
        cardElevation(0.dp),
        BorderStroke(
            1.dp, horizontalGradient(
                if(isSelected) if(online) green()
                else listOf(primary, primary)
                else listOf(border, border)
            )
        ), MutableInteractionSource()
    ) {
        Box(
            Modifier
                .wrapContentSize()
                .background(
                    horizontalGradient(
                        if(isSelected) if(online) green()
                        else listOf(primary, primary)
                        else listOf(empty, empty)
                    )
                )
        ) {
            Text(
                text, Modifier.padding(16.dp, 6.dp),
                if(isSelected) White
                else colorScheme.tertiary,
                fontWeight = SemiBold,
                style = typography.labelSmall
            )
        }
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
                if(isSelected) colorScheme.tertiary
                else colorScheme.onTertiary, tween(600)
            ).value, style = typography.titleLarge.copy(
                fontSize = animateIntAsState(
                    if(isSelected) 28 else 18, tween(200)
                ).value.sp
            )
        )
    }
}

@Preview
@Composable
private fun GiltyTab() {
    val list =
        remember { mutableStateListOf(true, false, false, false) }
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
    online: Boolean = false,
    onClick: (Int) -> Unit
) {
    Card(
        modifier, CircleShape,
        cardColors(Transparent), cardElevation(),
        BorderStroke(1.dp, colorScheme.onTertiary)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)) {
            repeat(tabs.size) {
                GiltyTabElement(
                    tabs[it], Modifier.weight(1f),
                    isSelected[it], online
                ) { onClick(it) }
                if(it < tabs.size - 1) Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(colorScheme.onTertiary),
                )
            }
        }
    }
}

@Composable
private fun GiltyTabElement(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    online: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier
            .background(
                animateColorAsState(
                    if(isSelected)
                        if(online) colors.tabActiveOnline
                        else colors.tabActive
                    else colors.tabInactive,
                    tween(400)
                ).value
            )
            .clickable { onClick() },
    ) {
        Text(
            text,
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colorScheme.tertiary, fontWeight = Bold,
            textAlign = TextAlign.Center,
            style = typography.labelSmall
        )
    }
}