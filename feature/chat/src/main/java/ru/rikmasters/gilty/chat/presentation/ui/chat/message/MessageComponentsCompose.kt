package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animated.AnimatedImage
import ru.rikmasters.gilty.shared.R.drawable.ic_sms_delivered
import ru.rikmasters.gilty.shared.R.drawable.ic_sms_read
import ru.rikmasters.gilty.shared.R.drawable.ic_write
import ru.rikmasters.gilty.shared.R.raw.typing_dots
import ru.rikmasters.gilty.shared.R.string.chats_hidden_photo
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.chat.SystemMessageType.*
import ru.rikmasters.gilty.shared.model.enumeration.SexType.FEMALE
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.badEmoji
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.HiddenImage
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
                DemoMessageModelLongMessage,
                (true), Modifier.padding(6.dp)
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WritingMessagePreview() {
    GiltyTheme {
        WritingMessage(
            shapes.large,
            Modifier.padding(6.dp)
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
                DemoMessageModelLongMessage,
                (true), Modifier.padding(6.dp),
                DemoMessageModel
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp),
                DemoMessageModelLongMessage
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
                DemoImageMessage,
                (true), (false), shapes.large,
                Modifier.padding(6.dp)
            )
            HiddenImageMessage(
                DemoImageMessage,
                (false), (true), shapes.large,
                Modifier.padding(6.dp)
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
                DemoMessageModelLongMessage,
                (true), Modifier.padding(6.dp),
                DemoImageMessage
            )
            TextMessage(
                DemoMessageModel,
                (false), Modifier.padding(6.dp),
                DemoImageMessage
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
                DemoImageMessage,
                (true), shapes.large,
                Modifier.padding(6.dp)
            )
            ImageMessage(
                DemoImageMessage,
                (false), shapes.large,
                Modifier.padding(6.dp)
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
            SystemMessageType.values().forEach {
                SystemMessage(
                    ChatNotificationType(
                        it, DemoMemberModel
                    ), Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun WritingMessage(
    shape: Shape = shapes.large,
    modifier: Modifier = Modifier
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
            painterResource(ic_write), (null), mod
        ) else AnimatedImage(typing_dots, mod)
    }
}

@Composable
fun SystemMessage(
    notification: ChatNotificationType?,
    modifier: Modifier = Modifier,
) {
    val bold = typography.labelSmall
        .copy(fontWeight = Bold).toSpanStyle()
    var emoji: EmojiModel? = null
    
    notification?.let {
        Text(
            buildAnnotatedString {
                emoji = badEmoji
                
                when(notification.type) {
                    CHAT_CREATED -> append(
                        "Чат создан"
                    )
                    
                    MEMBER_JOIN ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold) {
                                append(
                                    "${it.username}, ${it.age} "
                                )
                            }
                            appendInlineContent("emoji")
                            append(
                                " присоединил${
                                    if(it.gender == FEMALE)
                                        "aсь" else "ся"
                                }  к встрече"
                            )
                        }
                    
                    MEMBER_LEAVE ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold) {
                                append(
                                    "${it.username}, ${it.age} "
                                )
                            }
                            appendInlineContent("emoji")
                            append(
                                " покинул${
                                    if(it.gender == FEMALE)
                                        "а" else ""
                                }  встречу"
                            )
                        }
                    
                    MEMBER_SCREENSHOT ->
                        notification.member?.let {
                            emoji = it.emoji
                            withStyle(bold)
                            { append("${it.username}, ${it.age} ") }
                            appendInlineContent("emoji")
                            append(
                                " сделал${
                                    if(it.gender == FEMALE)
                                        "a" else ""
                                } скриншот!"
                            )
                        }
                    
                    TRANSLATION_START_30 -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append("Трансляция начнется через 30 минут") }
                    
                    TRANSLATION_START_5 -> withStyle(
                        bold.copy(
                            colorScheme.secondary,
                            fontWeight = SemiBold
                        )
                    ) { append("Трансляция начнется через 5 минут") }
                }
            },
            modifier.fillMaxWidth(),
            colorScheme.tertiary,
            style = typography.labelSmall,
            fontWeight = SemiBold,
            inlineContent = mapOf(
                "emoji" to InlineTextContent(
                    Placeholder(16.sp, 16.sp, TextCenter)
                ) {
                    emoji?.let {
                        GEmojiImage(it)
                    }
                }
            ), textAlign = Center
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HiddenImageMessage(
    message: MessageModel,
    sender: Boolean,
    hide: Boolean,
    shape: Shape = shapes.large,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier, (true), shape,
        cardColors(Transparent),
    ) {
        message.attachments?.let {
            Box(
                Modifier.background(
                    colorScheme.primaryContainer,
                    shape
                )
            ) {
                Row(Modifier.padding(12.dp, 8.dp)) {
                    it.file?.let { img ->
                        HiddenImage(img, Modifier, hide)
                        { onClick?.let { it() } }
                    }
                    Text(
                        stringResource(chats_hidden_photo),
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
    message: MessageModel,
    sender: Boolean,
    shape: Shape = shapes.large,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier, (true), shape,
        cardColors(Transparent),
    ) {
        Box {
            message.attachments?.let {
                it.file?.let { file ->
                    AsyncImage(
                        file.id, (null),
                        Modifier
                            .size(220.dp)
                            .background(
                                colorScheme.onTertiary,
                                shape
                            )
                            .clip(shape),
                        contentScale = Crop,
                    )
                }
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
                            if(sender) colorScheme.primary
                            else colorScheme.outline,
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

@Composable
fun TextMessage(
    message: MessageModel,
    sender: Boolean,
    modifier: Modifier = Modifier,
    answer: MessageModel? = null,
    shape: Shape = shapes.large,
) {
    Box(
        modifier.background(
            if(sender) colorScheme.primary
            else colorScheme.primaryContainer, shape
        )
    ) {
        Box(Modifier.padding(8.dp)) {
            Column {
                answer?.let {
                    AnswerContent(
                        it, Modifier.padding(bottom = 4.dp),
                        sender = sender
                    )
                }; TextWidget(message, sender)
            }
            MessageStatus(
                if(sender) White
                else colorScheme.onTertiary,
                sender, message,
                Modifier.align(BottomEnd)
            )
        }
    }
}

@Composable
private fun TextWidget(  // TODO Переписать под TextFlow
    message: MessageModel,
    sender: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        message.text, modifier
            .padding(
                end = if(sender) 50.dp else 36.dp
            ), if(sender) White
        else colorScheme.tertiary,
        style = typography.bodyMedium,
        textAlign = Left
    )
}

@Composable
private fun MessageStatus(
    color: Color,
    sender: Boolean,
    messageModel: MessageModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier, Start,
        CenterVertically
    ) {
        Text(
            messageModel.createdAt
                .format("HH:mm"),
            Modifier, color,
            style = typography.titleSmall,
        ); if(sender && messageModel.isDelivered)
        Icon(
            painterResource(
                if(messageModel.isRead)
                    ic_sms_read
                else ic_sms_delivered
            ), (null), Modifier
                .padding(start = 2.dp)
                .size(12.dp), color
        )
    }
}