package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
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
        NavBar(
            listOf(
                INACTIVE, ACTIVE,
                INACTIVE, NEW, INACTIVE
            )
        ) {}
    }
}

@Composable
fun NavBar(
    state: List<NavIconState>,
    modifier: Modifier = Modifier,
    onClick: (point: Int) -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(
                colorScheme.primaryContainer,
                ThemeExtra.shapes.ultraTopRoundedShape
            ),
        Arrangement.SpaceEvenly,
        Alignment.CenterVertically
    ) {
        state.forEachIndexed { i, state ->
            Item(
                when(i) {
                    0 -> if(state == ACTIVE)
                        R.drawable.ic_home_active
                    else R.drawable.ic_home
                    
                    1 -> when(state) {
                        ACTIVE -> R.drawable.ic_notification_active
                        INACTIVE -> R.drawable.ic_notification
                        NEW -> R.drawable.ic_notification_indicator
                    }
                    
                    3 -> when(state) {
                        ACTIVE -> R.drawable.ic_chat_active
                        INACTIVE -> R.drawable.ic_chat
                        NEW -> R.drawable.ic_chat_indicator
                    }
                    
                    4 -> if(state == ACTIVE)
                        R.drawable.ic_profile_active
                    else R.drawable.ic_profile
                    
                    else -> R.drawable.ic_add
                }, state, (i == 2)
            )
            { onClick(i) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    icon: Int,
    state: NavIconState,
    add: Boolean = false,
    onClick: () -> Unit,
) {
    if(add) AddButton(icon, onClick)
    else Card(
        onClick, shape = CircleShape,
        colors = cardColors(
            if(state == ACTIVE)
                colors.navBarActiveBackground
            else colorScheme.primaryContainer
        )
    ) {
        Image(
            painterResource(icon),
            (null), Modifier
                .padding(24.dp, 8.dp)
                .size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddButton(icon: Int, onClick: () -> Unit) {
    Card(
        onClick, shape = shapes.extraSmall,
        colors = cardColors(colors.navBarAddButton),
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