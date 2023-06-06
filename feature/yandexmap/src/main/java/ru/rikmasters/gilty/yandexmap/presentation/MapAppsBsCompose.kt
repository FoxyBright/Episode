package ru.rikmasters.gilty.yandexmap.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_google_maps
import ru.rikmasters.gilty.shared.R.drawable.ic_yandex_maps
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun MapAppsBs(
    alert: Boolean, appName: Int,
    modifier: Modifier = Modifier,
    alertDismiss: (Boolean) -> Unit,
    onClick: (Int) -> Unit,
) {
    val bsName =
        stringResource(R.string.meeting_maps_open_with)
    
    val appNames = listOf(
        stringResource(R.string.maps_google),
        stringResource(R.string.maps_yandex)
    )
    
    val appNamesSpace = listOf(
        stringResource(R.string.maps_google_space),
        stringResource(R.string.maps_yandex_space)
    )
    
    Box(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 19.dp
                ),
            Alignment.Center
        ) { Grip() }
    }
    Element(
        item = FilterModel(bsName) {
            Box(
                Modifier.padding(
                    top = 10.dp,
                    bottom = 40.dp
                )
            ) {
                LazyRow {
                    iconItem(
                        modifier = Modifier
                            .padding(end = 26.dp),
                        image = ic_google_maps,
                        label = appNames.first(),
                    ) { onClick(0) }
                    iconItem(
                        image = ic_yandex_maps,
                        label = appNames.last(),
                    ) { onClick(1) }
                }
            }
        },
        modifier = Modifier.background(
            colorScheme.background
        )
    )
    
    GAlert(
        show = alert,
        title = stringResource(R.string.meeting_maps_alert_title),
        label = stringResource(
            R.string.meeting_maps_alert_label,
            appNamesSpace[appName]
        ),
        success = Pair(stringResource(R.string.meeting_maps_alert_open)) {
            alertDismiss(true)
        },
        cancel = Pair(stringResource(R.string.cancel_button)) {
            alertDismiss(false)
        }
    )
}

@Composable
private fun Grip(
    modifier: Modifier = Modifier,
    color: Color = ThemeExtra.colors.gripColor,
) {
    Box(
        modifier
            .size(40.dp, 5.dp)
            .background(color, CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.iconItem(
    modifier: Modifier = Modifier,
    image: Int, label: String,
    onClick: () -> Unit,
) {
    item {
        Card(
            onClick = onClick,
            shape = shapes.large,
            colors = cardColors(Transparent)
        ) {
            Column(
                modifier, Top,
                CenterHorizontally
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(top = 8.dp),
                    style = typography.bodyMedium.copy(
                        color = colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }
    }
}