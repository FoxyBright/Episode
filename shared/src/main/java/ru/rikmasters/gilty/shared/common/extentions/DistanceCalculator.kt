package ru.rikmasters.gilty.shared.common.extentions

import android.location.Location
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
infix fun FullMeetingModel.distance(
    myLocation: Pair<Double, Double>?
): String {
    
    if(location?.lat == null
        || location.lng == null
        || myLocation == null
    ) return ""
    
    val array = FloatArray(2)
    
    Location.distanceBetween(
        location.lat,
        location.lng,
        myLocation.first,
        myLocation.second,
        array
    )
    
    return "${array[0].toInt() / 1000} км"
}