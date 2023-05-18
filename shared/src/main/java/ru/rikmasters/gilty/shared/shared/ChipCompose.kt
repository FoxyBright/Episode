package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.zIndex
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun ChipsPreview() {
    @Composable
    fun chip(
        state: Boolean,
        online: Boolean,
        gradient: Boolean,
    ) = GChip(
        Modifier.padding(6.dp, 12.dp),
        "Чип", state, online, gradient,
    ) {}
    GiltyTheme {
        Row(Modifier.fillMaxWidth()) {
            chip((false), (false), (false))
            chip((true), (false), (false))
            chip((true), (true), (true))
            chip((true), (true), (false))
        }
    }
}

@Preview
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
                    for (i1 in 0..list.lastIndex) {
                        list[i1] = false
                    }
                    list[i] = true
                }
            }
        }
    }
}

@Composable
fun GChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    isOnline: Boolean = false,
    onlineGradient: Boolean = true,
    backgroundColor: Color = colorScheme.primaryContainer,
    onClick: () -> Unit,
) {
    val primary = colorScheme.primary
    val secondary = colorScheme.secondary
    val empty = backgroundColor
    val border = colors.chipGray
    Box(
        modifier = modifier
            .sur(
                shape = shapes.extraSmall,
                backgroundColor = Transparent,
                border = BorderStroke(
                    1.dp, horizontalGradient(
                        when {
                            !isSelected -> listOf(border, border)
                            !isOnline -> listOf(primary, primary)
                            !onlineGradient -> listOf(secondary, secondary)
                            else -> green()
                        }
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
                        when {
                            !isSelected -> listOf(empty, empty)
                            !isOnline -> listOf(primary, primary)
                            !onlineGradient -> listOf(secondary, secondary)
                            else -> green()
                        }
                    )
                )
        ) {
            Text(
                text, Modifier.padding(16.dp, 6.dp),
                if (isSelected) White
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
        if (border != null)
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
                end = 10.dp, bottom = if (isSelected)
                    0.dp else 2.dp
            ), animateColorAsState(
                if (isSelected) colorScheme.tertiary
                else colorScheme.onTertiary, tween(600)
            ).value, style = typography.titleLarge.copy(
                fontSize = animateFloatAsState(
                    if (isSelected) 28f else 18f, tween(200)
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
            0, unEnabled = listOf(1, 2)
        )
    }
}

@Composable
fun GiltyTab(
    tabs: List<String>,
    selectedTab: Int,
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
                    (selectedTab == tab), online,
                    !unEnabled.contains(tab),
                    backgroundColor = animateColorAsState(
                        if (unEnabled.contains(tab)) colorScheme.background
                        else if ((selectedTab == tab)) if (online)
                            colors.tabActiveOnline
                        else colors.tabActive
                        else colors.tabInactive,
                        tween(400)
                    ).value,
                ) { onClick?.let { it(tab) } }
                if (tab < tabs.size - 1) Box(
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
fun GiltyPagerTab(
    modifier: Modifier,
    selectedTabIndex: Int,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    titles: List<String>,
    onTabClick:(Int)->Unit,
) {
    TabRow(
        modifier = modifier,
        contentColor = White,
        selectedTabIndex = selectedTabIndex,
        indicator = indicator,
        containerColor = Transparent
    ) {
        titles.forEachIndexed { index, title ->
            Row {
                GiltyTabElement(
                    title,
                    Modifier
                        .weight(1f)
                        .zIndex(2f)
                        .border(
                            if (selectedTabIndex == 1) 0.5.dp else 0.dp, colorScheme.scrim,
                            RoundedCornerShape(0.dp)
                        ),
                    (selectedTabIndex == index), false,
                    true
                ) {
                    onTabClick(index)
                }
            }
        }
    }

}

@Composable
fun GiltyTabElement(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    online: Boolean,
    enabled: Boolean,
    backgroundColor: Color = Transparent,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .background(backgroundColor)
            .clickable { onClick() },
    ) {
        Text(
            text,
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            color = animateColorAsState(
                targetValue = if (enabled) {
                    colorScheme.tertiary
                } else {
                    colorScheme.onTertiary
                },
                animationSpec = tween(easing = LinearEasing),
            ).value,
            style = typography.labelSmall,
            textAlign = TextAlign.Center,
            fontWeight = Bold
        )
    }
}