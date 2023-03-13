package ru.rikmasters.gilty.yandexmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.shared.shared.GAlert

@Composable
fun MapApps(
    alert: Boolean, appName: Int,
    modifier: Modifier = Modifier,
    alertDismiss: (Boolean) -> Unit,
    onClick: (Int) -> Unit,
) {
    Element(
        FilterModel(
            stringResource(R.string.meeting_maps_open_with)
        ) {
            Box(
                Modifier.padding(
                    top = 10.dp,
                    bottom = 40.dp
                )
            ) {
                LazyRow(Modifier) {
                    item {
                        IconItem(
                            Modifier.padding(end = 26.dp),
                            R.drawable.ic_google_maps,
                            stringResource(R.string.maps_google),
                        ) { onClick(0) }
                    }
                    item {
                        IconItem(
                            Modifier,
                            R.drawable.ic_yandex_maps,
                            stringResource(R.string.maps_yandex),
                        ) { onClick(1) }
                    }
                }
            }
        }, modifier.padding(top = 28.dp)
    )
    GAlert(
        show = alert,
        modifier = Modifier,
        success = Pair(stringResource(R.string.meeting_maps_alert_open))
        { alertDismiss(true) },
        cancel = Pair(stringResource(R.string.cancel_button))
        { alertDismiss(false) },
        label = stringResource(
            R.string.meeting_maps_alert_label,
            if(appName == 0) "Google Maps"
            else "Yandex Maps"
        ), title = stringResource(R.string.meeting_maps_alert_title)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun IconItem(
    modifier: Modifier = Modifier,
    image: Int, label: String,
    onClick: () -> Unit,
) {
    Card(
        onClick, Modifier,
        shape = shapes.large,
        colors = cardColors(Transparent)
    ) {
        Column(
            modifier, Top,
            CenterHorizontally
        ) {
            Image(
                painterResource(image),
                (null), Modifier.size(60.dp)
            )
            Text(
                label, Modifier.padding(top = 8.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
                textAlign = Center
            )
        }
    }
}