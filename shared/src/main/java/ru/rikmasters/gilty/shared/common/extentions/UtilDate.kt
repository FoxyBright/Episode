@file:Suppress("DEPRECATION", "unused")

package ru.rikmasters.gilty.shared.common.extentions

import android.annotation.SuppressLint
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.display
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.*
import java.util.Date
import kotlin.math.abs

const val DATE_FORMAT = "yyyy-MM-dd"
const val TIME_HOUR_MINUTE_FORMAT = "HH:mm"
const val FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
const val FULL_DATE_FORMAT_WIDTH_ZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val DATE_POINT_PATTERN = "dd.MM.yyyy"
const val MILLIS_IN_DAY = 86_400_000

/* LOCAL DATE */
////////////////////////////////////////////////////////////////////////////////////////////////////

interface LocalDateCallBack {
    
    fun year(date: Long): Int
    fun month(date: Long): Int
    fun day(date: Long): Int
    fun plusYear(date: Long, count: Int): Long
    fun plusMonth(date: Long, count: Int): Long
    fun plusDay(date: Long, count: Int): Long
    fun minusYear(date: Long, count: Int): Long
    fun minusMonth(date: Long, count: Int): Long
    fun minusDay(date: Long, count: Int): Long
    fun isBefore(from: Long, to: Long): Boolean
    fun isAfter(from: Long, to: Long): Boolean
    fun compareTo(date: Long, comp: Long): Int
    fun dayOfWeek(date: Long): DayOfWeek
}

private val dateCallback = object: LocalDateCallBack {
    override fun year(date: Long): Int = part(date, "yyyy")
    override fun month(date: Long): Int = part(date, "MM")
    override fun day(date: Long): Int = part(date, "dd")
    override fun plusYear(date: Long, count: Int) = plus(date, YEAR, count)
    override fun plusMonth(date: Long, count: Int) =
        plus(date, MONTH, count)
    
    override fun plusDay(date: Long, count: Int) =
        plus(date, DAY_OF_MONTH, count)
    
    override fun minusYear(date: Long, count: Int) =
        plus(date, YEAR, -count)
    
    override fun minusMonth(date: Long, count: Int) =
        plus(date, MONTH, -count)
    
    override fun minusDay(date: Long, count: Int) =
        plus(date, DAY_OF_MONTH, -count)
    
    override fun isBefore(from: Long, to: Long) = from < to
    override fun isAfter(from: Long, to: Long) = from > to
    override fun compareTo(date: Long, comp: Long) =
        Date(date).compareTo(Date(comp))
    
    override fun dayOfWeek(date: Long) =
        DayOfWeek.dayOfWeek(Date(date).toString().substring(0, 3))
}

class LocalDate(private var millis: Long) {
    companion object {
        
        /** Создает объект с указанными данными **/
        fun of(date: String) = LocalDate(date.long())
        
        fun of(year: Int, month: Int = 1, day: Int = 1) =
            LocalDate("$year-$month-$day".long())
        
        /** Выдает экземпляр с текущей датой **/
        private val now = c.time
        fun now() = LocalDate(
            "${now.year + 1900}-${
                now.month + 1
            }-${now.date}".long()
        )
        
        val MIN = LocalDate(Long.MIN_VALUE)
        
