package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.TextCenter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_MEMBER
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.*
import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
private fun PreviewHelper(
    type: NotificationType,
    adminText: String? = null,
    emoji: List<EmojiModel> = emptyList(),
    isOrganizer: Boolean = false,
    isOnline: Boolean = false,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        Column {
            Text(type.name, Modifier.padding(6.dp))
            NotificationText(
                DemoUserModel, type,
                DemoMeetingModel.copy(
                    memberState = if(isOrganizer)
                        IS_ORGANIZER else IS_MEMBER,
                    isOnline = isOnline
                ), ("11 мин"),
                Modifier.padding(16.dp, 4.dp),
                adminText, emoji
            )
        }
    }
}

@Preview
@Composable
private fun TextNotificationPreview() {
    GiltyTheme {
        Column {
            PreviewHelper(WATCH_MEETING_CREATED)
            PreviewHelper(TRANSLATION_STARTED, isOnline = true)
            PreviewHelper(TRANSLATION_15MIN, isOnline = true)
            PreviewHelper(MEETING_CANCELED)
            PreviewHelper(MEETING_KICKED)
            PreviewHelper(RESPOND)
            PreviewHelper(WATCH)
        }
    }
}

@Preview
@Composable
private fun InfoNotificationPreview() {
    GiltyTheme {
        Column {
            PreviewHelper(
                ADMIN_NOTIFICATION,
                ("Внимание! Ведутся технические " +
                        "работы, возможны сбои в приложении")
            )
            PreviewHelper(
                PHOTO_BLOCKED,
                stringResource(R.string.notification_meet_PHOTO_BLOCKED)
            )
        }
    }
}

@Preview
@Composable
private fun MEETING_OVER() {
    GiltyTheme {
        Column {
            PreviewHelper(
                MEETING_OVER,
                isOrganizer = true,
                isOnline = true,
                emoji = emptyList()
            )
            PreviewHelper(
                MEETING_OVER,
                isOrganizer = true,
                isOnline = true,
                emoji = listOf(DemoEmojiModel)
            )
            PreviewHelper(
                MEETING_OVER,
                isOrganizer = false,
                isOnline = false,
                emoji = emptyList()
            )
            PreviewHelper(
                MEETING_OVER,
                isOrganizer = false,
                isOnline = false,
                emoji = listOf(DemoEmojiModel)
            )
        }
    }
}

