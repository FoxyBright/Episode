package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.TextCenter
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.BOLD
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.MEET
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.TEXT
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.TIME
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.CustomText.USER
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_MEMBER
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.ADMIN_NOTIFICATION
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_CANCELED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_KICKED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_OVER
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.PHOTO_BLOCKED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.RESPOND
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.RESPOND_ACCEPTED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.TRANSLATION_15MIN
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.TRANSLATION_STARTED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.WATCH
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.WATCH_MEETING_CREATED
import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
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
                modifier = Modifier.padding(16.dp, 4.dp),
                notification = adminText, emoji = emoji,
                organizer = DemoUserModel,
                duration = ("11 мин"),
                type = type,
                meet = DemoMeetingModel.copy(
                    memberState = if (isOrganizer)
                        IS_ORGANIZER else IS_MEMBER,
                    isOnline = isOnline
                )
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
                type = ADMIN_NOTIFICATION,
                adminText = "Внимание! Ведутся технические " +
                        "работы, возможны сбои в приложении"
            )
            PreviewHelper(
                type = PHOTO_BLOCKED,
                adminText = stringResource(
                    R.string.notification_meet_PHOTO_BLOCKED
                )
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
    LinkText(
        message = getMessage(
            type = type,
            organizer = organizer,
            notification = notification,
            meet = meet,
            duration = duration,
            user = "${organizer?.username}${
                if (organizer?.age in 18..99) {
                    ", ${organizer?.age}"
                } else ""
            }"
        ),
        modifier = modifier,
        organizer = organizer,
        onMeetClick = onMeetClick,
        onUserClick = onUserClick
    )
}

@Composable
private fun getMessage(
    type: NotificationType,
    organizer: UserModel?,
    notification: String?,
    meet: MeetingModel?,
    duration: String,
    user: String,
) = buildAnnotatedString {
    when (type) {
        RESPOND_ACCEPTED ->
            RespondAccept(user, organizer, meet, duration)

        WATCH_MEETING_CREATED ->
            WatchMeetingCreated(user, organizer, meet, duration)

        MEETING_CANCELED ->
            MeetingCanceled(meet, user, organizer, duration)

        MEETING_KICKED ->
            MeetingKicked(user, organizer, meet, duration)

        RESPOND ->
            Respond(user, organizer, meet, duration)

        TRANSLATION_15MIN ->
            Translation15Min(user, organizer, meet, duration)

        TRANSLATION_STARTED ->
            TranslationStarted(meet, duration)

        MEETING_OVER ->
            MeetingOver(meet, organizer, user, duration)

        ADMIN_NOTIFICATION, PHOTO_BLOCKED ->
            System(notification, duration)

        WATCH -> Watch(user, organizer, duration)
    }
    getLinkAnnotation(user, meet)
}

@Composable
private fun Builder.MeetingOver(
    meet: MeetingModel?,
    user: String,
    organizer: UserModel?,
    duration: String,
    emoji: List<EmojiModel>?,
) {
    if (meet?.memberState == IS_ORGANIZER)
        if (emoji.isNullOrEmpty())
            MyMeetingOver(meet, duration)
        else MyMeetingOverWithEmoji(meet, duration)
    else if (emoji.isNullOrEmpty())
        MeetingOver(meet, organizer, user, duration)
    else MeetingOverWithEmoji(meet, organizer, user, duration)
}

@Composable
private fun Builder.MeetingOver(
    meet: MeetingModel?,
    organizer: UserModel?,
    user: String,
    duration: String,
) {
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meeting_took_place))
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TEXT)) { append(stringResource(R.string.notification_words_connector)) }
    withStyle(style(USER)) { append("$user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(BOLD)) {
        append(stringResource(R.string.notification_leave_meet_impressions))
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.MeetingOverWithEmoji(
    meet: MeetingModel?,
    organizer: UserModel?,
    user: String,
    duration: String,
) {
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meeting_emoji_for_meet))
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title} ")
    }
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_organizer_label))
    }
    withStyle(style(USER)) { append(" $user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.MyMeetingOver(
    meet: MeetingModel,
    duration: String,
) {
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meeting_took_place_for_organizer))
    }
    withStyle(style(MEET, meet.isOnline)) {
        append(" ${meet.title}")
    }
    withStyle(style(TEXT)) { append("? ") }
    withStyle(style(BOLD)) {
        append(stringResource(R.string.notification_leave_members_impressions))
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.MyMeetingOverWithEmoji(
    meet: MeetingModel,
    duration: String,
) {
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meeting_emoji_for_members))
    }
    withStyle(style(MEET, meet.isOnline)) {
        append(" ${meet.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.RespondAccept(
    user: String,
    organizer: UserModel?,
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if(organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(
            stringResource(
                R.string.notification_meet_RESPOND_ACCEPTED,
                genderEnding(
                    organizer?.gender,
                    RESPOND_ACCEPTED
                )
            )
        )
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.Respond(
    user: String,
    organizer: UserModel?,
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(
            stringResource(
                R.string.notification_meet_RESPOND,
                genderEnding(organizer?.gender, RESPOND)
            )
        )
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.MeetingCanceled(
    meet: MeetingModel?,
    user: String,
    organizer: UserModel?,
    duration: String,
) {
    withStyle(style(MEET, meet?.isOnline)) {
        append("${meet?.title} ")
    }
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meet_MEETING_CANCELED_start))
    }
    withStyle(style(USER)) { append(" $user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(
            stringResource(
                R.string.notification_meet_MEETING_CANCELED_end,
                genderEnding(
                    organizer?.gender,
                    MEETING_CANCELED
                )
            )
        )
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.MeetingKicked(
    user: String,
    organizer: UserModel?,
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(
            stringResource(
                R.string.notification_meet_MEETING_KICKED,
                genderEnding(
                    organizer?.gender,
                    MEETING_KICKED
                )
            )
        )
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.Watch(
    user: String,
    organizer: UserModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(" " + stringResource(R.string.notification_meet_WATCH))
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.Translation15Min(
    user: String,
    organizer: UserModel?,
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if (organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meet_TRANSLATION_15MIN_start))
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title} ")
    }
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meet_TRANSLATION_15MIN_end))
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.System(
    notification: String?,
    duration: String,
) {
    withStyle(style(TEXT)) { append(notification) }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.WatchMeetingCreated(
    user: String,
    organizer: UserModel?,
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(USER)) { append("$user ") }
    if(organizer?.emoji != null) appendInlineContent("emoji")
    withStyle(style(TEXT)) {
        append(
            stringResource(
                R.string.notification_meet_WATCH_MEETING_CREATED,
                genderEnding(
                    organizer?.gender,
                    WATCH_MEETING_CREATED
                )
            )
        )
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}")
    }
    withStyle(style(TIME)) { append(" $duration") }
}

