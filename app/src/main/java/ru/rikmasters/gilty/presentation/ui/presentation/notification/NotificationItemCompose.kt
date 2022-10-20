package ru.rikmasters.gilty.presentation.ui.presentation.notification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.NotificationType
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel
import ru.rikmasters.gilty.presentation.model.profile.notification.DemoNotificationRespondAcceptModel
import ru.rikmasters.gilty.presentation.model.profile.notification.NotificationModel
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import ru.rikmasters.gilty.utility.extentions.format
import kotlin.math.abs

@Preview
@Composable
fun NotificationItemPreview() {
    NotificationItem(
        NotificationItemState(
            DemoNotificationRespondAcceptModel,
            DragRowState(600f),
            MaterialTheme.shapes.medium,
            "${DemoNotificationRespondAcceptModel.date.format("HH")} ч"
        )
    )
}

data class NotificationItemState(
    val notification: NotificationModel,
    val rowState: DragRowState,
    val shape: Shape,
    val duration: String
)

interface NotificationItemCallback {
    fun onSwiped(rowDirection: RowDirection) {}
    fun onSwipeCancel() {}
    fun onClick(notification: NotificationModel) {}
    fun onEmojiClick(emoji: EmojiModel) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationItem(
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    callback: NotificationItemCallback? = null
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, state.shape)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.ic_trash_can),
                null, Modifier, Color.White
            )
            Text(
                stringResource(R.string.meeting_filter_delete_tag_label),
                Modifier.padding(),
                Color.White,
                style = ThemeExtra.typography.SubHeadSb
            )
        }
        Row(
            Modifier.swipeableRow(
                state.rowState,
                { callback?.onSwiped(RowDirection.Left) },
                { callback?.onSwipeCancel() }),
            Arrangement.Center, Alignment.CenterVertically
        ) {
            Card(
                { callback?.onClick(state.notification) },
                Modifier.fillMaxWidth(), true, state.shape,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        state.notification.meeting.organizer.avatar.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(12.dp, 8.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        painterResource(R.drawable.gb),
                        contentScale = ContentScale.FillBounds
                    )
                    val organizer = state.notification.meeting.organizer
                    NotificationText(
                        "${organizer.username}, ${organizer.age}",
                        organizer.emoji,
                        state.notification.type,
                        state.notification.meeting.title,
                        state.duration
                    )
                    if (state.notification.type == NotificationType.MEETING_OVER) {
                        Icon(
                            painterResource(R.drawable.ic_cloud_part),
                            null,
                            Modifier
                                .size(18.dp)
                                .padding(start = 50.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationText(
    user: String,
    emoji: EmojiModel,
    type: NotificationType,
    meet: String,
    duration: String
) {
    val messageText = when (type) {
        NotificationType.MEETING_OVER -> buildAnnotatedString {
            withStyle(
                ThemeExtra.typography.SubHeadMedium.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(stringResource(R.string.notification_meeting_took_place)) }
            withStyle(
                ThemeExtra.typography.SubHeadSb.copy(color = MaterialTheme.colorScheme.primary)
                    .toSpanStyle()
            ) { append(" $meet") }
            withStyle(
                ThemeExtra.typography.SubHeadMedium.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(stringResource(R.string.notification_words_connector)) }
            withStyle(
                ThemeExtra.typography.Body1Bold.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append("$user. ") }
            withStyle(
                ThemeExtra.typography.SubHeadSb.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(stringResource(R.string.notification_leave_impressions)) }
            withStyle(
                ThemeExtra.typography.SubHeadSb.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(". ") }
            withStyle(
                ThemeExtra.typography.SubHeadMedium.copy(color = ThemeExtra.colors.secondaryTextColor)
                    .toSpanStyle()
            ) { append(duration) }
        }

        NotificationType.RESPOND_ACCEPT -> buildAnnotatedString {
            withStyle(
                ThemeExtra.typography.Body1Bold.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append("$user ") }
            withStyle(
                ThemeExtra.typography.SubHeadMedium.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(stringResource(R.string.notification_meet_is_accept)) }
            withStyle(
                ThemeExtra.typography.SubHeadSb.copy(color = MaterialTheme.colorScheme.primary)
                    .toSpanStyle()
            ) { append(" $meet") }
            withStyle(
                ThemeExtra.typography.SubHeadSb.copy(color = ThemeExtra.colors.mainTextColor)
                    .toSpanStyle()
            ) { append(". ") }
            withStyle(
                ThemeExtra.typography.SubHeadMedium.copy(color = ThemeExtra.colors.secondaryTextColor)
                    .toSpanStyle()
            ) { append(duration) }
        }
    }
    Text(
        messageText,
        Modifier
            .padding(end = 20.dp)
            .padding(vertical = 12.dp)
    )
}

fun Modifier.swipeableRow(
    state: DragRowState,
    onSwiped: (RowDirection) -> Unit,
    onSwipeCancel: () -> Unit = {},
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures(
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue.coerceIn(state.maxWidth)
                    if (abs(coercedOffset.x) < state.maxWidth / 4) {
                        state.reset()
                        onSwipeCancel()
                    } else {
                        if (state.offset.targetValue.x > 0) {
                            state.swipe(RowDirection.Right)
                            onSwiped(RowDirection.Right)
                        } else {
                            state.swipe(RowDirection.Left)
                            onSwiped(RowDirection.Left)
                        }
                    }
                }
            },
            onDragCancel = { launch { state.reset(); onSwipeCancel() } },
            onDrag = { change, dragAmount ->
                launch {
                    val original = state.offset.targetValue
                    val summed = original + dragAmount
                    val newValue = Offset(summed.x.coerceIn(-state.maxWidth, state.maxWidth), 0f)
                    if (change.positionChange() != Offset.Zero) change.consume()
                    state.drag(newValue.x)
                }
            }
        )
    }
}.graphicsLayer { translationX = state.offset.value.x }

fun Offset.coerceIn(maxWidth: Float): Offset {
    return copy(x.coerceIn(-maxWidth, maxWidth))
}

enum class RowDirection { Left, Right }

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

    suspend fun swipe(direction: RowDirection, animationSpec: AnimationSpec<Offset> = tween(400)) {
        val endX = maxWidth * 1.5f
        when (direction) {
            RowDirection.Left -> offset.animateTo(offset(x = -endX), animationSpec)
            RowDirection.Right -> offset.animateTo(offset(x = endX), animationSpec)
        }
        this.swipedDirection = direction
    }

    private fun offset(x: Float = offset.value.x): Offset {
        return Offset(x, 0f)
    }

    suspend fun drag(x: Float) {
        offset.animateTo(offset(x))
    }
}