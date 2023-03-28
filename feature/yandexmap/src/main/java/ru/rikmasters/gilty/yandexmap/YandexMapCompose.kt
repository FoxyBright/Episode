package ru.rikmasters.gilty.yandexmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.yandex.mapkit.MapKit
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.shared.GradientButton


data class YandexMapState(
    val mapKit: MapKit,
    val location: LocationModel? = null,
    val userVisible: Boolean = false,
    val address: String? = null,
    val place: String? = null,
    val categoryName: String? = null,
)

interface YandexMapCallback {
    
    fun onBack()
    fun getRoute()
    fun onMarkerClick(meetPlace: MeetPlace)
}

@Composable
fun YandexMapContent(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    callback: YandexMapCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        TopBar(Modifier) { callback?.onBack() }
        MapContent(
            state, Modifier
                .fillMaxHeight(
                    if(state.location?.hide == true)
                        0.8f else 0.72f
                )
                .offset(y = 24.dp),
            callback
        )
        Bottom(state, Modifier)
        { callback?.getRoute() }
    }
}

@Composable
private fun Bottom(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .background(
                colorScheme.background,
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 19.dp
                ), Center
        ) { Grip() }
        Card(
            Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = cardColors(colorScheme.primaryContainer)
        ) {
            if(state.location?.hide == true) Text(
                stringResource(R.string.add_meet_detailed_meet_place_place_holder),
                Modifier.padding(16.dp, 12.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
            ) else
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        state.address ?: "", Modifier.padding(top = 8.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineSmall,
                        maxLines = 1,
                        overflow = Ellipsis
                    )
                    Text(
                        state.place ?: "", Modifier.padding(
                            top = 2.dp, bottom = 10.dp
                        ), colorScheme.tertiary,
                        style = typography.bodyMedium,
                        maxLines = 1,
                        overflow = Ellipsis
                    )
                }
        }
        if(state.location?.hide != true) GradientButton(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            stringResource(
                if(state.categoryName.isNullOrBlank())
                    R.string.map_get_route
                else R.string.select_button
            ),
        ) { onClick() }
    }
}

@Composable
private fun Grip(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.outline,
) {
    Box(
        modifier
            .size(40.dp, 5.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun TopBar(
    modifier: Modifier,
    onBack: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(16.dp, top = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Arrangement.Start, CenterVertically
        ) {
            IconButton(
                { onBack() },
                Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    (null), Modifier, colorScheme.tertiary
                )
            }
            Text(
                stringResource(R.string.map),
                Modifier, colorScheme.tertiary,
                style = typography.labelLarge,
            )
        }
    }
}