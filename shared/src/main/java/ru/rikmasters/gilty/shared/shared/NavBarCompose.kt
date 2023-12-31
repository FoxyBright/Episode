package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
fun NavBarPreview() {
    GiltyTheme {
        NavBar(
            listOf(
                INACTIVE, ACTIVE,
                INACTIVE, NEW_INACTIVE, INACTIVE
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
            .background(colorScheme.primaryContainer),
        SpaceEvenly, CenterVertically
    ) {
        state.forEachIndexed { i, state ->
            Item(
                icon = when(i) {
                    0 -> if(isSystemInDarkTheme())
                        if(state == ACTIVE)
                            R.drawable.ic_home_active_dark
                        else R.drawable.ic_home_dark
                    else
                        if(state == ACTIVE)
                            R.drawable.ic_home_active
                        else R.drawable.ic_home
                    
                    1 -> if(isSystemInDarkTheme()) when(state) {
                        ACTIVE -> R.drawable.ic_notification_active_dark
                        INACTIVE -> R.drawable.ic_notification_dark
                        NEW_INACTIVE -> R.drawable.ic_notification_indicator_dark
                        NEW_ACTIVE -> R.drawable.ic_notification_indicator_active_dark
                    } else when(state) {
                        ACTIVE -> R.drawable.ic_notification_active
                        INACTIVE -> R.drawable.ic_notification
                        NEW_INACTIVE -> R.drawable.ic_notification_indicator
                        NEW_ACTIVE -> R.drawable.ic_notification_indicator_active
                    }
                    
                    3 -> if(isSystemInDarkTheme()) when(state) {
                        ACTIVE -> R.drawable.ic_chat_active_dark
                        INACTIVE -> R.drawable.ic_chat_dark
                        NEW_INACTIVE -> R.drawable.ic_chat_indicator_dark
                        NEW_ACTIVE -> R.drawable.ic_chat_indicator_active_dark
                    } else when(state) {
                        ACTIVE -> R.drawable.ic_chat_active
                        INACTIVE -> R.drawable.ic_chat
                        NEW_INACTIVE -> R.drawable.ic_chat_indicator
                        NEW_ACTIVE -> R.drawable.ic_chat_indicator_active
                    }
                    
                    4 -> if(isSystemInDarkTheme()) {
                        if(state == ACTIVE)
                            R.drawable.ic_profile_active_dark
                        else R.drawable.ic_profile_dark
                    } else {
                        if(state == ACTIVE)
                            R.drawable.ic_profile_active
                        else R.drawable.ic_profile
                    }
                    
                    else -> R.drawable.ic_add
                },
                state = state,
                add = (i == 2),
                onClick = { onClick(i) }
            )
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
    if(add) Image(
        painter = painterResource(
            if(isSystemInDarkTheme())
                R.drawable.ic_add_button_day
            else R.drawable.ic_add_button
        ),
        contentDescription = null,
        modifier = Modifier
            .clickable { onClick() }
    ) else Card(
        onClick = onClick,
        shape = CircleShape,
        colors = cardColors(
            when(state) {
                ACTIVE, NEW_ACTIVE ->
                    colors.navBarActiveBackground
                else -> colorScheme.primaryContainer
            }
        )
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .padding(20.dp, 4.dp)
                .size(24.dp),
        )
    }
}