        val MAX = LocalDate(Long.MAX_VALUE)
    }
    
    /** Возвращает дату в виде строки **/
    override fun toString() = LocalDate(millis).format(DATE_FORMAT)
    
    /** Возвращает количество дней в месяце **/
    fun lengthOfMounth(): Int {
        val cal = getInstance()
        cal.time = Date(this.toString().long())
        return cal.getActualMaximum(DAY_OF_MONTH)
    }
    
    fun monthName() = Month.of(this.month()).display()
    
    fun atStartOfMounth() = if(this.day() == 1) this
    else this.minusDays(this.day() - 1)
    
    fun firstDayOfWeek() = this.atStartOfMounth().dayOfWeek()
    
    
    /** Приводит вид объекта к определенной форме. Возвращает строку **/
    fun format(pattern: String) = Date(millis).format(pattern)
    
    /** Возвращает дату на начало дня */
    fun atStartOfDay() = LocalDateTime.of(LocalDate(millis), LocalTime(0))
    
    /** Возвращает true если дата меньше, чем указаная для сравнения */
    fun isBefore(to: LocalDate) = Date(millis).before(Date(to.millis()))
    
    /** Возвращает true если дата больше, чем указаная для сравнения */
    fun isAfter(to: LocalDate) = Date(millis).after(Date(to.millis()))
    
    /** Возвращает -1    \    0    \     1 в зависимости от того равны даты или одна из них больше или меньше */
    fun compareTo(comp: LocalDate) =
        dateCallback.compareTo(millis, comp.millis())
    
    fun dayOfYear(): Int {
        val cal = getInstance()
        cal.clear()
        cal.time = Date(this.millis())
        return cal.get(DAY_OF_YEAR)
    }
    
    fun fistDayOfWeek(): LocalDate {
        val cal = getInstance()
        cal.time = Date(this.millis())
        val week = cal.get(WEEK_OF_MONTH)
        cal.clear()
        cal.set(WEEK_OF_MONTH, week)
        return LocalDate(cal.time.time)
    }
    
    /** Прибавляет к дате день/месяц/год **/
    fun plusYears(count: Int) =
        LocalDate(dateCallback.plusYear(millis, count))
    
    fun plusMonths(count: Int) =
        LocalDate(dateCallback.plusMonth(millis, count))
    
    fun plusDays(count: Int) =
        LocalDate(dateCallback.plusDay(millis, count))
    
    fun plusWeeks(count: Int) =
        LocalDate(dateCallback.plusDay(millis, 7) * count)
    
    /** Отнимает от даты день/месяц/год **/
    fun minusYears(count: Int) =
        LocalDate(dateCallback.minusYear(millis, count))
    
    fun minusMonths(count: Int) =
        LocalDate(dateCallback.minusMonth(millis, count))
    
    fun minusDays(count: Int) =
        LocalDate(dateCallback.minusDay(millis, count))
    
    /** Возвращает день/месяц/год от даты или дату в милисекундах **/
    fun year() = dateCallback.year(millis)
    fun month() = dateCallback.month(millis)
    fun day() = dateCallback.day(millis)
    fun dayOfWeek() = dateCallback.dayOfWeek(millis)
    fun millis() = millis
    
    fun second() = millis / 1000
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        other as LocalDate
        if(millis != other.millis) return false
        return true
    }
    
    override fun hashCode(): Int {
        return millis.hashCode()
    }
}

/* LOCAL TIME */
////////////////////////////////////////////////////////////////////////////////////////////////////

interface LocalTimeCallBack {
    
    fun hour(date: Long): Int
    fun minute(date: Long): Int
    fun second(date: Long): Int
    fun plusHour(date: Long, count: Int): Long
    fun plusMinute(date: Long, count: Int): Long
    fun plusSecond(date: Long, count: Int): Long
    fun minusHour(date: Long, count: Int): Long
    fun minusMinute(date: Long, count: Int): Long
    fun minusSecond(date: Long, count: Int): Long
    fun compareTo(date: Long, comp: Long): Int
}

private val timeCallback = object: LocalTimeCallBack {
    override fun hour(date: Long) = part(date, "HH")
    override fun minute(date: Long) = part(date, "mm")
    override fun second(date: Long) = part(date, "ss")
    override fun plusHour(date: Long, count: Int) = plus(date, HOUR, count)
    override fun plusMinute(date: Long, count: Int) =
        plus(date, MINUTE, count)
    
    override fun plusSecond(date: Long, count: Int) =
        plus(date, SECOND, count)
    
    override fun minusHour(date: Long, count: Int) =
        plus(date, HOUR, -count)
    
    override fun minusMinute(date: Long, count: Int) =
        plus(date, MINUTE, -count)
    
    override fun minusSecond(date: Long, count: Int) =
        plus(date, SECOND, -count)
    
    override fun compareTo(date: Long, comp: Long) =
        Date(date).compareTo(Date(comp))
}

class LocalTime(private var millis: Long) {
    companion object {
        
        /** Создает объект с указанными данными **/
        fun of(time: String) = LocalTime(time.long())
        fun of(hour: Int, minute: Int = 0, second: Int = 0) =
            LocalTime("$hour:$minute:$second".pattern(TIME_FORMAT) - offset)
        
        fun ofZ(hour: Int, minute: Int = 0, second: Int = 0) =
            LocalTime("$hour:$minute:$second".pattern(TIME_FORMAT))
        
        fun ofZ(time: String) = LocalDateTime(
            of(time).millis - offset
        )
        
        /** Выдает экземпляр с текущей датой **/
        fun now() = LocalTime(c.time.time)
    }
    
