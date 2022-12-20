package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Left
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R.drawable.gb
import ru.rikmasters.gilty.shared.R.drawable.ic_sms_delivered
import ru.rikmasters.gilty.shared.R.drawable.ic_sms_read
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.shared.HiddenImage
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MyMessage() {
    GiltyTheme {
        SwipeableMessage(
            DemoMessageModelLongMessage,
            (true), DragRowState(1f)
        )
    }
}

@Preview
@Composable
private fun MyMessageWithAttach() {
    GiltyTheme {
        SwipeableMessage(
            DemoMessageModel, (true),
            DragRowState(1f),
            DemoMessageModel.answer,
        )
    }
}

@Preview
@Composable
private fun MessagePreview() {
    GiltyTheme {
        SwipeableMessage(
            DemoMessageModel,
            (false), DragRowState(1f)
        )
    }
}

@Preview
@Composable
private fun MyMessagePreview() {
    GiltyTheme {
        SwipeableMessage(
            DemoMessageModel,
            (true), DragRowState(1f)
        )
    }
}

@Preview
@Composable
private fun MessageImage() {
    GiltyTheme {
        SwipeableMessage(
            DemoImageMessage,
            (false), DragRowState(1f)
        )
    }
}

@Preview
@Composable
private fun MessageHiddenImage() {
    GiltyTheme {
        SwipeableMessage(
            DemoHiddenImageMessage,
            (true), DragRowState(1f)
        )
    }
}

@Preview
@Composable
private fun MessageAnswer() {
    GiltyTheme {
        SwipeableMessage(
            DemoMessageModel, (false),
            DragRowState(1f),
        )
    }
}

private data class MessageState(
    val sender: Boolean,
    val align: Alignment,
    val textColor: Color,
    val background: Brush
)

@Composable
fun SwipeableMessage(
    message: MessageModel,
    sender: Boolean,
    state: DragRowState,
    answer: MessageModel? = null,
    hide: Boolean = false,
    modifier: Modifier = Modifier,
    onLongPress: ((MessageModel) -> Unit)? = null,
    onSwiped: ((MessageModel) -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null,
) {
    Row(
        modifier.swipeableRow(state)
        { onSwiped?.let { it(message) } },
        Arrangement.Center, CenterVertically
    ) {
        MessageContent(
            message, sender, answer, hide, Modifier,
            { onLongPress?.let { c -> c(it) } })
        { onImageClick?.let { c -> c(it) } }
    }
}

@Composable
private fun MessageContent(
    message: MessageModel,
    sender: Boolean,
    answer: MessageModel? = null,
    hide: Boolean,
    modifier: Modifier = Modifier,
    onLongPress: ((MessageModel) -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null
) {
    val back = colorScheme.primaryContainer
    val state = if(sender)
        MessageState(
            (true), CenterEnd, White,
            Brush.linearGradient(Gradients.red()),
        )
    else MessageState(
        (false), CenterStart, colorScheme.tertiary,
        Brush.linearGradient(listOf(back, back)),
    )
    Box(modifier.fillMaxWidth(), state.align) {
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp, 4.dp), state.align
        ) {
            Row(verticalAlignment = Bottom) {
                if(!sender) AsyncImage(
                    message.sender.avatar.id, (null),
                    Modifier
                        .padding(horizontal = 6.dp)
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = Crop,
                    placeholder = painterResource(gb)
                )
                Content(
                    Modifier
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    onLongPress?.let {
                                        it(message)
                                    }
                                },
                                onPress = {
                                    onImageClick?.let { c ->
                                        message.attachments?.let { c(it.id) }
                                    }
                                }
                            )
                        },
                    state, message, answer, hide
                ) { onImageClick?.let { c -> c(it) } }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: MessageState,
    message: MessageModel,
    answer: MessageModel?,
    hide: Boolean,
    onImageClick: ((image: String) -> Unit)? = null
) {
    if(message.attachments != null) ImageMessage(
        state, message,
        message.sender.avatar,
        modifier, message.hidden, hide
    ) { onImageClick?.let { c -> c(it) } }
    else Message(
        state, message,
        modifier, answer
    )
}

@Composable
private fun Message(
    state: MessageState,
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
    answer: MessageModel? = null
) {
    Box(modifier, state.align) {
        Box(
            Modifier.background(
                state.background,
                shapes.large
            ), state.align
        ) {
            Box(Modifier.padding(8.dp), state.align) {
                Column {
                    answer?.let {
                        AnswerContent(
                            it, Modifier.padding(bottom = 4.dp),
                            sender = state.sender
                        )
                    }
                    Text(
                        messageModel.text,
                        Modifier
                            .align(Alignment.Start)
                            .padding(
                                end = if(state.sender) 50.dp else 36.dp
                            ), state.textColor,
                        textAlign = if(state.sender) Right else Left,
                        style = typography.bodyMedium
                    )
                }
                Status(
                    state.textColor, state.sender,
                    messageModel, Modifier.align(BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun ImageMessage(
    state: MessageState,
    messageModel: MessageModel,
    image: ImageModel,
    modifier: Modifier = Modifier,
    hidden: Boolean = false,
    hide: Boolean = false,
    onImageClick: ((String) -> Unit)? = null
) {
    Box(modifier, state.align) {
        Box(Modifier, BottomEnd) {
            if(hidden) {
                Box(
                    Modifier.background(
                        colorScheme.primaryContainer,
                        shapes.large
                    )
                ) {
                    Row(Modifier.padding(12.dp, 8.dp)) {
                        HiddenImage(
                            image, Modifier, hide
                        ) { onImageClick?.let { it(image.id) } }
                        Text(
                            "Фото",
                            Modifier.padding(start = 12.dp, top = 4.dp),
                            colorScheme.tertiary,
                            style = typography.bodyMedium,
                            fontWeight = SemiBold
                        )
                    }
                }
            } else AsyncImage(
                image.id, (null),
                Modifier
                    .size(220.dp)
                    .clip(shapes.large)
                    .clickable {
                        onImageClick?.let {
                            it(image.id)
                        }
                    }, contentScale = Crop,
                placeholder = painterResource(gb)
            )
            Box(
                Modifier
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(
                        if(!hidden) state.background
                        else horizontalGradient(
                            listOf(
                                colorScheme.primaryContainer,
                                colorScheme.primaryContainer
                            )
                        )
                    )
            ) {
                Status(
                    if(!hidden) state.textColor
                    else colorScheme.onTertiary,
                    state.sender, messageModel,
                    Modifier.padding(4.dp, 2.dp)
                )
            }
        }
    }
}

@Composable
private fun Status(
    color: Color,
    sender: Boolean,
    messageModel: MessageModel,
    modifier: Modifier = Modifier
) {
    Row(modifier, Start, CenterVertically) {
        Text(
            messageModel.createdAt.format("HH:mm"),
            Modifier, color, style = typography.titleSmall,
        ); if(sender && messageModel.isDelivered)
        Icon(
            painterResource(
                if(messageModel.isRead) ic_sms_read
                else ic_sms_delivered
            ), (null), Modifier
                .padding(start = 2.dp)
                .size(12.dp), color
        )
    }
}