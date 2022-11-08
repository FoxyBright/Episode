package ru.rikmasters.gilty.chat.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.chat.presentation.ui.TextFieldType.COMMENT
import ru.rikmasters.gilty.chat.presentation.ui.TextFieldType.MESSAGE
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
fun MessengerBarPreview() {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    var answer by
    remember { mutableStateOf<MessageModel?>(DemoMessageModel) }
    Box(Modifier.fillMaxSize()) {
        MessengerBar(
            message, Modifier.align(Alignment.BottomCenter),
            answer, object : MessengerBarCallback {
                override fun textChange(text: String) {
                    message = text
                }

                override fun gallery() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            Text("Скрытые фотки", Modifier.height(300.dp))
                        }
                    }
                }

                override fun onSend() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    message = ""
                }

                override fun onCancelAnswer() {
                    answer = null
                }
            }
        )
    }
}

@Preview
@Composable
fun CommentBarPreview() {
    GiltyTheme {
        Box(Modifier.fillMaxSize()) {
            CommentBar("", {}, Modifier.align(Alignment.BottomCenter))
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
    callback: MessengerBarCallback? = null
) {
    Column(modifier.background(colorScheme.primaryContainer)) {
        answer?.let {
            Row(
                Modifier.fillMaxWidth(),
                SpaceBetween, CenterVertically
            ) {
                Row(
                    Modifier.padding(10.dp),
                    Start, CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.ic_answer_arrow),
                        null, Modifier
                            .padding(horizontal = 20.dp)
                            .size(28.dp),
                        colorScheme.primary
                    ); AnswerContent(it, Modifier, (true))
                }
                IconButton(
                    { callback?.onCancelAnswer() },
                    Modifier.padding(6.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_cross),
                        (null), Modifier.size(26.dp),
                        colorScheme.primary
                    )
                }
            }
        }
        Row(Modifier.padding(10.dp), Start, Bottom) {
            IconButton(
                { callback?.gallery() },
                Modifier.padding(start = 2.dp, bottom = 6.dp, end = 6.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_image_empty),
                    (null), Modifier.size(20.dp), colorScheme.tertiary
                )
            }
            TextField(text, MESSAGE,
                { callback?.textChange(it) })
            { callback?.onSend() }
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
    TextField(text, COMMENT, textChange, modifier)
    { onSend?.let { it() } }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TextField(
    text: String,
    type: TextFieldType,
    textChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSend: () -> Unit
) {
    Box(
        modifier.background(
            if (type == MESSAGE) colors.chatBackground
            else colors.commentBackground,
            shapes.chatRoundedShape
        )
    ) {
        TextField(
            text, textChange,
            Modifier
                .fillMaxWidth()
                .padding(end = 50.dp), colors = colors(
                if (type == MESSAGE) colors.chatBackground
                else colors.commentBackground
            ),
            placeholder = {
                Text(
                    stringResource(
                        if (type == MESSAGE) R.string.chats_messenger_place_holder
                        else R.string.chats_comment_place_holder
                    ),
                    Modifier, colorScheme.onTertiary,
                    style = typography.bodyMedium
                )
            },
            shape = shapes.chatRoundedShape,
            textStyle = typography.bodyMedium
        )
        if (text.isNotBlank())
            SendButton(
                if (type == MESSAGE) colorScheme.primary
                else colorScheme.secondary,
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp), onSend
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
            .clickable { onClick() }, Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.ic_send),
            null, Modifier
                .padding(vertical = 8.dp)
                .padding(start = 10.dp, end = 6.dp)
                .size(32.dp), Color.White
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun colors(
    containerColor: Color = colors.chatBackground
): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        textColor = colorScheme.tertiary,
        containerColor = containerColor,
        unfocusedIndicatorColor = Transparent,
        unfocusedLabelColor = colorScheme.onTertiary,
        focusedLabelColor = colorScheme.tertiary,
        focusedIndicatorColor = Transparent
    )
}

private enum class TextFieldType { COMMENT, MESSAGE }