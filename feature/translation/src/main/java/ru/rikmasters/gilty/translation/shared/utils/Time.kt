package ru.rikmasters.gilty.translation.shared.utils

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun getTimeDifferenceString(to: ZonedDateTime) : String {
    val currentTime = ZonedDateTime.now()
    if (currentTime.isAfter(to)) return "00:00"
    val differenceHours = currentTime.until(to, ChronoUnit.HOURS)
    val differenceMinutes = currentTime.until(to, ChronoUnit.MINUTES) % 60
    val differenceSeconds = currentTime.until(to, ChronoUnit.SECONDS) % 60
    val hour = if (differenceHours > 0) "$differenceHours:" else ""
    val minute = if (differenceMinutes < 10) "0$differenceMinutes:" else "$differenceMinutes:"
    val seconds = if (differenceSeconds < 10) "0$differenceSeconds" else "$differenceSeconds"
    return "$hour$minute$seconds"
}

fun getAdditionalTimeString(duration: Int) : String {
    val hour = (duration / 60).takeIf { it > 0 }
    val hourString = hour?.let { "$hour:" } ?: ""
    val minute = hour?.let { duration - 60 } ?: duration
    val minuteString = hour?.let {
        if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }
    } ?: "$minute"
    return "$hourString$minuteString:00"
}
