package ru.rikmasters.gilty.yandexmap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory.decodeResource
import android.location.Location
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.VisibleRegionUtils.toPolygon
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory.getInstance
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType.ONLINE
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider.fromBitmap
import com.yandex.runtime.image.ImageProvider.fromResource
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import ru.rikmasters.gilty.yandexmap.R.layout.map_layout

data class MeetPlace(
    val lat: Double,
    val lng: Double,
    val place: String,
    val address: String,
)

@Composable
fun MapContent(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    callback: YandexMapCallback? = null,
) {
    Box(modifier) {
        AndroidView(
            { context ->
                val properties = LayoutInflater
                    .from(context)
                    .inflate(map_layout, (null), (false))
                    .findViewById<MapView>(R.id.mapview)
                    .getProperties(state)
                
                properties.map.moveCamera(properties.camPosition)
                
                if(!state.categoryName.isNullOrBlank())
                    properties.getPoints(context, callback!!)
                else
                    (properties.obj to properties.point)
                        .let { (obj, it) ->
                            if(state.location?.hide != false) obj.addMapCircle(it)
                            else obj.addMarker(it, context, ic_location)
                        }
                
                context.reqPermissions()
                
                state.mapKit.userLocation(properties.map, state.userVisible)
                
                state.mapKit.onStart()
                properties.map
            },
            Modifier.fillMaxWidth(),
        )
        if(!state.categoryName.isNullOrBlank()) Box(
            Modifier
                .height(80.dp)
                .align(Center)
        ) {
            Image(
                painterResource(ic_pin),
                (null), Modifier
                    .size(42.dp)
                    .align(TopCenter)
            )
        }
    }
}

fun MapObjectCollection.addMarkerWithListener(
    map: MapView,
    meetPlace: MeetPlace,
    context: Context,
    position: CameraPosition,
): PlacemarkMapObject {
    val point = Point(meetPlace.lat, meetPlace.lng)
    val marker = addPlacemark(point)
    marker.changeIcon(ic_map_point, context)
    marker.addTapListener { _, _ ->
        map.moveCamera(setCamPosition(point))
        if(distance(point, position.target) <= 150)
            vibrate(context)
        true
    }
    return marker
}

private fun distance(start: Point, end: Point): Float {
    val array = FloatArray(2)
    Location.distanceBetween(
        start.latitude, start.longitude,
        end.latitude, end.longitude, array
    )
    return array[0]
}

private fun MapProperties.getPoints(
    context: Context, callback: YandexMapCallback,
) {
    val list = mutableListOf<PlacemarkMapObject?>()
    
    map.map.addCameraListener { map, position, _, _ ->
        
        searchManager.submit(
            searchText, toPolygon(
                map.visibleRegion(position)
            ), SearchOptions(),
            
            object: SearchListener {
                override fun onSearchResponse(res: Response) {
                    obj.clear()
                    res.collection.children.forEach {
                        val mark = it.obj?.geometry?.first()?.point?.let { point ->
                            val marker = obj.addMarkerWithListener(
                                this@getPoints.map,
                                MeetPlace(
                                    point.latitude,
                                    point.longitude,
                                    it.obj?.name!!,
                                    it.obj?.descriptionText!!,
                                ), context, position
                            )
                            marker
                        }
                        
                        if(!list.contains(mark)) list.add(mark)
                    }
                }
                
                override fun onSearchError(err: Error) {
                    log.d("Error ---> $err")
                }
            }
        )
    }
}

private fun PlacemarkMapObject.changeIcon(
    icon: Int, context: Context,
) = this.setIcon(fromResource(context, icon))


private fun MapView.getProperties(
    state: YandexMapState,
) = state.location.let {
    val point = Point(it?.lat!!, it.lng!!)
    MapProperties(
        camPosition = setCamPosition(point),
        searchManager = getInstance().createSearchManager(ONLINE),
        obj = this.map.mapObjects,
        searchText = state.categoryName ?: "",
        map = this,
        point = point
    )
}

private fun setCamPosition(point: Point) =
    CameraPosition(point, (14f), (0f), (0f))

private data class MapProperties(
    val camPosition: CameraPosition,
    val searchManager: SearchManager,
    val obj: MapObjectCollection,
    val searchText: String,
    val map: MapView,
    val point: Point,
)

private fun MapView.moveCamera(
    camPosition: CameraPosition,
) = this.map.move(
    camPosition,
    Animation(SMOOTH, (1f)),
    null
)

private fun MapKit.userLocation(
    map: MapView, state: Boolean,
) {
    this.createUserLocationLayer(
        map.mapWindow
    ).isVisible = state
}

private fun MapObjectCollection.addMarker(
    point: Point, context: Context, icon: Int,
) = this.addPlacemark(
    point, fromBitmap(
        decodeResource(
            context.resources, icon
        )
    )
)

private fun MapObjectCollection.addMapCircle(
    center: Point, radius: Float = 1000f,
) = this.addCircle(
    Circle(center, radius),
    Color(0xFFFF4745).toArgb(),
    4f, Color(0x1AFF4745).toArgb(),
).let { it.zIndex = 100f }

private fun Context.reqPermissions() {
    if(checkSelfPermission(
            (this), ACCESS_FINE_LOCATION
        ) != PERMISSION_GRANTED
        && checkSelfPermission(
            (this), ACCESS_COARSE_LOCATION
        ) != PERMISSION_GRANTED
    ) requestPermissions(
        this as Activity, arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        ), (0)
    )
}