package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
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
                    Modifier.padding(6.dp, 12.dp),
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
                    Modifier,
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

@Composable
fun GiltyChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    online: Boolean = false,
    onClick: () -> Unit,
) {
    val primary = colorScheme.primary
    val empty = colorScheme.primaryContainer
    val border = colors.chipGray
    
    
    Box(
        modifier = modifier
            .sur(
                shape = shapes.medium,
                backgroundColor = Transparent,
                border = BorderStroke(
                    1.dp, horizontalGradient(
                        if(isSelected) if(online) green()
                        else listOf(primary, primary)
                        else listOf(border, border)
                    )
                ),
            )
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                enabled = true,
                role = Role.Button,
                onClick = onClick
            ),
        propagateMinConstraints = true
    ) {
        Box(
            Modifier
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

private fun Modifier.sur(
    shape: Shape,
    backgroundColor: Color,
    border: BorderStroke?,
) = this
    .shadow(0.dp, shape, clip = false)
    .then(
        if(border != null)
            Modifier.border(border, shape)
        else Modifier
    )
    .background(backgroundColor, shape)
    .clip(shape)

@Composable
fun GiltyString(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clickable(
                MutableInteractionSource(),
                (null)
            ) { onClick() }) {
        Text(
            text, Modifier.padding(
                end = 10.dp, bottom = if(isSelected)
                    0.dp else 2.dp
            ), animateColorAsState(
                if(isSelected) colorScheme.tertiary
                else colorScheme.onTertiary, tween(600)
            ).value, style = typography.titleLarge.copy(
                fontSize = animateFloatAsState(
                    if(isSelected) 28f else 18f, tween(200)
                ).value.sp
            )
        )
    }
}

@Preview
@Composable
private fun GiltyTab() {
    GiltyTheme {
        GiltyTab(
            listOf("Ко всем", "К каждому", "Ко мне"),
            0, unEnabled = listOf(2)
        )
    }
}

@Composable
fun GiltyTab(
    tabs: List<String>,
    isSelected: Int,
    modifier: Modifier = Modifier,
    online: Boolean = false,
    unEnabled: List<Int> = emptyList(),
    onClick: ((Int) -> Unit)? = null,
) {
    Card(
        modifier, CircleShape,
        cardColors(Transparent), cardElevation(),
        BorderStroke(1.dp, colorScheme.onTertiary)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(Max)
        ) {
            repeat(tabs.size) { tab ->
                GiltyTabElement(
                    tabs[tab], Modifier.weight(1f),
                    (isSelected == tab), online,
                    !unEnabled.contains(tab)
                ) { onClick?.let { it(tab) } }
                if(tab < tabs.size - 1) Box(
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
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .background(
                animateColorAsState(
                    if(!enabled) colorScheme.background
                    else if(isSelected) if(online)
                        colors.tabActiveOnline
                    else colors.tabActive
                    else colors.tabInactive,
                    tween(400)
                ).value
            )
            .clickable { if(enabled) onClick() },
    ) {
        Text(
            text,
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            if(enabled) colorScheme.tertiary
            else colorScheme.onTertiary,
            style = typography.labelSmall,
            textAlign = TextAlign.Center,
            fontWeight = Bold
        )
    }
}