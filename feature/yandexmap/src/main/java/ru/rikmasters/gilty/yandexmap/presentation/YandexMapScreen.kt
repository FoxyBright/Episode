package ru.rikmasters.gilty.yandexmap.presentation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetState
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue.Collapsed
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue.Expanded
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.yandexmap.model.MeetPlaceModel
import ru.rikmasters.gilty.yandexmap.viewmodel.YandexMapViewModel

@Composable
fun YandexMapScreen(
    vm: YandexMapViewModel,
    location: String,
    categoryName: String?,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    
    val isSearching by vm.isSearching.collectAsState()
    val addMeet by vm.addMeet.collectAsState()
    val mapKit by vm.mapKit.collectAsState()
    val address = addMeet?.address ?: ""
    val place = addMeet?.place ?: ""
    
    val loc = mapper.readValue(
        location, LocationModel::class.java
    )
    var placeNameChangeJob: Job? = null
    
    LaunchedEffect(Unit) {
        vm.setMapKit(
            MapKitFactory
                .getInstance()
        )
        vm.changeMeetPlace(
            MeetPlaceModel(
                lat = loc.lat,
                lng = loc.lng,
                place = loc.place,
                address = loc.address
            )
        )
    }
    var bsState by remember {
        mutableStateOf(true)
    }
    
    val subBsState =
        rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(Expanded)
        )
    
    val appBsState =
        rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(Collapsed)
        )
    
    LaunchedEffect(bsState) {
        subBsState.bottomSheetState.let {
            if(bsState) it.expand()
            else it.collapse()
        }
    }
    
    mapKit?.let { map ->
        YandexMapContent(
            state = YandexMapState(
                mapKit = map,
                location = loc,
                userVisible = false,
                address = address,
                place = place,
                categoryName = categoryName,
                isSearching = isSearching,
                subBsState = subBsState,
                appBsState = appBsState
            ),
            callback = object: YandexMapCallback {
                
                override fun appBsExpandState(state: Boolean) {
                    val bs =
                        appBsState.bottomSheetState
                    scope.launch {
                        if(state) bs.expand()
                        else bs.collapse()
                    }
                }
                
                override fun subBsExpandState(state: Boolean) {
                    bsState = state
                }
                
                override fun onIsSearchingChange(value: Boolean) {
                    scope.launch { vm.onIsSearchingChange(value) }
                }
                
                override fun onCameraChange(point: Point) {
                    scope.launch { vm.onCameraChange(point) }
                }
                
                override fun onMarkerClick(
                    meetPlaceModel: MeetPlaceModel,
                ) {
                    placeNameChangeJob?.cancel()
                    placeNameChangeJob = scope.launch {
                        delay(1000L)
                        scope.launch {
                            vm.changeMeetPlace(
                                meetPlaceModel
                            )
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