package ru.rikmasters.gilty.yandexmap.ymap

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.ui_view.ViewProvider
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import ru.rikmasters.gilty.yandexmap.model.getMeetPlace
import ru.rikmasters.gilty.yandexmap.presentation.YandexMapCallback

/**
 * Добавляет круг на карту
 * **/
fun MapObjectCollection.addMapCircle(
    center: Point,
    radius: Float = 1000f,
    borderColor: Color = Color(0xFFFF4745),
    background: Color = Color(0x1AFF4745),
    borderWidth: Float = 4f,
    zIndex: Float = 100f,
) {
    val circle = Circle(center, radius)
    val border = borderColor.toArgb()
    val back = background.toArgb()
    
    this.addCircle(
        circle, border,
        borderWidth, back
    ).zIndex = zIndex
}

/**
 * Добавляет маркер на карту
 * **/
fun MapObjectCollection.addMarker(
    point: Point,
    context: Context,
    icon: Int,
) {
    this.addPlacemark(
        point, context.getMarkerView(
            image = icon
        )
    )
}

/**
 * Добавляет на карту маркер с возможностью прослушки касания
 * **/
@SuppressLint("UseCompatLoadingForDrawables")
fun MapObjectCollection.addMarkerWithListener(
    map: MapView,
    point: Point,
    geoItem: GeoObjectCollection.Item,
    context: Context,
    callback: YandexMapCallback?,
): PlacemarkMapObject {
    
    return addPlacemark(
        point, context.getMarkerView(false)
    ).let {
        val meetPlace = getMeetPlace(it, geoItem)
        
        it.addTapListener { _, _ ->
            callback?.onMarkerClick(meetPlace)
            vibrate(context)
            map.moveCamera(point)
            true
        }
        
        return@let it
    }
}

/**
 * Позволяет менять векторную иконку
 * у существующих маркеров
 * **/
@SuppressLint("UseCompatLoadingForDrawables")
fun Context.getMarkerView(
    selected: Boolean = false,
    image: Int? = null,
) = View(this).apply {
    val icon = image ?: if(selected)
        R.drawable.ic_point_on_the_map_selected
    else R.drawable.ic_point_on_the_map
    
    background = context.getDrawable(icon)
}.let { ViewProvider(it) }