@Composable
fun NotificationText(
    organizer: UserModel?,
    type: NotificationType,
    meet: MeetingModel?,
    duration: String,
    modifier: Modifier = Modifier,
    notification: String? = null,
    emoji: List<EmojiModel>? = null,
    onMeetClick: (() -> Unit)? = null,
    onUserClick: (() -> Unit)? = null,
) {
    val user = "${organizer?.username}, ${organizer?.age}"
    val message = buildAnnotatedString {
        when(type) {
            MEETING_OVER -> if(meet?.memberState == IS_ORGANIZER) if(emoji.isNullOrEmpty()) {
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meeting_took_place_for_organizer)) }
                withStyle(style(MEET, meet.isOnline)) { append(" ${meet.title}") }
                withStyle(style(TEXT)) { append("? ") }
                withStyle(style(BOLD)) {
                    append(stringResource(R.string.notification_leave_members_impressions))
                }
                withStyle(style(TIME)) { append(" $duration") }
            } else {
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meeting_emoji_for_members)) }
                withStyle(style(MEET, meet.isOnline)) { append(" ${meet.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            else if(emoji.isNullOrEmpty()) {
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meeting_took_place)) }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_words_connector)) }
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(BOLD)) {
                    append(stringResource(R.string.notification_leave_meet_impressions))
                }
                withStyle(style(TIME)) { append(" $duration") }
            } else {
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meeting_emoji_for_meet)) }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title} ") }
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_organizer_label)) }
                withStyle(style(USER)) { append(" $user ") }
                appendInlineContent("emoji")
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            RESPOND_ACCEPTED -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(
                        stringResource(
                            R.string.notification_meet_RESPOND_ACCEPTED,
                            genderEnding(organizer?.gender, RESPOND_ACCEPTED)
                        )
                    )
                }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            RESPOND -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(
                        stringResource(
                            R.string.notification_meet_RESPOND,
                            genderEnding(organizer?.gender, RESPOND)
                        )
                    )
                }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            MEETING_CANCELED -> {
                withStyle(style(MEET, meet?.isOnline)) { append("${meet?.title} ") }
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meet_MEETING_CANCELED_start)) }
                withStyle(style(USER)) { append(" $user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(
                        stringResource(
                            R.string.notification_meet_MEETING_CANCELED_end,
                            genderEnding(organizer?.gender, MEETING_CANCELED)
                        )
                    )
                }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            MEETING_KICKED -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(
                        stringResource(
                            R.string.notification_meet_MEETING_KICKED,
                            genderEnding(organizer?.gender, MEETING_KICKED)
                        )
                    )
                }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            WATCH -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(" " + stringResource(R.string.notification_meet_WATCH))
                }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            TRANSLATION_15MIN -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meet_TRANSLATION_15MIN_start)) }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title} ") }
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meet_TRANSLATION_15MIN_end)) }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            ADMIN_NOTIFICATION, PHOTO_BLOCKED -> {
                withStyle(style(TEXT)) { append(notification) }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            WATCH_MEETING_CREATED -> {
                withStyle(style(USER)) { append("$user ") }
                appendInlineContent("emoji")
                withStyle(style(TEXT)) {
                    append(
                        stringResource(
                            R.string.notification_meet_WATCH_MEETING_CREATED,
                            genderEnding(organizer?.gender, WATCH_MEETING_CREATED)
                        )
                    )
                }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}") }
                withStyle(style(TIME)) { append(" $duration") }
            }
            
            TRANSLATION_STARTED -> {
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meet_ADMIN_NOTIFICATION_start)) }
                withStyle(style(MEET, meet?.isOnline)) { append(" ${meet?.title}. ") }
                withStyle(style(TEXT)) { append(stringResource(R.string.notification_meet_ADMIN_NOTIFICATION_end)) }
                withStyle(style(TIME)) { append(" $duration") }
            }
        }
        val annotated = this.toAnnotatedString()
        val userPlace = annotated.indexOf(user)
        val meetPlace = annotated.indexOf("${meet?.title}")
        addStringAnnotation(
            ("user"), (""), userPlace,
            (userPlace + user.length + 2)
        )
        addStringAnnotation(
            ("meet"), (""), meetPlace,
            (meetPlace + "${meet?.title}".length)
        )
    }
    LinkedText(
        message, modifier, mapOf(
            "emoji" to InlineTextContent(
                Placeholder(18.sp, 18.sp, TextCenter)
            ) { GEmojiImage(organizer?.emoji, Modifier.size(18.dp)) }
        )
    ) {
        message.getStringAnnotations(("meet"), it, it)
            .firstOrNull()?.let { onMeetClick?.let { it() } }
        message.getStringAnnotations(("user"), it, it)
            .firstOrNull()?.let { onUserClick?.let { it() } }
    }
}

private enum class CustomText { USER, TEXT, MEET, TIME, BOLD }

@Composable
private fun style(
    text: CustomText,
    online: Boolean? = false,
) = when(text) {
    USER -> font(weight = Bold, textStyle = typography.bodyMedium)
    BOLD -> font(weight = SemiBold)
    TEXT -> font()
    TIME -> font(colorScheme.onTertiary)
    MEET -> font(
        if(online == true) colorScheme.secondary
        else colorScheme.primary, weight = SemiBold
    )
}.toSpanStyle()

private fun genderEnding(
    genderType: GenderType?,
    notType: NotificationType,
) = when(notType) {
    RESPOND -> if(genderType == FEMALE) "ась" else "ся"
    RESPOND_ACCEPTED, MEETING_CANCELED, MEETING_KICKED, WATCH_MEETING_CREATED ->
        if(genderType == FEMALE) "а" else ""
    
    else -> ""
}

@Composable
private fun font(
    color: Color = colorScheme.tertiary,
    weight: FontWeight = Medium,
    textStyle: TextStyle = typography.labelSmall,
) = textStyle.copy(color, fontWeight = weight)

@Composable
private fun LinkedText(
    text: AnnotatedString,
    modifier: Modifier,
    inlineContent: Map<String, InlineTextContent>,
    onClick: (Int) -> Unit,
) {
    val layoutResult = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }
    BasicText(
        text, modifier.pointerInput(onClick) {
            detectTapGestures { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            }
        }, onTextLayout = {
            val onText: (TextLayoutResult) -> Unit = {}
            layoutResult.value = it
            onText(it)
        }, inlineContent = inlineContent
    )
}