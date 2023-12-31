package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.TextCenter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextAlign.Companion.Left
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.animated.AnimatedImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType.*
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.badEmoji
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.HiddenImage
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun TextMessagePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TextMessage(
                DemoLongMessageModel,
                (true), Modifier.padding(6.dp),
                isOnline = true
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp),
                isOnline = true
            )
        }
    }
}

@Preview
@Composable
private fun WritingMessagePreview() {
    GiltyTheme {
        WritingMessage(
            Modifier.padding(6.dp),
            shapes.large
        )
    }
}

@Preview
@Composable
private fun AnswerMessagePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TextMessage(
                DemoLongMessageModel,
                (true), Modifier.padding(6.dp),
                DemoMessageModel,
                isOnline = true
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp),
                DemoLongMessageModel,
                isOnline = true
            )
        }
    }
}

@Preview
@Composable
private fun HiddenPhotoMessagePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            HiddenImageMessage(
                Modifier.padding(6.dp),
                DemoImageMessageModel,
                (true), (false), shapes.large
            )
            HiddenImageMessage(
                Modifier.padding(6.dp),
                DemoImageMessageModel,
                (false), (true), shapes.large
            )
        }
    }
}

@Preview
@Composable
private fun AnswerImageMessagePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TextMessage(
                DemoLongMessageModel, (true), Modifier.padding(6.dp),
                DemoImageMessageModel, isOnline = true
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp),
                DemoImageMessageModel, isOnline = true
            )
        }
    }
}

@Preview
@Composable
private fun ImageMessagePreview() {
    
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ImageMessage(
                Modifier.padding(6.dp),
                DemoImageMessageModel,
                (true), shapes.large,
                (true)
            )
            ImageMessage(
                Modifier.padding(6.dp),
                DemoImageMessageModel,
                (false), shapes.large,
                (true)
            )
        }
    }
}

