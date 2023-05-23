package ru.rikmasters.gilty.yandexmap.model

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory.getInstance
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType.COMBINED
import ru.rikmasters.gilty.yandexmap.presentation.YandexMapState

data class MapProperties(
    val searchManager: SearchManager,
    val obj: MapObjectCollection,
    val searchText: String,
    val map: MapView,
    val point: Point,
)

fun MapView.getProperties(
    state: YandexMapState,
) = state.location.let {
    val point = Point(
        it?.lat ?: 0.0,
        it?.lng ?: 0.0
    )
    MapProperties(
        searchManager = getInstance()
            .createSearchManager(COMBINED),
        obj = this.map.mapObjects,
        searchText = state.categoryName ?: "",
        map = this,
        point = point
    )
}