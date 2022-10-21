package ru.rikmasters.gilty.presentation.ui.presentation.notification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.presentation.model.profile.notification.NotificationModel
import ru.rikmasters.gilty.utility.extentions.format
import ru.rikmasters.gilty.utility.extentions.toEpochMillis
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

private const val FORMAT = "yyyy-MM-dd"

fun getDifferenceOfTime(date: String): String {
    val now = LocalDate.now()
    val dateList = date.format(FORMAT).split("-")
    val localDate = LocalDate.of(
        dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
    )

    return when {
        now == localDate -> "10 ч"
        now != localDate -> "11 ч"
        else -> ""
    }
}

class NotificationsByDateSeparator(private val notifications: List<NotificationModel>) {

    private enum class Groups { Today, Week, Early }


    fun getTodayList(): List<NotificationModel> {
        return cycle(Groups.Today)
    }

    fun getWeekList(): List<NotificationModel> {
        return cycle(Groups.Week)
    }

    fun getEarlyList(): List<NotificationModel> {
        return cycle(Groups.Early)
    }

    private fun cycle(group: Groups): List<NotificationModel> {
        val list = arrayListOf<NotificationModel>()
        when (group) {
            Groups.Today -> {
                notifications.forEach {
                    if (todayControl(it.date)) list.add(it)
                }
            }

            Groups.Week -> {
                notifications.forEach {
                    if (weekControl(it.date) && !todayControl(it.date)) list.add(it)
                }
            }

            Groups.Early -> {
                notifications.forEach {
                    if (!weekControl(it.date) && !todayControl(it.date)) list.add(it)
                }
            }
        }
        return list
    }

    private fun todayControl(date: String): Boolean {
        return date.format(FORMAT) == LocalDateTime.now().format(FORMAT)
    }

    private fun weekControl(date: String): Boolean {
        val dateList = date.format(FORMAT).split("-")
        val localDate = LocalDate.of(
            dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
        ).toEpochMillis() / 1000
        return (localDate in thisWeek().first..thisWeek().second)
    }

    private fun thisWeek(): Pair<Long, Long> {
        val format = DateTimeFormatter.ofPattern("dd")
        val now = LocalDate.now()
        val start = LocalDate.of(
            now.year,
            now.month,
            format.format(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                .toInt()
        )
        return Pair(
            start.toEpochMillis() / 1000, LocalDate.of(
                now.year, now.month, format.format(start.plusDays(7)).toInt()
            ).toEpochMillis() / 1000
        )
    }
}

fun Modifier.swipeableRow(
    state: DragRowState,
    onSwiped: (RowDirection) -> Unit,
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures({}, {
            launch {
                val coercedOffset = state.offset.targetValue.coerceIn(state.maxWidth)
                if (abs(coercedOffset.x) < state.maxWidth / 4) state.reset()
                else {
                    if (state.offset.targetValue.x < 0) {
                        state.swipe(RowDirection()); state.reset(); onSwiped(RowDirection())
                    }
                }
            }
        }, { launch { state.reset() } }, { change, dragAmount ->
            launch {
                if (change.positionChange() != Offset.Zero) change.consume()
                state.drag((state.offset.targetValue + dragAmount).x.coerceIn(-state.maxWidth, 0f))
            }
        }
        )
    }
}.graphicsLayer { translationX = state.offset.value.x }

fun Offset.coerceIn(maxWidth: Float): Offset {
    return copy(x.coerceIn(-maxWidth, 0f))
}

class RowDirection

@Composable
fun rememberDragRowState(): DragRowState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    return remember { DragRowState(screenWidth) }
}

class DragRowState(val maxWidth: Float) {
    val offset = Animatable(offset(0f), Offset.VectorConverter)
    private var swipedDirection: RowDirection? by mutableStateOf(null)
    suspend fun reset() {
        offset.animateTo(offset(0f), tween(400))
    }

    suspend fun swipe(direction: RowDirection) {
        if (direction == RowDirection())
            offset.animateTo(offset(-(maxWidth * 1.5f)), tween(400))
        this.swipedDirection = direction
    }

    private fun offset(x: Float = offset.value.x): Offset {
        return Offset(x, 0f)
    }

    suspend fun drag(x: Float) {
        offset.animateTo(offset(x))
    }
}