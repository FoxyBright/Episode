package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel
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
    modifier: Modifier = Modifier
) {
    val active = false // TODO Должна быть проверка - участвует пользователь в meet или нет
    Card(
        { callback?.onRespondClick(respond.meet) },
        modifier.fillMaxWidth(), (true), MaterialTheme.shapes.medium,
        CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        val user = respond.sender
        when(respond.type) {
            RespondType.SEND -> BrieflyRow(
                user.avatar, respond.meet.title, (null),
                Modifier.padding(start = 16.dp, top = 12.dp)
            )
            
            RespondType.RECEIVED -> BrieflyRow(
                user.avatar, "${user.username}, ${user.age}",
                user.emoji, Modifier.padding(start = 16.dp, top = 12.dp)
            )
        }
        Column(Modifier.padding(start = 66.dp)) {
            Divider(Modifier.padding(vertical = 12.dp))
            respond.comment?.let {
                Text(
                    it, Modifier.padding(bottom = 12.dp, end = 20.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            respond.hiddenPhoto?.let {
                HiddenPhoto(
                    respond, Modifier.padding(bottom = 12.dp)
                ) { callback?.onImageClick(it) }
            }
            Buttons(
                Modifier, active,
                if(respond.type == RespondType.RECEIVED) {
                    { callback?.onAcceptClick(respond) }
                } else null)
            { callback?.onCancelClick(respond) }
        }
    }
}

@Composable
private fun HiddenPhoto(
    respond: RespondModel,
    modifier: Modifier = Modifier,
    onClick: (HiddenPhotoModel) -> Unit
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
    negative: () -> Unit
) {
    Row(modifier) {
        positive?.let {
            SmallButton(
                stringResource(R.string.notification_respond_accept_button),
                MaterialTheme.colorScheme.primary, Color.White,
                Modifier.padding(bottom = 12.dp, end = 4.dp),
                (true), positive
            )
        }
        SmallButton(
            if(active) stringResource(R.string.cancel_button)
            else stringResource(R.string.meeting_filter_delete_tag_label),
            MaterialTheme.colorScheme.outlineVariant,
            Color.White, Modifier.padding(bottom = 12.dp),
            (true), negative
        )
    }
}