@Composable
private fun Builder.TranslationStarted(
    meet: MeetingModel?,
    duration: String,
) {
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meet_ADMIN_NOTIFICATION_start))
    }
    withStyle(style(MEET, meet?.isOnline)) {
        append(" ${meet?.title}. ")
    }
    withStyle(style(TEXT)) {
        append(stringResource(R.string.notification_meet_ADMIN_NOTIFICATION_end))
    }
    withStyle(style(TIME)) { append(" $duration") }
}

private fun Builder.getLinkAnnotation(
    user: String,
    meet: MeetingModel?,
) {
    val (userPlace, meetPlace) =
        toAnnotatedString().let {
            it.indexOf(user) to
                    it.indexOf("${meet?.title}")
        }
    addStringAnnotation(
        tag = "user",
        annotation = "",
        start = userPlace,
        end = userPlace + user.length + 2
    )
    addStringAnnotation(
        tag = "meet",
        annotation = "",
        start = meetPlace,
        end = meetPlace + "${meet?.title}".length
    )
}

@Composable
private fun LinkText(
    message: AnnotatedString,
    modifier: Modifier,
    organizer: UserModel?,
    onMeetClick: (() -> Unit)?,
    onUserClick: (() -> Unit)?,
) {
    val inlineContent = if (organizer?.emoji != null) mapOf(
        "emoji" to InlineTextContent(
            Placeholder(18.dp.toSp(), 18.dp.toSp(), TextCenter)
        ) {
            organizer.emoji?.let {
                GEmojiImage(it, Modifier.size(18.dp))
            }
        }
    ) else mapOf()
    LinkedText(
        text = message,
        modifier = modifier,
        inlineContent = inlineContent,
    ) {
        message
            .getStringAnnotations(("meet"), it, it)
            .firstOrNull()?.let { onMeetClick?.let { it() } }
        message
            .getStringAnnotations(("user"), it, it)
            .firstOrNull()?.let { onUserClick?.let { it() } }
    }
}

private enum class CustomText {
    USER, TEXT, MEET, TIME, BOLD
}

@Composable
private fun style(
    text: CustomText,
    online: Boolean? = false,
) = when (text) {
    USER -> font(
        weight = Bold,
        textStyle = typography.bodyMedium
    )

    BOLD -> font(weight = SemiBold)
    TEXT -> font()
    TIME -> font(colorScheme.onTertiary)
    MEET -> font(
        if (online == true)
            colorScheme.secondary
        else colorScheme.primary,
        weight = SemiBold
    )
}.toSpanStyle()

private fun genderEnding(
    genderType: GenderType?,
    notType: NotificationType,
) = when (notType) {
    RESPOND -> if (genderType == FEMALE)
        "ась" else "ся"

    RESPOND_ACCEPTED,
    MEETING_CANCELED,
    MEETING_KICKED,
    WATCH_MEETING_CREATED,
    -> if (genderType == FEMALE)
        "а" else ""

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
        text = text,
        modifier = modifier
            .pointerInput(onClick) {
                detectTapGestures { pos ->
                    layoutResult.value?.let { layoutResult ->
                        onClick(layoutResult.getOffsetForPosition(pos))
                    }
                }
            },
        onTextLayout = {
            val onText: (TextLayoutResult) -> Unit = {}
            layoutResult.value = it
            onText(it)
        },
        inlineContent = inlineContent
    )
}