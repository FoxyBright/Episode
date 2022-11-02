package ru.rikmasters.gilty.notifications.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun NotificationText() {
    GiltyTheme {
        val notification = DemoNotificationMeetingOverModel
        NotificationText(
            notification.meeting.organizer, notification.type,
            notification.meeting, getDifferenceOfTime(notification.date),
            Modifier
                .padding(end = 20.dp)
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
fun NotificationText(
    organizer: OrganizerModel,
    type: NotificationType,
    meet: MeetingModel,
    duration: String,
    modifier: Modifier = Modifier
) {
    val user = "${organizer.username}, ${organizer.age}"
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val sbText = typography.labelSmall.copy(fontWeight = SemiBold)
    val textStyle = typography.labelSmall.copy(colors.tertiary).toSpanStyle()
    val boldTextStyle = sbText.copy(colors.tertiary).toSpanStyle()
    val meetingStyle = sbText.copy(colors.primary).toSpanStyle()
    val organizerStyle =
        typography.bodyMedium.copy(colors.tertiary, fontWeight = Bold).toSpanStyle()
    val timeStyle = typography.labelSmall.copy(colors.onTertiary).toSpanStyle()
    val message = when (type) {
        NotificationType.MEETING_OVER -> buildAnnotatedString {
            withStyle(textStyle) { append(stringResource(R.string.notification_meeting_took_place)) }
            withStyle(meetingStyle) { append(" ${meet.title}") }
            withStyle(textStyle) { append(stringResource(R.string.notification_words_connector)) }
            withStyle(organizerStyle) { append("$user. ") }
            withStyle(boldTextStyle) {
                append("${stringResource(R.string.notification_leave_impressions)}. ")
            }; withStyle(timeStyle) { append(duration) }
        }

        NotificationType.RESPOND_ACCEPT -> buildAnnotatedString {
            withStyle(organizerStyle) { append("$user ") }
            withStyle(textStyle) { append(stringResource(R.string.notification_meet_is_accept)) }
            withStyle(meetingStyle) { append(" ${meet.title}") }
            withStyle(boldTextStyle) { append(". ") }
            withStyle(timeStyle) { append(duration) }
        }

        NotificationType.LEAVE_EMOTIONS -> buildAnnotatedString {
            withStyle(textStyle) { append(stringResource(R.string.notification_leave_emotion)) }
            withStyle(meetingStyle) { append(" ${meet.title} ") }
            withStyle(textStyle) {
                append(
                    "${
                        meet.dateTime.format("dd MMMM")
                    }. ${stringResource(R.string.notification_organizer_label)} "
                )
            }
            withStyle(organizerStyle) { append("$user ") }
            withStyle(timeStyle) { append(duration) }
        }
    }
    Text(message, modifier)
}