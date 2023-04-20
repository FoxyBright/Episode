package ru.rikmasters.gilty.shared.common.extentions

private const val DASH = "-"
const val FORMAT = "yyyy-MM-dd"
const val TODAY_LABEL = "Сегодня"
const val TIME_FORMAT = "HH:mm:ss"
val LOCAL_TIME: LocalTime = LocalTime.now()
val LOCAL_DATE: LocalDate = LocalDate.now()
const val ZERO_TIME = "T00:00:00.000Z"
const val TIME_START = "00"
val NOW_DATE = "${LOCAL_DATE}T$LOCAL_TIME.000Z"
val TOMORROW = "${LOCAL_DATE.plusDays(1)}T$LOCAL_TIME.000Z"

fun getDate() = arrayListOf<String>().let {
    repeat(365) { i ->
        it.add("${LOCAL_DATE.plusDays(i)}$ZERO_TIME")
    }
    it.toList()
}

fun getTime() = arrayListOf<String>().let {
    repeat(24) { i ->
        it.add(
            if(i in 0..9)
                "0$i" else "$i"
        )
    }; it
} to arrayListOf<String>().let {
    repeat(12) { i ->
        it.add(
            when(i) {
                0 -> "00"
                1 -> "05"
                else -> "${i * 5}"
            }
        )
    }; it
}

fun todayControl(date: String) =
    date.format(FORMAT) == LOCAL_DATE.format(FORMAT)

fun tomorrowControl(date: String) =
    date.format(FORMAT) == LOCAL_DATE
        .plusDays(1)
        .format(FORMAT)

fun getDifferenceOfTime(date: String): String {
    val difference = (
            LocalDateTime.now().plusMinute(1).millis() -
                    LocalDateTime.of(date).millis()
            ) / 1000
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
        dateList.first().toInt(),
        dateList[1].toInt(),
        dateList.last().toInt()
    )
}

fun String.timeClock() =
    this.format("HH:mm")


fun String.dateCalendar() =
    this.format("dd MMMM")


fun String.time() = this
    .format(TIME_FORMAT)
    .split(":").let {
        LocalTime.of(
            it.first().toInt(),
            it[1].toInt(),
            it.last().toInt()
        )
    }


fun weekControl(
    date: String,
) = (date.format(FORMAT)
    .split(DASH).let {
        LocalDate.of(
            it.first().toInt(),
            it[1].toInt(),
            it.last().toInt()
        ).second()
    } in thisWeek().first..
        thisWeek().second)


fun earlierWeekControl(date: String): Boolean {
    val dateList = date.format(FORMAT).split(DASH)
    val localDate = LocalDate.of(
        dateList.first().toInt(),
        dateList[1].toInt(),
        dateList.last().toInt()
    ).millis() / 1000
    return (localDate < thisWeek().first)
}

fun thisWeek() = LOCAL_DATE
    .let { it.minusDays(it.dayOfWeek().ordinal) }
    .let { it.second() to it.plusDays(7).second() }
