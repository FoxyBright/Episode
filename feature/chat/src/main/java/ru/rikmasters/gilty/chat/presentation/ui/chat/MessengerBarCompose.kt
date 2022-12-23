package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.TextFieldType.COMMENT
import ru.rikmasters.gilty.chat.presentation.ui.chat.TextFieldType.MESSAGE
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.AnswerContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_answer_arrow
import ru.rikmasters.gilty.shared.R.drawable.ic_cross
import ru.rikmasters.gilty.shared.model.chat.DemoMessageModelLongMessage
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
private fun MessengerBarPreview() {
    GiltyTheme {
        MessengerBar(
            "Сообщение",
            Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun MessengerBarWithAnswerPreview() {
    GiltyTheme {
        MessengerBar(
            "", Modifier.padding(16.dp),
            DemoMessageModelLongMessage
        )
    }
}

@Preview
@Composable
fun CommentBarPreview() {
    GiltyTheme {
        CommentBar(
            "",
            {}, Modifier.padding(10.dp)
        )
    }
}

@Preview
@Composable
fun CommentBarWithTextPreview() {
    GiltyTheme {
        CommentBar(
            "Комментарий",
            {}, Modifier.padding(10.dp)
        )
    }
}

interface MessengerBarCallback {
    
    fun textChange(text: String) {}
    fun gallery() {}
    fun onSend() {}
    fun onCancelAnswer() {}
}

@Composable
fun MessengerBar(
    text: String,
    modifier: Modifier = Modifier,
    answer: MessageModel? = null,
    callback: MessengerBarCallback? = null
) {
    Column(
        modifier
            .imePadding()
            .background(colorScheme.primaryContainer)
    ) {
        answer?.let {
            Answer(answer) { callback?.onCancelAnswer() }
        }
        Row(Modifier.padding(10.dp), Center, Bottom) {
            IconButton(
                { callback?.gallery() },
                Modifier.padding(start = 2.dp, end = 6.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_image_empty),
                    (null), Modifier.size(20.dp), colorScheme.tertiary
                )
            }
            MessageField(text, MESSAGE,
                { callback?.textChange(it) })
            { callback?.onSend() }
        }
    }
}

@Composable
private fun Answer(
    answer: MessageModel?,
    onCancel: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.padding(10.dp).weight(1f),
            Start, CenterVertically
        ) {
            Icon(
                painterResource(ic_answer_arrow),
                null, Modifier
                    .padding(horizontal = 20.dp)
                    .size(28.dp),
                colorScheme.primary
            )
            answer?.let {
                AnswerContent(
                    it, Modifier,
                    (false), (true)
                )
            }
        }
        IconButton(onCancel, Modifier.padding(6.dp)) {
            Icon(
                painterResource(ic_cross),
                (null), Modifier.size(26.dp),
                colorScheme.primary
            )
        }
    }
}

@Composable
fun CommentBar(
    text: String,
    textChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSend: (() -> Unit)? = null
) {
    MessageField(text, COMMENT, textChange, modifier)
    { onSend?.let { it() } }
}

@Composable
private fun MessageField(
    text: String,
    type: TextFieldType,
    textChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSend: () -> Unit
) {
    val message = text.ifBlank {
        stringResource(
            if(type == MESSAGE)
                R.string.chats_messenger_place_holder
            else R.string.chats_comment_place_holder
        )
    }
    val style = typography.bodyMedium
    val color =
        if(text.isBlank())
            colorScheme.onTertiary
        else {
            if(type == COMMENT) White
            else colorScheme.tertiary
        }
    Box(
        modifier
            .fillMaxWidth()
            .background(
                if(type == MESSAGE) colors.chatBackground
                else colors.commentBackground,
                shapes.chatRoundedShape
            )
    ) {
        Box(
            Modifier
                .padding(vertical = 12.dp)
                .padding(start = 16.dp, end = 50.dp)
        ) {
            BasicTextField(
                text, { textChange(it) },
                Modifier
                    .fillMaxWidth()
                    .align(CenterStart),
                maxLines = 5,
                textStyle = style.copy(color),
                cursorBrush = SolidColor(colorScheme.primary)
            ) {
                if(text.isEmpty()) Text(
                    message, Modifier,
                    color, style = style
                ); it()
            }
        }
        if(text.isNotBlank()) SendButton(
            if(type == MESSAGE) colorScheme.primary
            else colorScheme.secondary,
            Modifier
                .align(BottomEnd)
                .padding(6.dp), onSend
        )
    }
}

@Composable
private fun SendButton(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.ic_send),
            (null), Modifier
                .padding(vertical = 6.dp)
                .padding(start = 8.dp, end = 4.dp)
                .size(18.dp), White
        )
    }
}

private enum class TextFieldType { COMMENT, MESSAGE }