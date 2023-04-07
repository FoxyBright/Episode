package ru.rikmasters.gilty.shared.common.extentions

private const val DASH = "-"
const val FORMAT = "yyyy-MM-dd"
const val TODAY_LABEL = "Сегодня"
const val TIME_FORMAT = "HH:mm:ss"
val LOCAL_TIME: LocalTime = LocalTime.now()
val LOCAL_DATE: LocalDate = LocalDate.now()
const val ZERO_TIME = "T00:00:00.000Z"
const val TIME_START = "00"
const val MINUTES_IN_HOUR = 55
const val HOURS_IN_DAY = 23
val NOW_DATE = "${LOCAL_DATE}T${LOCAL_TIME}.000Z"
val TOMORROW = "${LOCAL_DATE.plusDays(1)}T${LOCAL_TIME}.000Z"
val YESTERDAY = "${LOCAL_DATE.minusDays(1)}T${LOCAL_TIME}.000Z"
fun getDate(period: Int = 150): List<String> {
    val list = arrayListOf<String>()
    getDateList(period).first.forEach { list.add(it) }
    list.reverse(); list.add(TODAY_LABEL)
    getDateList(period).second.forEach { list.add(it) }
    return list
}

fun getDateList(times: Int): Pair<List<String>, List<String>> {
    val lastList = arrayListOf<String>()
    val newList = arrayListOf<String>()
    var todayPlus = LOCAL_DATE.plusDays(1)
    var todayMinus = LOCAL_DATE.minusDays(1)
    for(i in 1..times) {
        newList.add("$todayPlus$ZERO_TIME".dateCalendar())
        lastList.add("$todayMinus$ZERO_TIME".dateCalendar())
        todayPlus = todayPlus.plusDays(1)
        todayMinus = todayMinus.minusDays(1)
    }
    return Pair(lastList, newList)
}

fun getTime(range: Iterable<Int>, step: Int): List<String> {
    val list = arrayListOf<String>()
    list.add("start")
    for(it in range as IntRange step step)
        if(it.toString().length != 1) list.add(it.toString())
        else list.add("0${it}")
    list.add("end")
    return list
}

fun replacer(it: String, end: String): String {
    return when(it) {
        "start" -> end
        "end" -> TIME_START
        else -> it
    }
}

fun todayControl(date: String) =
    date.format(FORMAT) == LOCAL_DATE.format(FORMAT)

fun tomorrowControl(date: String) =
    date.format(FORMAT) == LOCAL_DATE
        .plusDays(1)
        .format(FORMAT)

fun getDifferenceOfTime(date: String): String {
    val difference = (LocalDateTime.now().plusMinute(1).millis() -
            LocalDateTime.of(date).millis()) / 1000
    return when {
        difference < 60 -> "$difference cек"
        difference in 60..3599 -> "${difference / 60} мин"
        difference in 3600..86399 -> "${difference / 3600} ч"
        else -> "${difference / 86400} д"
    }
}

fun String.date(): LocalDate {
    val dateList = this.format(FORMAT).split(DASH)
    return LocalDate.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    )
}

fun String.timeClock(): String {
    return this.format("HH:mm")
}

fun String.dateCalendar(): String {
    return this.format("dd MMMM")
}

fun String.time(): LocalTime {
    val dateList = this.format(TIME_FORMAT).split(":")
    return LocalTime.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    )
}

fun weekControl(date: String): Boolean {
    val dateList = date.format(FORMAT).split(DASH)
    val localDate = LocalDate.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    ).millis() / 1000
    return (localDate in thisWeek().first..thisWeek().second)
}

fun earlierWeekControl(date: String): Boolean {
    val dateList = date.format(FORMAT).split(DASH)
    val localDate = LocalDate.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    ).millis() / 1000
    return (localDate < thisWeek().first)
}

fun thisWeek(): Pair<Long, Long> {
    val start = LOCAL_DATE.fistDayOfWeek()
    return Pair(
        start.millis() / 1000, LocalDate.of(
            LOCAL_DATE.year(), LOCAL_DATE.month(),
            start.plusDays(7).format("dd").toInt()
        ).millis() / 1000
    )
}
