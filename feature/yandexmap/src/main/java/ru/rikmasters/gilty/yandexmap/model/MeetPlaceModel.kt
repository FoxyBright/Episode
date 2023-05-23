package ru.rikmasters.gilty.yandexmap.model

import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.map.PlacemarkMapObject

data class MeetPlaceModel(
    val lat: Double?,
    val lng: Double?,
    val place: String?,
    val address: String?,
)

fun getMeetPlace(
    placemark: PlacemarkMapObject,
    geoItem: Item,
) = MeetPlaceModel(
    lat = placemark.geometry.latitude,
    lng = placemark.geometry.longitude,
    place = geoItem.obj?.name ?: "",
    address = geoItem.obj?.descriptionText ?: "",
)