@Preview
@Composable
private fun SystemMessagePreview() {
    GiltyTheme {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            ChatNotificationType.values().forEach {
                SystemMessage(
                    ChatNotificationModel(
                        it, DemoUserModel
                    ), Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun WritingMessage(
    modifier: Modifier = Modifier,
    shape: Shape = shapes.large,
) {
    Box(
        modifier.background(
            colorScheme.primaryContainer,
            shape
        )
    ) {
        val mod = Modifier
            .padding(12.dp, 8.dp)
            .size(24.dp)
        if(LocalInspectionMode.current) Image(
            painterResource(R.drawable.ic_write), (null), mod
        ) else AnimatedImage(R.raw.typing_dots, mod)
    }
}

@Composable
fun SystemMessage(
    notification: ChatNotificationModel?,
    modifier: Modifier = Modifier,
) {
    val bold = typography.labelSmall
        .copy(fontWeight = Bold).toSpanStyle()
    var emoji: EmojiModel? = null
    
    notification?.let {
        Text(
            buildAnnotatedString {
                
                fun userLabel(user: UserModel): String {
                    val username = user.username ?: ""
                    val age =
                        if(user.age in 18..99)
                            "${user.age}" else ""
                    return if(age.isBlank())
                        "$username "
                    else "$username, $age "
                }
                
                emoji = badEmoji
                
                when(notification.type) {
                    CHAT_CREATED -> append(
                        stringResource(
                            R.string.chats_message_create_chat,
                            "н"
                        )
                    )
                    
                    MEMBER_JOIN ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold) { append(userLabel(it)) }
                            appendInlineContent("emoji")
                            append(
                                " ${
                                    stringResource(
                                        R.string.chats_message_join_meet,
                                        if(it.gender == FEMALE)
                                            "aсь" else "ся"
                                    )
                                }"
                            )
                        }
                    
                    MEMBER_LEAVE ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold) { append(userLabel(it)) }
                            appendInlineContent("emoji")
                            append(
                                " ${
                                    stringResource(
                                        R.string.chats_message_leave_meet,
                                        if(it.gender == FEMALE)
                                            "a" else ""
                                    )
                                }"
                            )
                        }
                    
                    MEMBER_SCREENSHOT ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold) { append(userLabel(it)) }
                            appendInlineContent("emoji")
                            append(
                                " ${
                                    stringResource(
                                        R.string.chats_message_make_screenshot,
                                        if(it.gender == FEMALE)
                                            "a" else ""
                                    )
                                }"
                            )
                        }
                    
                    TRANSLATION_START_30 -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append(stringResource(R.string.chats_message_translation_30)) }
                    
                    TRANSLATION_START_5 -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append(stringResource(R.string.chats_message_translation_5)) }
                    
                    TRANSLATION_STARTED -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append(stringResource(R.string.chats_message_translation_started)) }
                    
                    TRANSLATION_COMPLETED -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append(stringResource(R.string.chats_message_translation_completed)) }
                }
            },
            modifier.fillMaxWidth(),
            colorScheme.tertiary,
            style = typography.labelSmall,
            fontWeight = SemiBold,
            inlineContent = mapOf(
                "emoji" to InlineTextContent(
                    Placeholder(
                        width = 16.dp.toSp(),
                        height = 16.dp.toSp(),
                        placeholderVerticalAlign = TextCenter
                    )
                ) { emoji?.let { GEmojiImage(it) } }
            ), textAlign = Center
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HiddenImageMessage(
    modifier: Modifier = Modifier,
    message: MessageModel,
    sender: Boolean,
    hide: Boolean?,
    shape: Shape = shapes.large,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier, (true), shape,
        cardColors(Transparent),
    ) {
        message.message?.attachments?.let {
            Box(
                Modifier.background(
                    colorScheme.primaryContainer,
                    shape
                )
            ) {
                Row(Modifier.padding(12.dp, 8.dp)) {
                    HiddenImage(
                        it.first().file,
                        Modifier, (hide == true)
                    ) { onClick?.let { it() } }
                    Text(
                        stringResource(R.string.chats_hidden_photo),
                        Modifier.padding(
                            start = 12.dp,
                            top = 4.dp
                        ), colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
                MessageStatus(
                    colorScheme.onTertiary,
                    sender, message, Modifier
                        .padding(8.dp)
                        .align(BottomEnd)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImageMessage(
    modifier: Modifier = Modifier,
    message: MessageModel,
    sender: Boolean,
    shape: Shape = shapes.large,
    isOnline: Boolean,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier, (true), shape,
        cardColors(Transparent),
    ) {
        Box(Modifier.fillMaxHeight(0.5f)) {
            message.message?.attachments?.let {
                GCachedImage(
                    url = it.first().file?.url,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            colorScheme.onTertiary,
                            shape
                        )
                        .clip(shape),
                    contentScale = FillWidth,
                )
            }
            Box(
                Modifier
                    .padding(8.dp)
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max)
                    .align(BottomEnd)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .alpha(if(sender) 1f else 0.5f)
                        .background(
                            linearGradient(
                                if(sender)
                                    if(isOnline) green
                                    else red()
                                else outline
                            ),
                            CircleShape
                        )
                )
                MessageStatus(
                    White, sender, message,
                    Modifier.padding(6.dp, 4.dp)
                )
            }
        }
    }
}

private val green
    @Composable get() = listOf(
        colorScheme.secondary,
        colorScheme.secondary
    )

private val outline
    @Composable get() = listOf(
        colorScheme.outline,
        colorScheme.outline
    )
private val primaryContainer
    @Composable get() = listOf(
        colorScheme.primaryContainer,
        colorScheme.primaryContainer
    )

@Composable
fun TextMessage(
    message: MessageModel,
    sender: Boolean,
    modifier: Modifier = Modifier,
    answer: MessageModel? = null,
    shape: Shape = shapes.large,
    isOnline: Boolean,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier.background(
            linearGradient(
                if(sender)
                    if(isOnline) green
                    else red()
                else primaryContainer
            ), shape
        )
    ) {
        Box(Modifier.padding(8.dp)) {
            Column {
                answer?.let {
                    AnswerContent(
                        it, Modifier
                            .padding(
                                bottom = 4.dp,
                                end = 30.dp
                            )
                            .clickable { onClick?.let { it() } },
                        isOnline, sender
                    )
                }; TextWidget(message, sender)
            }
            MessageStatus(
                color = if(sender) White
                else colorScheme.onTertiary,
                sender = sender,
                messageModel = message,
                modifier = Modifier.align(BottomEnd)
            )
        }
    }
}

@Composable
private fun TextWidget(
    // TODO Переписать под TextFlow
    message: MessageModel,
    sender: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message.message?.text ?: "",
        modifier = modifier
            .padding(
                end = if(sender)
                    50.dp else 36.dp
            ),
        style = typography.bodyMedium.copy(
            color = if(sender) White
            else colorScheme.tertiary,
            textAlign = Left
        ),
    )
}

@Composable
private fun MessageStatus(
    color: Color,
    sender: Boolean,
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier, Start,
        CenterVertically
    ) {
        Text(
            text = messageModel.createdAt
                .format("HH:mm"),
            color = color,
            style = typography.titleSmall,
        )
        if(sender) Icon(
            painter = painterResource(
                if(messageModel.isRead)
                    R.drawable.ic_sms_read
                else R.drawable.ic_sms_delivered
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 2.dp)
                .size(12.dp),
            tint = color
        )
    }
}