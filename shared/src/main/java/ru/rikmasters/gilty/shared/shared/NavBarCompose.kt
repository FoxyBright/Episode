package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_add
import ru.rikmasters.gilty.shared.R.drawable.ic_chat
import ru.rikmasters.gilty.shared.R.drawable.ic_home
import ru.rikmasters.gilty.shared.R.drawable.ic_notification
import ru.rikmasters.gilty.shared.R.drawable.ic_profile
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
fun NavBarPreview() {
    GiltyTheme {
        NavBar(listOf(INACTIVE, ACTIVE, INACTIVE, NEW, INACTIVE)) {}
    }
}

@Composable
fun NavBar(
    state: List<NavIconState>,
    modifier: Modifier = Modifier,
    onClick: (point: Int) -> Unit
) {
    val iconList = listOf(
        ic_home, ic_notification,
        ic_add, ic_chat, ic_profile
    )
    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                colorScheme.primaryContainer,
                ThemeExtra.shapes.ultraTopRoundedShape
            ),
        Arrangement.SpaceEvenly,
        Alignment.CenterVertically
    ) {
        state.forEachIndexed { i, state ->
            Item(iconList[i], state, (i == 2))
            { onClick(i) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    icon: Int,
    state: NavIconState = INACTIVE,
    add: Boolean = false,
    onClick: () -> Unit
) {
    if (add) AddButton(icon, onClick)
    else
        Box {
            Card(
                onClick, shape = CircleShape,
                colors = CardDefaults.cardColors(
                    if (state == ACTIVE) colors.navBarActiveBackground
                    else colorScheme.primaryContainer
                )
            )
            {
                Icon(
                    painterResource(icon),
                    (null), Modifier
                        .padding(24.dp, 8.dp)
                        .size(24.dp),
                    if (state == ACTIVE) colors.navBarActive
                    else colors.navBarInactive
                )
            }
            if (state == NEW) Point(
                Modifier
                    .align(TopEnd)
                    .padding(top = 10.dp, end = 22.dp)
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddButton(icon: Int, onClick: () -> Unit) {
    Card(
        onClick, shape = shapes.extraSmall,
        colors = CardDefaults.cardColors(colors.navBarAddButton),
    ) {
        Icon(
            painterResource(icon),
            (null), Modifier
                .padding(14.dp, 8.dp)
                .size(10.dp),
            colorScheme.primaryContainer
        )
    }
}

@Composable
private fun Point(modifier: Modifier) {
    Box(
        modifier
            .size(12.dp)
            .background(
                colorScheme.primaryContainer,
                CircleShape
            ), Center
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(
                    colorScheme.primary,
                    CircleShape
                )
        )
    }
}