    /** Возвращает дату в виде строки **/
    override fun toString() = Date(millis + offset).format(TIME_FORMAT)
    
    /** Приводит вид объекта к определенной форме. Возвращает строку **/
    fun format(pattern: String) = Date(millis).format(pattern)
    
    fun compareTo(comp: LocalTime) =
        timeCallback.compareTo(millis, comp.millis())
    
    /** Прибавляет к дате день/месяц/год **/
    fun plusHour(count: Int) =
        LocalTime(timeCallback.plusHour(millis, count))
    
    fun plusMinute(count: Int) =
        LocalTime(timeCallback.plusMinute(millis, count))
    
    fun plusSecond(count: Int) =
        LocalTime(timeCallback.plusSecond(millis, count))
    
    /** Отнимает от даты день/месяц/год **/
    fun minusHour(count: Int) =
        LocalTime(timeCallback.minusHour(millis, count))
    
    fun minusMinute(count: Int) =
        LocalTime(timeCallback.minusMinute(millis, count))
    
    fun minusSecond(count: Int) =
        LocalTime(timeCallback.minusSecond(millis, count))
    
    /** Возвращает день/месяц/год от даты или дату в милисекундах **/
    fun hour() = timeCallback.hour(millis)
    fun minute() = timeCallback.minute(millis)
    fun second() = timeCallback.second(millis)
    fun millis() = millis
}

/* LOCAL DATE_TIME */
////////////////////////////////////////////////////////////////////////////////////////////////////

val c: Calendar get() = getInstance()
val offset = c.get(ZONE_OFFSET)

class LocalDateTime(
    private val millis: Long,
) {
    
    /** Возвращает дату в виде строки **/
    override fun toString() = Date(millis + offset)
        .format(FULL_DATE_FORMAT_WIDTH_ZONE)
    
    companion object {
        
        /** Создает объект с указанными данными **/
        fun of(datetime: String) = LocalDateTime(datetime.long())
        
        fun ofZ(datetime: String) = LocalDateTime(datetime.long() + offset)
        
        /** Создает объект с указанными данными **/
        fun of(localDate: LocalDate, localTime: LocalTime) = of(
            "${localDate.year()}-${localDate.month()}-${
                localDate.day()
            }T${localTime.hour()}:${localTime.minute()}:${
                localTime.second()
            }"
        )
        
        /** Создает объект с указанными данными **/
        fun of(
            year: Int, month: Int = 1, day: Int = 1,
            hour: Int = 1, minute: Int = 1, second: Int = 1,
        ) = of("$year-$month-$day" + "T" + "$hour:$minute:$second")
        
        
        /** Выдает экземпляр с текущей датой **/
        fun now() = LocalDateTime(c.time.time)
        
        fun nowZ() = LocalDateTime(now().millis() - offset)
    }
    
    fun isBefore(to: LocalDateTime) =
        Date(millis).before(Date(to.millis()))
    
    /** Возвращает true если дата больше, чем указаная для сравнения */
    fun isAfter(to: LocalDateTime) = Date(millis).after(Date(to.millis()))
    
    /** Приводит вид объекта к определенной форме. Возвращает строку **/
    fun format(pattern: String) = Date(millis + offset).format(pattern)


    
    /** Возвращает указанную часть даты **/
    fun year() = dateCallback.year(millis)
    fun month() = dateCallback.month(millis)
    fun day() = dateCallback.day(millis)
    fun hour() = timeCallback.hour(millis)
    fun minute() = timeCallback.minute(millis)
    fun second() = timeCallback.second(millis)
    fun millis() = millis
    
    /** Прибавляет к дате выбранное количество единиц **/
    fun plusYear(count: Int) =
        LocalDateTime(dateCallback.plusYear(millis, count))
    
    fun plusMonth(count: Int) =
        LocalDateTime(dateCallback.plusMonth(millis, count))
    
    fun plusDay(count: Int) =
        LocalDateTime(dateCallback.plusDay(millis, count))
    
    fun plusHour(count: Int) =
        LocalDateTime(timeCallback.plusHour(millis, count))
    
    fun plusMinute(count: Int) =
        LocalDateTime(timeCallback.plusMinute(millis, count))
    
    fun plusSecond(count: Int) =
        LocalDateTime(timeCallback.plusSecond(millis, count))
    
    /** Отнимает от даты выбранное количество единиц **/
    fun minusYear(count: Int) =
        LocalDateTime(dateCallback.minusDay(millis, count))
    
    fun minusMonth(count: Int) =
        LocalDateTime(dateCallback.minusMonth(millis, count))
    
    fun minusDay(count: Int) =
        LocalDateTime(dateCallback.minusYear(millis, count))
    
    fun minusHour(count: Int) =
        LocalDateTime(timeCallback.minusHour(millis, count))
    
    fun minusMinute(count: Int) =
        LocalDateTime(timeCallback.minusMinute(millis, count))
    
    fun minusSecond(count: Int) =
        LocalDateTime(timeCallback.minusSecond(millis, count))
}

