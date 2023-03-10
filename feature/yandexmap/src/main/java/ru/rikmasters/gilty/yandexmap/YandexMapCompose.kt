package ru.rikmasters.gilty.yandexmap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory.decodeResource
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider.fromBitmap
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.yandexmap.R.id.mapview
import ru.rikmasters.gilty.yandexmap.R.layout.map_layout


data class YandexMapState(
    val location: LocationModel,
    val mapKit: MapKit,
    val userVisible: Boolean,
)

interface YandexMapCallback {
    
    fun onBack()
    fun getRoute()
}

@Composable
fun YandexMapContent(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    callback: YandexMapCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        TopBar(Modifier) { callback?.onBack() }
        Map(
            state, Modifier
                .fillMaxHeight(
                    if(state.location.hide!!)
                        0.8f else 0.72f
                )
                .offset(y = 24.dp)
        )
        Bottom(
            state.location.hide!!,
            state.location.address!!,
            state.location.place!!, Modifier
        ) { callback?.getRoute() }
    }
}

@SuppressLint("InflateParams")
@Composable
private fun Map(
    state: YandexMapState,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            
            val inflater = LayoutInflater
                .from(context)
                .inflate(map_layout, null, false)
            
            val map = inflater.findViewById<MapView>(mapview)
            
            val point = Point(
                state.location.lat!!,
                state.location.lng!!
            )
            
            map.map.move(
                CameraPosition(
                    point, (14f), (0f), (0f)
                ), Animation(SMOOTH, (1f)), null
            )
            
            val obj = map.map.mapObjects
            
            if(state.location.hide == true) obj.addCircle(
                Circle(point, 1000f),
                Color(0xFFFF4745).toArgb(), 4f,
                Color(0x1AFF4745).toArgb(),
            ).let {
                it.zIndex = 100f
            } else obj.addPlacemark(
                point, fromBitmap(
                    decodeResource(
                        context.resources,
                        R.drawable.ic_location
                    )
                )
            )
            
            requestLocationPermissions(context)
            
            state.mapKit.createUserLocationLayer(
                map.mapWindow
            ).isVisible = state.userVisible
            
            state.mapKit.onStart()
            
            return@AndroidView map
        },
        
        update = {}
    )
}

private fun requestLocationPermissions(context: Context) {
    if(checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
        && checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
    ) ActivityCompat.requestPermissions(
        context as Activity, arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        ), (0)
    )
    return
}

@Composable
private fun Bottom(
    hide: Boolean,
    address: String,
    place: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .background(
                colorScheme.background,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 19.dp),
            Alignment.Center
        ) {
            Box(
                modifier
                    .size(40.dp, 5.dp)
                    .background(
                        colorScheme.outline,
                        RoundedCornerShape(50)
                    )
            )
        }
        Card(
            Modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = cardColors(colorScheme.primaryContainer)
        ) {
            if(hide) Text(
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
                        address, Modifier.padding(top = 8.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineSmall,
                        maxLines = 1,
                        overflow = Ellipsis
                    )
                    Text(
                        place, Modifier.padding(
                            top = 2.dp, bottom = 10.dp
                        ), colorScheme.tertiary,
                        style = typography.bodyMedium,
                        maxLines = 1,
                        overflow = Ellipsis
                    )
                }
        }
        if(!hide) GradientButton(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            stringResource(R.string.map_get_route),
        ) { onClick() }
    }
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

@Composable
fun MapApps(
    alert: Boolean,
    appName: Int,
    modifier: Modifier = Modifier,
    alertDismiss: (Boolean) -> Unit,
    onClick: (Int) -> Unit,
) {
    Element(
        FilterModel(
            stringResource(R.string.meeting_maps_open_with)
        ) {
            Box(Modifier.padding(top = 10.dp, bottom = 40.dp)) {
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
                            Modifier, R.drawable.ic_yandex_maps,
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
        success = Pair(stringResource(R.string.meeting_maps_alert_open)) { alertDismiss(true) },
        cancel = Pair(stringResource(R.string.cancel_button)) { alertDismiss(false) },
        label = stringResource(
            R.string.meeting_maps_alert_label,
            if(appName == 0) "Google Maps" else "Yandex Maps"
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
        Column(modifier, Top, CenterHorizontally) {
            Image(
                painterResource(image),
                (null), Modifier.size(60.dp)
            )
            Text(
                label, Modifier.padding(top = 8.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}