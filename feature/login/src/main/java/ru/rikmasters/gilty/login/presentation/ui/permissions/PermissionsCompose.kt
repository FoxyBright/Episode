package ru.rikmasters.gilty.login.presentation.ui.permissions

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.animated.AnimatedImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.map
import ru.rikmasters.gilty.shared.R.raw.find_more
import ru.rikmasters.gilty.shared.R.raw.find_more_night
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
@Preview
fun PermissionsContentPreview() {
    GiltyTheme {
        PermissionsContent(
            PermissionsState(true),
            Modifier.background(colorScheme.background)
        )
    }
}

data class PermissionsState(
    val geopositionState: Boolean = false,
    val notificationState: Boolean = false
)

interface PermissionsCallback {
    
    fun onBack()
    fun onComplete()
    fun requestPermission()
    fun notificationChange()
}

@Composable
fun PermissionsContent(
    state: PermissionsState,
    modifier: Modifier = Modifier,
    callback: PermissionsCallback? = null
) {
    Box(modifier.fillMaxSize()) {
        Column {
            ActionBar(
                stringResource(R.string.permissions_action_bar),
                stringResource(R.string.permissions_action_bar_details),
            ) { callback?.onBack() }
            MapImage()
            Text(
                stringResource(R.string.permissions_title),
                Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 16.dp),
                colorScheme.tertiary,
                style = typography.titleLarge
            )
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(colorScheme.background)
            ) {
                PermItem(
                    stringResource(R.string.permission_geoposition_label),
                    0, state.geopositionState
                ) { callback?.requestPermission() }
                Divider(Modifier.padding(start = 16.dp))
                PermItem(
                    stringResource(R.string.notification_screen_name),
                    1, state.notificationState
                ) { callback?.notificationChange() }
            }
        }
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.permissions_finish_button)
        ) { callback?.onComplete() }
    }
}

@Composable
private fun MapImage() {
    val mod = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(38.dp, 22.dp)
    if(LocalInspectionMode.current) Image(
        painterResource(map),
        (null), mod, contentScale = Fit
    )
    else AnimatedImage(
        if(isSystemInDarkTheme())
            find_more_night
        else find_more, mod
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermItem(
    name: String,
    index: Int,
    state: Boolean,
    onClick: () -> Unit
) {
    Card(
        { onClick() }, Modifier, (true),
        LazyItemsShapes(index, 2),
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = if(state) 16.dp else 20.dp
                ), SpaceBetween, CenterVertically
        ) {
            Text(
                name, Modifier.padding(vertical = 16.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = Normal
            )
            CheckBox(
                state, Modifier
                    .size(if(state) 32.dp else 24.dp)
                    .clip(CircleShape)
            ) { onClick() }
        }
    }
}