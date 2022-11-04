package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TrackCheckBox
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingDetailsBottomComposePreview() {
    GiltyTheme {
        var hiddenPhoto by remember { mutableStateOf(false) }
        var commentText by remember { mutableStateOf("") }
        MeetingDetailsBottomCompose(
            Modifier.padding(16.dp),
            MeetingDetailsBottomComposeState(hiddenPhoto, commentText),
            object : MeetingDetailsBottomCallback {
                override fun onHiddenPhotoActive(hidden: Boolean) {
                    hiddenPhoto = hidden
                }

                override fun onCommentChange(text: String) {
                    commentText = text
                }

                override fun onRespondClick() {}
            }
        )
    }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingDetailsBottomComposeFullPreview() {
    GiltyTheme {
        MeetingDetailsBottomCompose(
            Modifier.padding(16.dp),
            MeetingDetailsBottomComposeState(
                true, DemoFullMeetingModel.description
            )
        )
    }
}

data class MeetingDetailsBottomComposeState(
    val hiddenPhoto: Boolean,
    val comment: String
)

interface MeetingDetailsBottomCallback {
    fun onHiddenPhotoActive(hidden: Boolean)
    fun onCommentChange(text: String)
    fun onRespondClick()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingDetailsBottomCompose(
    modifier: Modifier = Modifier,
    state: MeetingDetailsBottomComposeState,
    callback: MeetingDetailsBottomCallback? = null
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.meeting_question_comment_or_assess),
            Modifier,
            style = MaterialTheme.typography.labelLarge
        )
        TextField(
            state.comment, { if (it.length <= 120) callback?.onCommentChange(it) },
            Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldColors(),
            placeholder = {
                Text(
                    stringResource(R.string.meeting_comment_text_holder),
                    color = MaterialTheme.colorScheme.onTertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }, textStyle = MaterialTheme.typography.bodyMedium
        )
        Text(
            "${state.comment.length}/120",
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            MaterialTheme.colorScheme.onTertiary,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.headlineSmall
        )
        Card(
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.meeting_open_hidden_photos),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
                TrackCheckBox(state.hiddenPhoto) { callback?.onHiddenPhotoActive(it) }
            }
        }
        Text(
            stringResource(R.string.meeting_only_organizer_label),
            Modifier.padding(top = 4.dp, start = 16.dp),
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.headlineSmall
        )
        GradientButton(
            Modifier.padding(top = 28.dp),
            stringResource(R.string.meeting_respond)
        ) { callback?.onRespondClick() }
    }
}
