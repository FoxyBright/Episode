package ru.rikmasters.gilty.yandexmap

import com.yandex.mapkit.MapKit
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager

class YandexMapViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy { manager.addMeetFlow.state(null) }
    
    val mapKit = MutableStateFlow<MapKit?>(null)
    
    suspend fun changeMeetPlace(meetPlace: MeetPlace) {
        manager.update(
            place = meetPlace.place,
            address = meetPlace.address,
            lat = meetPlace.lat,
            lng = meetPlace.lng,
        )
    }
    
    suspend fun setMapKit(map: MapKit) {
        mapKit.emit(map)
    }
}