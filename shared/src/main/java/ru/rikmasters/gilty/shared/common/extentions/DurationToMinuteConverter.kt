package ru.rikmasters.gilty.shared.common.extentions

fun durationToMinutes(duration: String): Int {
    if(duration == "Сутки") return 1440
    val hourCheck = duration.contains("час")
    val minuteCheck = duration.contains("минут")
    
    fun String.getHours() = this.substringBefore("час")
        .replace(" ", "").toInt()
    
    fun String.getMinutes() = this.reversed().substring(6, 8)
        .reversed().replace(" ", "").toInt()
    
    return when {
        hourCheck && minuteCheck ->
            duration.getHours() * 60 + duration.getMinutes()
        
        hourCheck -> duration.getHours() * 60
        minuteCheck -> duration.getMinutes()
        else -> 0
    }
}

fun durationToString(duration: Int): String {
    val hours = (duration - (duration % 60)) / 60
    val minutes = duration - hours * 60
    val hoursLabel = when("$hours".last()) {
        '1' -> "час"
        '2', '3', '4' -> "часа"
        else -> "часов"
    }
    return when {
        hours > 0 && minutes > 0 -> "$hours:$minutes ч"
        hours > 0 -> "$hours $hoursLabel"
        minutes > 0 -> "$minutes мин"
        else -> ""
    }
}