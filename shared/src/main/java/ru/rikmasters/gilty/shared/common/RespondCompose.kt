package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SEND
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme { Respond(DemoReceivedRespondsModel) }
}

@Preview
@Composable
private fun SendResponds() {
    GiltyTheme { Respond(DemoSendRespondsModel) }
}

@Preview
@Composable
private fun ReceivedWithoutPhotosResponds() {
    GiltyTheme { Respond(DemoReceivedRespondModelWithoutPhoto) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Respond(
    respond: RespondModel,
    callback: RespondCallback? = null,
    modifier: Modifier = Modifier,
) {
    val active = false // TODO Должна быть проверка - участвует пользователь в meet или нет
    Card(
        { callback?.onRespondClick(respond.meet) },
        modifier.fillMaxWidth(), (true), MaterialTheme.shapes.medium,
        CardDefaults.cardColors(colorScheme.primaryContainer)
    ) {
        val user = respond.sender
        val username = "${user.username}, ${user.age}"
        when(respond.type) {
            SEND -> MeetRow(user, respond.meet.title)
            RECEIVED -> MeetRow(user, username, user.emoji)
        }
        Column(Modifier.padding(start = 66.dp)) {
            Divider(Modifier)
            respond.comment?.let {
                Text(
                    it, Modifier
                        .padding(end = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
            }
            respond.hiddenPhoto?.let {
                HiddenPhoto(
                    respond, Modifier
                ) { callback?.onImageClick(it) }
            }
            Buttons(
                Modifier.padding(vertical = 8.dp), active,
                if(respond.type == RECEIVED) {
                    { callback?.onAcceptClick(respond) }
                } else null)
            { callback?.onCancelClick(respond) }
        }
    }
}

@Composable
private fun MeetRow(
    user: OrganizerModel,
    text: String,
    emoji: EmojiModel? = null,
) {
    BrieflyRow(
        text, Modifier.padding(
            start = 16.dp, top = 12.dp
        ), user.avatar, emoji
    )
}

@Composable
private fun HiddenPhoto(
    respond: RespondModel,
    modifier: Modifier = Modifier,
    onClick: (AvatarModel) -> Unit,
) {
    respond.hiddenPhoto?.let {
        LazyRow(modifier) {
            items(it) { photo ->
                HiddenImage(
                    photo.first,
                    Modifier.padding(6.dp),
                    photo.second
                ) { onClick(photo.first) }
            }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    positive: (() -> Unit)? = null,
    negative: () -> Unit,
) {
    Row(modifier) {
        positive?.let {
            SmallButton(
                stringResource(R.string.notification_respond_accept_button),
                colorScheme.primary, Color.White,
                Modifier.padding(end = 4.dp),
                (true), positive
            )
        }
        SmallButton(
            if(active) stringResource(R.string.cancel_button)
            else stringResource(R.string.meeting_filter_delete_tag_label),
            colorScheme.outlineVariant,
            Color.White, Modifier,
            (true), negative
        )
    }
}