/* MONTH */
////////////////////////////////////////////////////////////////////////////////////////////////////

enum class Month {
    /** Январь месяц с 31 днем. Имеет числовое значение 1 **/
    JANUARY,
    
    /** Февраль месяц с 28 днями, или 29 в високосный год. Имеет числовое значение 2 **/
    FEBRUARY,
    
    /** Март месяц с 31 днями. Имеет числовое значение 3 **/
    MARCH,
    
    /** Апрель месяц с 30 днями. Имеет числовое значение 4 **/
    APRIL,
    
    /** Май месяц с 31 днями. Имеет числовое значение 5 **/
    MAY,
    
    /** Июнь месяц с 30 днями. Имеет числовое значение 6 **/
    JUNE,
    
    /** Июль месяц с 31 днями. Имеет числовое значение 7 **/
    JULY,
    
    /** Август месяц с 31 днями. Имеет числовое значение 8 **/
    AUGUST,
    
    /** Сентябрь месяц с 30 днями. Имеет числовое значение 9 **/
    SEPTEMBER,
    
    /** Октябрь месяц с 31 днями. Имеет числовое значение 10 **/
    OCTOBER,
    
    /** Ноябрь месяц с 30 днями. Имеет числовое значение 11 **/
    NOVEMBER,
    
    /** Декабрь месяц с 31 днями. Имеет числовое значение 12 **/
    DECEMBER;
    
    companion object {
        
        /** Частный кэш всех констант */
        private val enums = Month.values()
        
        /** Список всех месяцев на русском */
        private val names = arrayOf(
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
        
        private val rodNames = arrayOf(
            "Января",
            "Февраля",
            "Марта",
            "Апреля",
            "Мая",
            "Июня",
            "Июля",
            "Августа",
            "Сентября",
            "Октября",
            "Ноября",
            "Декабря"
        )
        
        /** Получает экземпляр Month из значения Int */
        fun of(month: Int): Month {
            return when {
                month <= 0 -> enums.last()
                month > 12 -> enums.first()
                else -> enums[month - 1]
            }
        }
        
        /** Возвращает месяц года, с 1 (январь) по 12 (декабрь) */
        fun Month.getValue(): Int = ordinal + 1
        
        /** Получает текстовое представление, такое как "Январь" или "Декабрь" */
        fun Month.display() = names[this.ordinal]
        
        fun Month.displayRodName() = rodNames[this.ordinal]
    }
}

/* DAY OF WEEK */
////////////////////////////////////////////////////////////////////////////////////////////////////

enum class DayOfWeek {
    /** Понедельник. Имеет числовое значение 1 */
    MONDAY,
    
    /** Вторник. Имеет числовое значение 2 */
    TUESDAY,
    
    /** Среда. Имеет числовое значение 3 */
    WEDNESDAY,
    
    /** Четверг. Имеет числовое значение 4 */
    THURSDAY,
    
    /** Пятница. Имеет числовое значение 5 */
    FRIDAY,
    
    /** Суббота. Имеет числовое значение 6 */
    SATURDAY,
    
    /** Воскресенье. Имеет числовое значение 7 */
    SUNDAY;
    
