package ru.rikmasters.gilty.shared.common.extentions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

const val FORMAT = "yyyy-MM-dd"
const val FULL_FORMAT = "yyyy-MM-dd-HH-mm-ss"
const val MILLIS_IN_SECOND = 1000
const val SECONDS_IN_MINUTE = 60
const val MINUTES_IN_HOUR = 60
const val HOURS_IN_DAY = 24

const val MILLIS_IN_DAY = MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY

fun LocalDate.toEpochMillis() =
    this.toEpochDay() * MILLIS_IN_DAY

object LocalDateFactory {

    fun ofEpochMillis(millis: Long): LocalDate =
        LocalDate.ofEpochDay(millis / MILLIS_IN_DAY)
}


fun todayControl(date: String): Boolean {
    return date.format(FORMAT) == DateTimeFormatter.ofPattern(FORMAT).format(LocalDate.now())
}

fun String.format(pattern: String): String =
    OffsetDateTime.parse(this)
        .withOffsetSameInstant(ZoneOffset.ofHours(3))
        .toLocalDateTime()
        .format(pattern)

fun LocalDateTime.display(): String =
    this.format("d '%s' yyyy, HH:mm")
        .format(this.month.display())

fun LocalDateTime.displayDate(): String =
    this.format("d '%s' yyyy")
        .format(this.month.display())

fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

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

fun Month.displayStatic(): String = monthNames[this.ordinal]
