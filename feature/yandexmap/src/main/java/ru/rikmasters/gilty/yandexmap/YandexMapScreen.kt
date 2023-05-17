package ru.rikmasters.gilty.yandexmap

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@Composable
fun YandexMapScreen(
    vm: YandexMapViewModel,
    location: String,
    categoryName: String?,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val userVisibility = false
    
    val addMeet by vm.addMeet.collectAsState()
    val mapKit by vm.mapKit.collectAsState()
    val isSearching by vm.isSearching.collectAsState()
    val address = addMeet?.address ?: ""
    val place = addMeet?.place ?: ""
    
    val loc = mapper.readValue(
        location, LocationModel::class.java
    )
    var placeNameChangeJob: Job? = null
    
    LaunchedEffect(Unit) {
        vm.setMapKit(MapKitFactory.getInstance())
        vm.changeMeetPlace(
            MeetPlace(
                loc.lat, loc.lng,
                loc.place, loc.address
            )
        )
    }
    
    mapKit?.let { map ->
        YandexMapContent(
            YandexMapState(
                map, loc, userVisibility,
                address, place, categoryName, isSearching
            ), Modifier, object: YandexMapCallback {
                
                override fun onMarkerClick(meetPlace: MeetPlace) {
                    placeNameChangeJob?.cancel()
                    placeNameChangeJob = scope.launch {
                        delay(1000L)
                        scope.launch { vm.changeMeetPlace(meetPlace) }
                    }
                }

                override fun onIsSearchingChange(value: Boolean) { scope.launch { vm.onIsSearchingChange(value) } }

                override fun onCameraChange(point: Point) { scope.launch { vm.onCameraChange(point) } }

                override fun getRoute() {
                    scope.launch {
                        if(categoryName.isNullOrBlank())
                            nav.navigate("APPS?lat=${loc.lat}&lng=${loc.lng}")
                        else asm.bottomSheet.collapse()
                    }
                }
                
                override fun onBack() {
                    map.onStop()
                    nav.popBackStack()
                }
            }
        )
    }
}