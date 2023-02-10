package ru.rikmasters.gilty.chat.presentation.ui.chat.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_image_empty
import ru.rikmasters.gilty.shared.R.string.chats_message_answer_photo_label
import ru.rikmasters.gilty.shared.R.string.chats_message_answer_video_label
import ru.rikmasters.gilty.shared.R.string.profile_hidden_photo
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PRIVATE_PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.VIDEO
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun AnswerLongPreview() {
    GiltyTheme {
        AnswerContent(
            DemoMessageModelLongMessage,
            Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun MyAnswer() {
    GiltyTheme {
        AnswerContent(
            DemoMessageModel,
            Modifier.padding(16.dp),
            (true), (false)
        )
    }
}

@Preview
@Composable
private fun ImageAnswerTextBox() {
    GiltyTheme {
        AnswerContent(
            DemoImageMessage,
            Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun AnswerTextField() {
    GiltyTheme {
        AnswerContent(
            DemoMessageModel,
            Modifier.padding(16.dp),
            textField = true
        )
    }
}

@Composable
fun AnswerContent(
    message: MessageModel,
    modifier: Modifier = Modifier,
    sender: Boolean = false,
    textField: Boolean = false,
) {
    Row(
        modifier, Start,
        CenterVertically
    ) {
        Spacer(
            Modifier
                .background(
                    if(!sender)
                        colorScheme.primary
                    else White
                )
                .width(1.dp)
                .height(38.dp)
        )
        message.attachments?.let {
            it.file?.let { file ->
                AsyncImage(
                    file.id, (null), Modifier
                        .padding(start = 8.dp)
                        .size(38.dp)
                        .clip(shapes.small),
                    contentScale = Crop,
                    placeholder = painterResource(
                        ic_image_empty
                    )
                )
            }
        }
        val user = message.sender.username
        Column(Modifier.padding(start = 12.dp)) {
            Text(
                if(textField) "${
                    stringResource(R.string.chats_message_answer_recipient)
                } $user" else user ?: "", Modifier,
                if(!sender) colorScheme.primary
                else White,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            ); Label(message, sender)
        }
    }
}

@Composable
private fun Label(
    message: MessageModel,
    sender: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message.attachments?.let {
            stringResource(
                when(it.type) {
                    PHOTO -> chats_message_answer_photo_label
                    PRIVATE_PHOTO -> profile_hidden_photo
                    VIDEO -> chats_message_answer_video_label
                }
            )
        } ?: message.text,
        modifier,
        if(!sender) colorScheme.onTertiary
        else White,
        overflow = Ellipsis,
        style = typography.labelSmall,
        maxLines = 1,
    )
}