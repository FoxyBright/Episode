package ru.rikmasters.gilty.yandexmap.ymap

import android.location.Location
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

/**
 * Позволяет получать координаты пользователя
 * и отборажать их маркером на карте
 * **/
fun MapKit.userLocation(
    map: MapView, state: Boolean,
) {
    this.createUserLocationLayer(
        map.mapWindow
    ).isVisible = state
}

/**
 * Возвращает ближайшую к указанной точке из предоставленного списка
 * **/
infix fun Point.near(
    list: List<Point>,
) = list.map { it to (it distance this) }
    .filter { it.second <= 100f }
    .minByOrNull { it.second }
    ?.first

/**
 * Возвращает расстояние между выбранными точками в метрах
 * **/
infix fun Point.distance(point: Point): Float {
    val array = FloatArray(2)
    Location.distanceBetween(
        latitude, longitude,
        point.latitude, point.longitude,
        array
    )
    return array[0]
}

/**
 * Позволяет сравнить две точки коодинат
 * **/
infix fun Point.equally(
    value: Point?,
) = value?.let {
    latitude == it.latitude
            && longitude == it.longitude
} ?: false

/**
 * Перемещает камеру на карте по указанным координатам
 * **/
fun MapView.moveCamera(
    point: Point,
    animation: Animation = Animation(SMOOTH, (1f)),
    zoom: Float = 14f,
) {
    CameraPosition(point, zoom, (0f), (0f)).let {
        this.map.move(it, animation, null)
    }
}