    companion object {
        
        /** Частный кэш всех констант */
        private val enums = values()
        
        /** Список всех дней недели на русском */
        private val names = arrayOf(
            "Понедельник", "Вторник", "Среда",
            "Четверг", "Пятница", "Суббота", "Воскресенье",
        )
        
        private val rodNames = arrayOf(
            "Понедельник", "Вторник", "Среду",
            "Четверг", "Пятницу", "Субботу", "Воскресенье",
        )
        
        private val shortRuNames = arrayOf(
            "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
        )
        
        fun DayOfWeek.displayShort() = shortRuNames[this.ordinal]
        fun DayOfWeek.displayRodName() = rodNames[this.ordinal]
        
        fun list() = values().toList()
        
        fun valueList() = values().map {
            it.displayShort().uppercase()
        }
        
        private val shortNames = arrayOf(
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        )
        
        fun dayOfWeek(day: String): DayOfWeek {
            shortNames.forEachIndexed { i, it ->
                if(it == day) return enums[i]
            }; return MONDAY
        }
        
        /** Получает экземпляр DayOfWeek из значения Int */
        fun of(index: Int): DayOfWeek {
            return when {
                index <= 0 -> DayOfWeek.enums.last()
                index > 12 -> DayOfWeek.enums.first()
                else -> DayOfWeek.enums[index - 1]
            }
        }
        
        /** Возвращает день недели, с 1 (Понедельник) по 7 (Воскресенье) */
        fun DayOfWeek.value() = ordinal + 1
        
        /** Получает текстовое представление, такое как "Понедельник" или "Воскресенье" */
        fun DayOfWeek.display() = names[this.ordinal]
    }
}

/* COMMON FUNCTIONS */
////////////////////////////////////////////////////////////////////////////////////////////////////

/** Возвращает часть даты указанную в патерне в числовом значении **/
private fun part(date: Long, pattern: String) =
    Date(date).format(pattern).toInt()

/** Изменяет дату в определенном поле на указанное кол-во единиц **/
private fun plus(date: Long, field: Int, count: Int): Long {
    val c = getInstance()
    c.time = Date(date)
    c.add(field, count)
    return c.time.time
}

/** Задает определенный формат для объекта класса Date(). Возвращает строку**/
@SuppressLint("SimpleDateFormat")
fun Date.format(pattern: String): String = try {
    SimpleDateFormat(pattern).format(this)
} catch(e: Exception) {
    this.toString()
}

/** Задает определенный формат для даты в строке в формате ISO-8601. Возвращает строку**/
fun String.format(pattern: String): String =
    Date(this.long()).format(pattern)

fun LocalDateTime.display(): String =
    this.format("d 'M' yyyy, HH:mm")
        .replace("M", Month.of(this.month()).display())

fun LocalDateTime.displayDate(): String =
    this.format("d 'M' yyyy")
        .replace("M", Month.of(this.month()).display())

fun Long.toDays() = this / 86_400_000

fun Long.toMinutes() = this / 60_000

class Duration {
    companion object {
        
        fun between(from: LocalDateTime, to: LocalDateTime) =
            abs(from.millis() - to.millis())
        
        fun between(from: LocalTime, to: LocalTime) =
            abs(from.millis() - to.millis())
        
        fun between(from: LocalDate, to: LocalDate) =
            abs(from.millis() - to.millis())
    }
}

/** Преобразует строку с датой в формате ISO-8601 в Long **/
fun String.long(): Long {
    return try {
        this.pattern(FULL_DATE_FORMAT)
    } catch(e: Exception) {
        try {
            this.pattern(TIME_FORMAT)
        } catch(e: Exception) {
            this.pattern(DATE_FORMAT)
        }
    } - this.timeZoneOffset()
}

private fun String.timeZoneOffset(): Long {
    val tzText = when {
        this.contains('Z') -> "+00:00"
        this.contains('+') -> this.timeZone('+')
        else -> {
            val last = this.timeZone('-')
            if(last.length != 5) "+00:00"
            else last
        }
    }
    
    return tzText.getLongTime(tzText.first())
}

private fun String.getLongTime(sign: Char): Long {
    val hours = (this.substring(1, 3).toInt())
    val minutes = (this.substring(4, 6).toInt())
    val long = (hours * 3_600_000).toLong() + (minutes * 60000)
    return if(sign == '-') (-1) * long else long
}

private fun String.timeZone(symbol: Char) =
    this.substring(this.lastIndexOf(symbol), this.length)

@SuppressLint("SimpleDateFormat")
fun String.format(from: String, to: String) =
    SimpleDateFormat(from)
        .parse(this)
        ?.format(to)
        ?: ""

@SuppressLint("SimpleDateFormat")
fun String.pattern(pattern: String): Long =
    SimpleDateFormat(pattern).parse(this)?.time ?: 0L