package ru.rikmasters.gilty.login.presentation.ui.permissions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
fun PermissionsContentPreview() {
    GiltyTheme {
        PermissionsContent(PermissionsState())
    }
}

data class PermissionsState(
    val geopositionState: Boolean = false,
    val notificationState: Boolean = false
)

interface PermissionsCallback : NavigationInterface {
    fun geopositionChange() {}

    fun notificationChange() {}
}

@Composable
fun PermissionsContent(
    state: PermissionsState,
    modifier: Modifier = Modifier,
    callback: PermissionsCallback? = null
) {
    Box(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ActionBar(
                stringResource(R.string.permissions_action_bar),
                stringResource(R.string.permissions_action_bar_details),
            ) { callback?.onBack() }
            Image(
                painterResource(R.drawable.map),
                stringResource(R.string.map),
                Modifier
                    .fillMaxWidth()
                    .padding(38.dp, 22.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                stringResource(R.string.permissions_title),
                Modifier.padding(bottom = 12.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PermItem(
                    stringResource(R.string.permission_geoposition_label),
                    0, state.geopositionState
                )
                { callback?.geopositionChange() }
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
        ) { callback?.onNext() }
    }
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
        { onClick() },
        Modifier.fillMaxWidth(), (true),
        LazyItemsShapes(index, 2),
        CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            Arrangement.SpaceBetween
        ) {
            Text(
                name, Modifier,
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal
            )
            CheckBox(state) { onClick() }
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun PermissionConfirmationWindowPreview() {
    GiltyTheme {
        PermissionConfirmationWindow()
    }
}

@Composable
fun PermissionConfirmationWindow() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(ThemeExtra.colors.elementsBack)
            .clip(MaterialTheme.shapes.large)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.geolocation_icon),
                stringResource(R.string.geolocation_icon),
                Modifier
                    .size(60.dp)
                    .padding(18.dp)
            )
            Text(
                stringResource(R.string.permissions_answer),
                Modifier.padding(bottom = 20.dp),
                MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Divider()
            Text(
                stringResource(R.string.permissions_when_using_button),
                Modifier.padding(vertical = 20.dp),
                MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
            Divider()
            Text(
                stringResource(R.string.permissions_once_button),
                Modifier.padding(vertical = 20.dp),
                MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
            Divider()
            Text(
                stringResource(R.string.permissions_prohibit_button),
                Modifier.padding(vertical = 20.dp),
                MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

