package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign.Companion.Left
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MyMessage() {
    GiltyTheme { Message(DemoMessageModelLongMessage, true) }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MyMessageWithAttach() {
    GiltyTheme {
        Message(
            DemoMessageModel, (true),
            DemoImageMessage
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MessagePreview() {
    GiltyTheme { Message(DemoMessageModelLongMessage, false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MyMessagePreview() {
    GiltyTheme { Message(DemoMessageModel, true) }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MessageImage() {
    GiltyTheme { Message(DemoImageMessage, false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF4B4B4B)
@Composable
private fun MessageAnswer() {
    GiltyTheme {
        Message(
            DemoMessageModel, (false),
            DemoMessageModelLongMessage
        )
    }
}

@Composable
fun Message(
    messageModel: MessageModel, sender: Boolean,
    answer: MessageModel? = null
) {
    val back = MaterialTheme.colorScheme.primaryContainer
    val state = if(sender)
        MessageState(
            (true), CenterEnd, Color.White,
            Brush.linearGradient(Gradients.red()),
        )
    else MessageState(
        (false), CenterStart, MaterialTheme.colorScheme.tertiary,
        Brush.linearGradient(listOf(back, back)),
    )
    Box(Modifier.fillMaxWidth(), state.align) {
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp, 4.dp), state.align
        ) {
            Row(verticalAlignment = Bottom) {
                if(!sender) AsyncImage(
                    messageModel.sender.avatar.id, (null),
                    Modifier
                        .padding(horizontal = 6.dp)
                        .size(24.dp)
                        .clip(CircleShape), contentScale = Crop,
                    placeholder = painterResource(R.drawable.gb)
                )
                Content(state, messageModel, answer, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Content(
    state: MessageState,
    messageModel: MessageModel,
    answer: MessageModel?,
    modifier: Modifier = Modifier
) {
    if(messageModel.attachments != null) ImageMessage(
        state, messageModel,
        messageModel.sender.avatar, modifier
    ) else Message(state, messageModel, modifier, answer)
}

@Composable
private fun ImageMessage(
    state: MessageState,
    messageModel: MessageModel,
    image: ImageModel,
    modifier: Modifier = Modifier
) {
    Box(modifier, state.align) {
        Box(Modifier, BottomEnd) {
            AsyncImage(
                image.id, (null),
                Modifier
                    .size(220.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = Crop,
                placeholder = painterResource(R.drawable.gb)
            )
            Box(
                Modifier
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(state.background)
            ) {
                Status(
                    state.textColor, state.sender, messageModel,
                    Modifier.padding(4.dp, 2.dp)
                )
            }
        }
    }
}

private data class MessageState(
    val sender: Boolean,
    val align: Alignment,
    val textColor: Color,
    val background: Brush
)

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
                MaterialTheme.shapes.large
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
                            .align(if(state.sender) End else Alignment.Start)
                            .padding(
                                end = if(state.sender) 50.dp else 30.dp
                            ), state.textColor,
                        textAlign = if(state.sender) Right else Left,
                        style = MaterialTheme.typography.bodyMedium
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
private fun Status(
    color: Color,
    sender: Boolean,
    messageModel: MessageModel,
    modifier: Modifier = Modifier
) {
    Row(modifier, Start, CenterVertically) {
        Text(
            messageModel.createdAt.format("HH:mm"),
            Modifier, color, style = MaterialTheme.typography.titleSmall,
        ); if(sender && messageModel.isDelivered)
        Icon(
            painterResource(
                if(messageModel.isRead) R.drawable.ic_sms_read
                else R.drawable.ic_sms_delivered
            ), (null),
            Modifier
                .padding(start = 2.dp)
                .size(12.dp), Color.White
        )
    }
}
