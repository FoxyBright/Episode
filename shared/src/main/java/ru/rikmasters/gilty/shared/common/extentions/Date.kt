package ru.rikmasters.gilty.shared.common.extentions

import android.os.Build
import androidx.annotation.RequiresApi
import ru.rikmasters.gilty.core.log.log
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

private const val DASH = "-"
const val FORMAT = "yyyy-MM-dd"
const val TODAY_LABEL = "Сегодня"
const val TIME_FORMAT = "HH-mm-ss"
val LOCAL_TIME: LocalTime = LocalTime.now()
val LOCAL_DATE: LocalDate = LocalDate.now()
const val ZERO_TIME = "T00:00:00.000Z"
const val TIME_START = "00"
const val MILLIS_IN_SECOND = 1000
const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val HOURS_IN_DAY = 24

const val MILLIS_IN_DAY = MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY

fun LocalDate.toEpochMillis() =
    this.toEpochDay() * MILLIS_IN_DAY

@Suppress("unused")
object LocalDateFactory {

    fun ofEpochMillis(millis: Long): LocalDate =
        LocalDate.ofEpochDay(millis / MILLIS_IN_DAY)
}

fun getDate(period: Int = 150): List<String> {
    val list = arrayListOf<String>()
    getDateList(period).first.forEach { list.add(it) }
    list.reverse(); list.add(TODAY_LABEL)
    getDateList(period).second.forEach { list.add(it) }
    list.forEach { log.v(it) }
    return list
}

fun getDateList(times: Int): Pair<List<String>, List<String>> {
    val lastList = arrayListOf<String>()
    val newList = arrayListOf<String>()
    var todayPlus = LOCAL_DATE.plusDays(1)
    var todayMinus = LOCAL_DATE.minusDays(1)
    for (i in 1..times) {
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
    for (it in range as IntRange step step)
        if (it.toString().length != 1) list.add(it.toString())
        else list.add("0${it}")
    list.add("end")
    return list
}

fun replacer(it: String, end: String): String {
    return when (it) {
        "start" -> end
        "end" -> TIME_START
        else -> it
    }
}

fun todayControl(date: String): Boolean {
    return date.format(FORMAT) == LOCAL_DATE.format(FORMAT)
}

fun getDifferenceOfTime(date: String): String {
    val list = arrayListOf<Int>()
    "${date.time().minusHours((3))}".split((":"))
        .forEach { list.add(it.toInt()) }
    return if (todayControl(date))
        if (LOCAL_TIME.hour - list.first() > 0)
            "${LOCAL_TIME.hour - list.first()} ч"
        else if (LOCAL_TIME.minute - list[1] > 0)
            "${LOCAL_TIME.minute - list[1]} м"
        else "${LOCAL_TIME.second - list.last()} с"
    else "${LOCAL_DATE.dayOfYear - date.date().dayOfYear} д"
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
    val dateList = this.format(TIME_FORMAT).split(DASH)
    return LocalTime.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    )
}

fun weekControl(date: String): Boolean {
    val dateList = date.format(FORMAT).split(DASH)
    val localDate = LocalDate.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    ).toEpochMillis() / 1000
    return (localDate in thisWeek().first..thisWeek().second)
}

fun thisWeek(): Pair<Long, Long> {
    val format = DateTimeFormatter.ofPattern("dd")
    val now = LocalDate.now()
    val start = LocalDate.of(
        now.year, now.month, format.format(
            LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        ).toInt()
    )
    return Pair(
        start.toEpochMillis() / 1000, LocalDate.of(
            now.year, now.month, format.format(start.plusDays(7)).toInt()
        ).toEpochMillis() / 1000
    )
}

fun String.format(pattern: String): String =
    OffsetDateTime.parse(this)
        .withOffsetSameInstant(ZoneOffset.ofHours(3))
        .toLocalDateTime()
        .format(pattern)

@Suppress("unused")
fun LocalDateTime.display(): String =
    this.format("d '%s' yyyy, HH:mm")
        .format(this.month.display())

@Suppress("unused")
fun LocalDateTime.displayDate(): String =
    this.format("d '%s' yyyy")
        .format(this.month.display())

fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

@Suppress("unused")
fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

@Suppress("unused")
fun LocalTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

var monthNames = arrayOf(
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь"
)

@RequiresApi(Build.VERSION_CODES.O)
fun Month.display(): String =
    this.getDisplayName(TextStyle.FULL, Locale("ru"))

@Suppress("unused")
fun Month.displayStatic(): String = monthNames[this.ordinal]
