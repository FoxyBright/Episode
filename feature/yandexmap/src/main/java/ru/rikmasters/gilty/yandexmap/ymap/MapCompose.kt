package ru.rikmasters.gilty.yandexmap.ymap

import android.content.Context
import android.view.LayoutInflater
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegionUtils.toPolygon
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.feature.yandexmap.R.id.mapview
import ru.rikmasters.gilty.feature.yandexmap.R.layout.map_layout
import ru.rikmasters.gilty.shared.R.drawable.ic_location
import ru.rikmasters.gilty.shared.R.drawable.ic_not_selected_pointer
import ru.rikmasters.gilty.yandexmap.model.*
import ru.rikmasters.gilty.yandexmap.presentation.YandexMapCallback
import ru.rikmasters.gilty.yandexmap.presentation.YandexMapState
import kotlin.math.roundToInt


fun MapProperties.cameraListener(callback: YandexMapCallback? = null) {
    val cameraListener1 = CameraListener { map, position, _, finished ->
        callback?.onCameraChange(position.target)
    }
    this.map.map.addCameraListener(cameraListener1)
}

@Composable
fun MapContent(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    callback: YandexMapCallback? = null,
) {
    val offsetY: Animatable<Float, AnimationVector1D> =
        remember { Animatable(0f) }

    val currentPointerImage = remember {
        mutableStateOf(ic_not_selected_pointer)
    }

    LaunchedEffect(state.isSearching) {
        if (state.isSearching && offsetY.value == 0f) {
            callback?.subBsExpandState(false)
            currentPointerImage.value = ic_not_selected_pointer
            offsetY.animateTo(
                targetValue = -45f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 0
                )
            )
        } else if (!state.isSearching) {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 0
                )
            )
            currentPointerImage.value = ic_location
            callback?.subBsExpandState(true)
        }
    }

    Box(modifier) {
        AndroidView(
            factory = { context ->
                val properties = LayoutInflater
                    .from(context)
                    .inflate(map_layout, (null), (false))
                    .findViewById<MapView>(mapview)
                    .getProperties(state)

                properties.map.moveCamera(properties.point)

                val obj = properties.obj
                val point = properties.point

                when {
                    !state.categoryName.isNullOrBlank() -> {
                        @Suppress("unused_variable") // для stronglink
                        val camListener = properties.getPoints(
                            context, properties, callback
                        )
                    }

                    state.location?.hide != false -> {
                        obj.addMapCircle(point)
                        @Suppress("unused_variable") // для stronglink
                        val camListener =
                            properties camListener callback
                    }


                    else -> {
                        obj.addMarker(point, context, ic_location)
                        @Suppress("unused_variable") // для stronglink
                        val camListener =
                            properties camListener callback
                    }
                }

                context.requestGeoPermissions()

                state.mapKit.userLocation(
                    map = properties.map,
                    state = state.userVisible
                )

                state.mapKit.onStart()
                properties.map
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Cursor(
            state = !state.categoryName.isNullOrBlank(),
            icon = currentPointerImage.value,
            modifier = Modifier.align(Center),
            offset = offsetY.value.roundToInt()
        )
    }
}

@Composable
private fun Cursor(
    state: Boolean,
    icon: Int,
    modifier: Modifier,
    offset: Int,
) {
    if (state) Box(modifier.height(80.dp)) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .align(TopCenter)
                .offset { IntOffset((0), offset) }
        )
    }
}

private infix fun MapProperties.camListener(
    callback: YandexMapCallback?,
) = map.map.addCameraListener { _, _, _, finished ->
    callback?.subBsExpandState(finished)
}

private fun MapProperties.getPoints(
    context: Context,
    properties: MapProperties,
    callback: YandexMapCallback?,
) = map.map.addCameraListener { map, position, _, finished ->

    callback?.onCameraChange(position.target)

    search(map, position, callback) { points ->
        val nearest by mutableStateOf(
            position.target near points.map { it.first }
        )

        points.forEach { (p, it) ->
            val marker = obj.addMarkerWithListener(
                this.map, p, it, context, callback
            )
            if (p equally nearest) {
                marker.setView(context.getMarkerView(true))
                if (finished) properties.map.moveCamera(p)
                callback?.onMarkerClick((getMeetPlace(marker, it)))
            } else marker.setView(context.getMarkerView(false))
        }
    }
}

private fun MapProperties.search(
    camMap: Map, position: CameraPosition,
    callback: YandexMapCallback?,
    onUpdate: (List<Pair<Point, Item>>) -> Unit,
) {
    val list = mutableListOf<Pair<Point, Item>>()
    searchManager.submit(
        Point(
            position.target.latitude,
            position.target.longitude
        ), null, SearchOptions(), object : SearchListener {
            override fun onSearchResponse(p0: Response) {
                callback?.onMarkerClick(
                    MeetPlaceModel(
                        lat = position.target.latitude,
                        lng = position.target.longitude,
                        place = p0.collection.children[0].obj
                            ?.descriptionText.toString(),
                        address = p0.collection.children[0].obj
                            ?.name.toString()
                    )
                )
            }

            override fun onSearchError(p0: Error) {}
        }
    )

    val searchListener = object : SearchListener {
        override fun onSearchResponse(res: Response) {
            obj.clear()
            list.clear()
            res.collection.children.forEach {
                it.obj?.geometry?.first()
                    ?.point?.let { point ->
                        list.add(point to it)
                    }
            }
            onUpdate(list)
        }

        override fun onSearchError(err: Error) {
            log.d(err.toString())
        }
    }

    searchManager.submit(
        searchText, toPolygon(camMap.visibleRegion(position)),
        SearchOptions(), searchListener
    )
}