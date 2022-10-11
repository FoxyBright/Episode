package ru.rikmasters.gilty.presentation.ui.presentation.login

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.shared.CheckBox
import ru.rikmasters.gilty.presentation.ui.shared.Divider
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Composable
@ExperimentalMaterial3Api
fun PermissionsContent(modifier: Modifier, callback: NavigationInterface? = null) {
    var geopositionState by remember { mutableStateOf(true) }
    var notificationState by remember { mutableStateOf(false) }
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
                stringResource(R.string.find_more),
                stringResource(R.string.find_more_details),
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
                stringResource(R.string.permissions),
                Modifier.padding(bottom = 12.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(ThemeExtra.colors.elementsBack)
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp), Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.geoposition),
                            color = ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.buttonText
                        )
                        CheckBox(geopositionState) { geopositionState = it }
                    }
                    Divider(Modifier.padding(start = 16.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp), Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.notifications),
                            color = ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.buttonText
                        )
                        CheckBox(notificationState) { notificationState = it }
                    }
                }
            }
        }
        GradientButton(
            { callback?.onNext() },
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.finish)
        )
    }
}

@Composable
@ExperimentalMaterial3Api
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
                stringResource(R.string.answer_permission),
                Modifier.padding(bottom = 20.dp),
                ThemeExtra.colors.mainTextColor,
                textAlign = TextAlign.Center,
                style = ThemeExtra.typography.H3
            )
            Divider()
            Text(
                stringResource(R.string.when_using),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                fontWeight = FontWeight.Bold,
                style = ThemeExtra.typography.MediumText
            )
            Divider()
            Text(
                stringResource(R.string.once),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                fontWeight = FontWeight.Bold,
                style = ThemeExtra.typography.MediumText
            )
            Divider()
            Text(
                stringResource(R.string.prohibit),
                Modifier.padding(vertical = 20.dp),
                ThemeExtra.colors.primary,
                fontWeight = FontWeight.Bold,
                style = ThemeExtra.typography.MediumText
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
fun PermissionConfirmationWindowPreview() {
    GiltyTheme {
        PermissionConfirmationWindow()
    }

}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
fun PermissionsContentPreview() {
    GiltyTheme {
        PermissionsContent(Modifier)
    }
}

