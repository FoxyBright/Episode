package ru.rikmasters.gilty.presentation.ui.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.profile.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.presentation.model.profile.notification.DemoNotificationRespondAcceptModel
import ru.rikmasters.gilty.presentation.model.profile.notification.DemoTodayNotificationMeetingOver
import ru.rikmasters.gilty.presentation.model.profile.notification.DemoTodayNotificationRespondAccept
import ru.rikmasters.gilty.presentation.model.profile.notification.NotificationModel
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(showBackground = true)
@Composable
fun NotificationsComposePreview() {
    GiltyTheme {
        val notificationsList = remember {
            mutableListOf(
                DemoNotificationRespondAcceptModel,
                DemoNotificationMeetingOverModel,
                DemoTodayNotificationRespondAccept,
                DemoTodayNotificationMeetingOver,
            )
        }
        NotificationsCompose(Modifier, NotificationsState(notificationsList))
    }
}

data class NotificationsState(
    val notificationsList: List<NotificationModel> = listOf()
)

@Composable
fun NotificationsCompose(
    modifier: Modifier = Modifier,
    state: NotificationsState,
    callback: NotificationItemCallback? = null
) {
    val separator = NotificationsByDateSeparator(state.notificationsList)
    val todayList = separator.getTodayList().map { it to rememberDragRowState() }
    val weekList = separator.getWeekList().map { it to rememberDragRowState() }
    val earlierList = separator.getEarlyList().map { it to rememberDragRowState() }
    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            stringResource(R.string.notification_screen_name),
            Modifier.padding(top = 80.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.H1
        )
        LazyColumn(Modifier.fillMaxSize(), rememberLazyListState()) {
            if (todayList.isNotEmpty()) {
                item { label(stringResource(R.string.meeting_profile_bottom_today_label)) }
                itemsIndexed(todayList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            shape(index, todayList.size),
                            "11 ч"
                        ),
                    )
                }
            }
            if (weekList.isNotEmpty()) {
                item { label(stringResource(R.string.notification_on_this_week_label)) }
                itemsIndexed(weekList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            shape(index, weekList.size),
                            "11 ч"
                        )
                    )
                }
            }
            if (earlierList.isNotEmpty()) {
                item { label(stringResource(R.string.notification_earlier_label)) }
                itemsIndexed(earlierList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            shape(index, earlierList.size),
                            "11 ч"
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun label(text: String) {
    Text(
        text, Modifier.padding(vertical = 20.dp),
        ThemeExtra.colors.mainTextColor,
        style = ThemeExtra.typography.H3
    )
}

@Composable
private fun shape(index: Int, size: Int): Shape {
    return if (size != 1)
        return when (index) {
            0 -> ThemeExtra.shapes.mediumTopRoundedShape
            size - 1 -> ThemeExtra.shapes.mediumBottomRoundedShape
            else -> RoundedCornerShape(0.dp)
        } else MaterialTheme.shapes.medium
}