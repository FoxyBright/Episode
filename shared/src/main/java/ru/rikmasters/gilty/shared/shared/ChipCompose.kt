package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.rikmasters.gilty.shared.common.extentions.toSp
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
fun GChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    isOnline: Boolean = false,
    onlineGradient: Boolean = true,
    primary: Color = colorScheme.primary,
    secondary: Color = colorScheme.secondary,
    empty: Color = colorScheme.primaryContainer,
    border: Color = colors.chipGray,
    textModifier:Modifier = Modifier,
    onClick: () -> Unit,
) {
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
                text, modifier = Modifier.padding(16.dp, 6.dp).then(textModifier),
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
    val tween = 400
    val offset = animateDpAsState(
        if(isSelected) 0.dp else -(3).dp,
        tween(tween)
    ).value
    
    val color = animateColorAsState(
        targetValue = if(isSelected)
            colorScheme.tertiary
        else colorScheme.onTertiary,
        animationSpec = tween(tween)
    ).value
    
    val size = animateDpAsState(
        targetValue = if(isSelected) 28.dp else 18.dp,
        animationSpec = tween(tween)
    ).value.toSp()
    
    Box(
        modifier
            .clickable(
                MutableInteractionSource(),
                (null)
            ) { onClick() }) {
        Text(
            text = text,
            modifier = Modifier
                .padding(end = 10.dp)
                .offset(y = offset),
            color = color,
            style = typography.titleLarge.copy(
                fontSize = size
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
                        if(unEnabled.contains(tab)) colorScheme.background
                        else if((selectedTab == tab)) if(online)
                            colors.tabActiveOnline
                        else colors.tabActive
                        else colors.tabInactive,
                        tween(400)
                    ).value,
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
fun GiltyPagerTab(
    modifier: Modifier,
    selectedTabIndex: Int,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    titles: List<String>,
    onTabClick: (Int) -> Unit,
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
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .zIndex(2f)
                        .border(
                            width = if(selectedTabIndex == 1)
                                0.5.dp else 0.dp,
                            color = colorScheme.scrim,
                            shape = RoundedCornerShape(0.dp)
                        ),
                    isSelected = selectedTabIndex == index,
                    online = false,
                    enabled = true
                ) { onTabClick(index) }
            }
        }
    }
}

@Composable
fun GiltyTabElement(
    text: String,
    modifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER")
    isSelected: Boolean = false,
    @Suppress("UNUSED_PARAMETER")
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
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            color = animateColorAsState(
                targetValue = if(enabled)
                    colorScheme.tertiary
                else colorScheme.onTertiary,
                animationSpec = tween(
                    easing = LinearEasing
                ),
            ).value,
            style = typography.labelSmall,
            textAlign = Center,
            fontWeight = Bold
        )
    }
}