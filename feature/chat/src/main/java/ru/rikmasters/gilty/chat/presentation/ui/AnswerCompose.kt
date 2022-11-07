package ru.rikmasters.gilty.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun AnswerPreview() {
    GiltyTheme { AnswerContent(DemoMessageModel, DemoMemberModel) }
}

@Composable
fun AnswerContent(
    message: MessageModel,
    recipient: MemberModel,
    modifier: Modifier = Modifier
) {
    Row(modifier, Arrangement.Start, Alignment.CenterVertically) {
        Spacer(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .width(2.dp)
                .height(38.dp)
        )
        message.attachments?.let {
            AsyncImage(
                it.id, (null), Modifier
                    .padding(start = 8.dp)
                    .size(38.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
        }
        Column(Modifier.padding(start = 12.dp)) {
            Text(
                stringResource(
                    R.string.chats_message_answer_recipient,
                    recipient.username
                ), Modifier, MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (message.attachments != null)
                Text(
                    stringResource(
                        when (message.attachments.type) {
                            PhotoType.PHOTO -> R.string.chats_message_answer_photo_label
                            PhotoType.VIDEO -> R.string.chats_message_answer_video_label
                        }
                    ),
                    Modifier, MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.labelSmall,
                )
            else
                Text(
                    if (message.text.length < 30) message.text
                    else "${message.text.substring(0, 30)}…",
                    Modifier, MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.labelSmall,
                )
        }
    }
}