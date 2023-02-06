package ru.rikmasters.gilty.notifications.presentation.ui.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.item.CustomText.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.dateCalendar
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.LEAVE_EMOTIONS
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_OVER
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.RESPOND_ACCEPT
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun LEAVE_EMOTIONS() {
    GiltyTheme {
        NotificationText(
            DemoOrganizerModel, LEAVE_EMOTIONS, DemoMeetingModel,
            getDifferenceOfTime(DemoMeetingModel.datetime),
            Modifier.padding(20.dp)
        )
    }
}

@Preview
@Composable
private fun MEETING_OVER() {
    GiltyTheme {
        NotificationText(
            DemoOrganizerModel, MEETING_OVER, DemoMeetingModel,
            getDifferenceOfTime(DemoMeetingModel.datetime),
            Modifier.padding(20.dp)
        )
    }
}

@Preview
@Composable
private fun RESPOND_ACCEPT() {
    GiltyTheme {
        NotificationText(
            DemoOrganizerModel, RESPOND_ACCEPT, DemoMeetingModel,
            getDifferenceOfTime(DemoMeetingModel.datetime),
            Modifier.padding(20.dp)
        )
    }
}


@Composable
fun NotificationText(
    organizer: OrganizerModel?,
    type: NotificationType,
    meet: MeetingModel,
    duration: String,
    modifier: Modifier = Modifier,
) {
    val user = "${organizer?.username}, ${organizer?.age}"
    val message = when(type) {
        MEETING_OVER -> buildAnnotatedString {
            withStyle(text(TEXT)) { append(stringResource(R.string.notification_meeting_took_place)) }
            withStyle(text(MEET)) { append(" ${meet.title}") }
            withStyle(text(TEXT)) { append(stringResource(R.string.notification_words_connector)) }
            withStyle(text(USER)) { append("$user. ") }
            withStyle(text(BOLD)) {
                append("${stringResource(R.string.notification_leave_impressions)}. ")
            }; withStyle(text(TIME)) { append(duration) }
        }
        
        RESPOND_ACCEPT -> buildAnnotatedString {
            withStyle(text(USER)) { append("$user ") }
            withStyle(text(TEXT)) { append(stringResource(R.string.notification_meet_is_accept)) }
            withStyle(text(MEET)) { append(" ${meet.title}") }
            withStyle(text(BOLD)) { append(". ") }
            withStyle(text(TIME)) { append(duration) }
        }
        
        LEAVE_EMOTIONS -> buildAnnotatedString {
            withStyle(text(TEXT)) { append(stringResource(R.string.notification_leave_emotion)) }
            withStyle(text(MEET)) { append(" ${meet.title} ") }
            withStyle(text(TEXT)) {
                append(
                    "${
                        meet.datetime.dateCalendar()
                    }. ${stringResource(R.string.notification_organizer_label)} "
                )
            }
            withStyle(text(USER)) { append("$user ") }
            withStyle(text(TIME)) { append(duration) }
        }
    }
    Text(message, modifier)
}

private enum class CustomText { USER, TEXT, MEET, TIME, BOLD }

@Composable
private fun text(text: CustomText): SpanStyle {
    val colors = colorScheme
    val font: TextStyle = when(text) {
        USER -> font(weight = Bold, textStyle = typography.bodyMedium)
        MEET -> font(colors.primary, weight = SemiBold)
        TIME -> font(colors.onTertiary)
        BOLD -> font(weight = SemiBold)
        TEXT -> font()
    }; return font.toSpanStyle()
}

@Composable
private fun font(
    color: Color = colorScheme.tertiary,
    weight: FontWeight = Medium,
    textStyle: TextStyle = typography.labelSmall,
): TextStyle {
    return textStyle.copy(color, fontWeight = weight)
}