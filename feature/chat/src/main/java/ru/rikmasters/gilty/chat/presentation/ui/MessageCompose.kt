package ru.rikmasters.gilty.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview
@Composable
private fun MessageContentPreview() {
    GiltyTheme { MessageContent(DemoMessageModel, true) }
}

private data class MessageState(
    val align: Alignment,
    val textColor: Color,
    val background: Brush
)

@Composable
fun MessageContent(
    messageModel: MessageModel, sender: Boolean,
    answer: Pair<MessageModel, MemberModel>? = null
) {
    val back = MaterialTheme.colorScheme.primaryContainer
    val state = if (sender)
        MessageState(
            CenterEnd, Color.White,
            Brush.linearGradient(Gradients.red()),
        )
    else MessageState(
        CenterStart, MaterialTheme.colorScheme.tertiary,
        Brush.linearGradient(listOf(back, back)),
    )
    Box(Modifier.fillMaxWidth(), state.align) {
        Box(
            Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp, 4.dp), state.align
        ) {
            Row(verticalAlignment = Bottom) {
                if (sender) Message(
                    state, messageModel,
                    Modifier.padding(end = 6.dp), answer
                )
                AsyncImage(
                    messageModel.sender.avatar.id, (null),
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = Crop
                ); if (!sender) Message(
                state, messageModel,
                Modifier.padding(start = 6.dp), answer
            )
            }
        }
    }
}

@Composable
private fun Message(
    state: MessageState,
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
    answer: Pair<MessageModel, MemberModel>? = null
) {
    Box(modifier.background(state.background, MaterialTheme.shapes.large)) {
        Box(Modifier.padding(8.dp)) {
            Column {
                answer?.let {
                    AnswerContent(
                        it.first, it.second,
                        Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    messageModel.text,
                    Modifier.padding(end = 40.dp), state.textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Status(state.textColor, messageModel, Modifier.align(BottomEnd))
        }
    }
}

@Composable
private fun Status(
    color: Color,
    messageModel: MessageModel,
    modifier: Modifier = Modifier
) {
    Row(modifier, Start, CenterVertically) {
        Text(
            messageModel.createdAt.format("HH:mm"),
            Modifier, color, style = MaterialTheme.typography.titleSmall,
        ); if (messageModel.isDelivered)
        Icon(
            painterResource(
                if (messageModel.isRead) R.drawable.ic_sms_read
                else R.drawable.ic_sms_delivered
            ), (null),
            Modifier
                .padding(start = 2.dp)
                .size(12.dp)
        )
    }
}
