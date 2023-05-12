package ru.rikmasters.gilty.shared.common.extentions

private const val DASH = "-"
const val FORMAT = "yyyy-MM-dd"
const val TIME_FORMAT = "HH:mm:ss"
val LOCAL_TIME: LocalTime = LocalTime.now()
val LOCAL_DATE: LocalDate = LocalDate.now()
const val ZERO_TIME = "T00:00:00.000Z"
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
        difference in 60..3_599 -> "${difference / 60} мин"
        difference in 3_600..86_399 -> "${difference / 3_600} час"
        difference in 86_400..604_799 -> "${difference / 86_400} д"
        difference in 604_800..2_591_999 -> "${difference / 604_800} нед"
        difference in 2_592_000..946_079_999 -> "${difference / 2_592_000} мес"
        else -> (difference / 946_080_000).let {
            when("$it".last()) {
                '1' -> "$it год"
                '2', '3', '4' -> "$it года"
                else -> "$it лет"
            }
        }
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

fun getMonth(date:String):Int{
    val dateList = date.format(FORMAT).split(DASH)
    val localDate = LocalDate.of(
        dateList.first().toInt(),
        dateList[1].toInt(),
        dateList.last().toInt()
    ).millis()

    return LocalDateTime(localDate).month()

}

fun isAfter(date1:String, date2:String):Boolean{
    val dateList1 = date1.format(FORMAT).split(DASH)
    val localDate1 = LocalDate.of(
        dateList1.first().toInt(),
        dateList1[1].toInt(),
        dateList1.last().toInt()
    ).millis()

    val dateList2 = date2.format(FORMAT).split(DASH)
    val localDate2 = LocalDate.of(
        dateList2.first().toInt(),
        dateList2[1].toInt(),
        dateList2.last().toInt()
    ).millis()
    return localDate1 > localDate2
}