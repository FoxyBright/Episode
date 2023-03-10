package ru.rikmasters.gilty.yandexmap

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.yandex.mapkit.MapKitFactory
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@Composable
fun YandexMapScreen(
    vm: YandexMapViewModel,
    location: String,
    nav: NavHostController,
) {
    
    val userLocationState by vm.userLocation.collectAsState()
    val mapKit by vm.mapKit.collectAsState()
    val loc = mapper.readValue(
        location, LocationModel::class.java
    )
    
    LaunchedEffect(Unit) { vm.setMapKit(MapKitFactory.getInstance()) }
    
    mapKit?.let { map ->
        YandexMapContent(
            YandexMapState(
                loc, map, userLocationState
            ), Modifier, object: YandexMapCallback {
                
                override fun getRoute() {
                    nav.navigate("APPS?lat=${loc.lat}&lng=${loc.lng}")
                }
                
                override fun onBack() {
                    map.onStop()
                    nav.popBackStack()
                }
            }
        )
    }
}