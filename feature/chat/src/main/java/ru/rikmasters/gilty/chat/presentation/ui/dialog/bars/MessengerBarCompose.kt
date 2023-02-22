package ru.rikmasters.gilty.chat.presentation.ui.dialog.bars

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
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.TextFieldType.COMMENT
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.TextFieldType.MESSAGE
import ru.rikmasters.gilty.chat.presentation.ui.dialog.message.AnswerContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_answer_arrow
import ru.rikmasters.gilty.shared.R.drawable.ic_cross
import ru.rikmasters.gilty.shared.R.string.text_text
import ru.rikmasters.gilty.shared.model.chat.DemoLongMessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

private val smthText
    @Composable
    get() = stringResource(text_text)

@Preview
@Composable
private fun MessengerBarPreview() {
    GiltyTheme {
        Column {
            MessengerBar(
                smthText,
                Modifier.padding(6.dp),
                isOnline = true
            )
            MessengerBar(
                "",
                Modifier.padding(6.dp),
                isOnline = true
            )
        }
    }
}

@Preview
@Composable
private fun MessengerBarWithAnswerPreview() {
    GiltyTheme {
        Column {
            MessengerBar(
                smthText, Modifier.padding(6.dp),
                DemoLongMessageModel, false
            )
            MessengerBar(
                "", Modifier.padding(6.dp),
                DemoLongMessageModel, true
            )
        }
    }
}

@Preview
@Composable
fun CommentBarPreview() {
    GiltyTheme {
        Column {
            CommentBar(
                smthText, {},
                Modifier.padding(10.dp)
            )
            CommentBar(
                "", {},
                Modifier.padding(10.dp)
            )
        }
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
    isOnline: Boolean,
    callback: MessengerBarCallback? = null,
) {
    Column(
        modifier
            .imePadding()
            .background(colorScheme.primaryContainer)
    ) {
        answer?.let {
            Answer(it, isOnline) {
                callback?.onCancelAnswer()
            }
        }
        Row(Modifier.padding(10.dp), Center, Bottom) {
            IconButton(
                { callback?.gallery() },
                Modifier.padding(start = 2.dp, end = 6.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_image_empty),
                    (null), Modifier.size(24.dp), colorScheme.tertiary
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
    isOnline: Boolean,
    onCancel: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 10.dp)
                .weight(1f),
            Start, CenterVertically
        ) {
            Icon(
                painterResource(ic_answer_arrow),
                null, Modifier
                    .padding(start = 14.dp, end = 22.dp)
                    .size(24.dp),
                if(isOnline) colorScheme.secondary
                else colorScheme.primary
            )
            answer?.let {
                AnswerContent(
                    it, Modifier, isOnline,
                    (false), (true)
                )
            }
        }
        IconButton(
            onCancel,
            Modifier
                .padding(horizontal = 6.dp)
                .padding(top = 10.dp)
        ) {
            Icon(
                painterResource(ic_cross),
                (null), Modifier.size(22.dp),
                if(isOnline) colorScheme.secondary
                else colorScheme.primary
            )
        }
    }
}

@Composable
fun CommentBar(
    text: String,
    textChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSend: (() -> Unit)? = null,
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
    onSend: () -> Unit,
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
    onClick: () -> Unit,
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