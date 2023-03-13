package ru.rikmasters.gilty.yandexmap

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@Composable
fun YandexMapScreen(
    vm: YandexMapViewModel,
    location: String,
    categoryName: String?,
    nav: NavHostController,
) {
    
    val userVisibility = false
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    
    val address by vm.address.collectAsState()
    val mapKit by vm.mapKit.collectAsState()
    val place by vm.place.collectAsState()
    val loc = mapper.readValue(
        location, LocationModel::class.java
    )
    
    LaunchedEffect(Unit) {
        vm.setMapKit(MapKitFactory.getInstance())
        loc.address?.let { vm.changeAddress(it) }
        loc.place?.let { vm.changePlace(it) }
        log.d("MEET CATEGORY --->>> $categoryName")
    }
    
    mapKit?.let { map ->
        YandexMapContent(
            YandexMapState(
                map, loc, userVisibility,
                address, place, categoryName
            ), Modifier, object: YandexMapCallback {
                
                
                override fun onMarkerClick(meetPlace: MeetPlace) {
                    scope.launch {
                        vm.changeMeetPlace(meetPlace)
    
                        Toast.makeText(context, meetPlace.place, Toast.LENGTH_SHORT).show()
                        vm.changeAddress(meetPlace.address)
                        vm.changePlace(meetPlace.place)
                    }
                }
                
                override fun getRoute() {
                    scope.launch {
                        if(categoryName.isNullOrBlank())
                            nav.navigate("APPS?lat=${loc.lat}&lng=${loc.lng}")
                        else {
                            // TODO здесь помещать в экран деталей
                            asm.bottomSheet.collapse()
                        }
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