package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

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
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.shared.TextFieldColors
import ru.rikmasters.gilty.presentation.ui.shared.TrackCheckBox
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingDetailsBottomComposePreview() {
    GiltyTheme {
        var hiddenPhoto by remember { mutableStateOf(false) }
        var commentText by remember { mutableStateOf("") }
        MeetingDetailsBottomCompose(
            Modifier,
            MeetingDetailsBottomComposeState(
                DemoFullMeetingModel,
                "2 часа",
                hiddenPhoto,
                commentText
            ),
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

data class MeetingDetailsBottomComposeState(
    val meetingModel: FullMeetingModel,
    val eventDuration: String,
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
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        MeetingBottomSheetTopBarCompose(
            Modifier,
            state.meetingModel,
            state.eventDuration
        )
        Text(
            stringResource(R.string.meeting_question_comment_or_assess),
            Modifier.padding(top = 30.dp),
            style = ThemeExtra.typography.H3
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
                    color = ThemeExtra.colors.secondaryTextColor,
                    style = ThemeExtra.typography.Body1Medium
                )
            }
        )
        Text(
            "${if (state.comment.isEmpty()) "0" else state.comment.length}/120",
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            ThemeExtra.colors.secondaryTextColor,
            textAlign = TextAlign.End,
            style = ThemeExtra.typography.LabelText
        )
        Card(
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.meeting_open_hidden_photos))
                TrackCheckBox(state.hiddenPhoto) { callback?.onHiddenPhotoActive(it) }
            }
        }
        Text(
            stringResource(R.string.meeting_only_organizer_label),
            Modifier.padding(top = 4.dp, start = 16.dp),
            color = ThemeExtra.colors.secondaryTextColor,
            style = ThemeExtra.typography.LabelText
        )
        GradientButton(
            Modifier.padding(16.dp, 28.dp),
            stringResource(R.string.meeting_respond)
        ) { callback?.onRespondClick() }
    }
}
