package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme {
        ReceivedRespond(DemoRespondModel)
    }
}

@Preview
@Composable
private fun SentResponds() {
    GiltyTheme {
        SentRespond(DemoMeetWithRespondsModel)
    }
}

@Composable
fun SentRespond(
    respond: MeetWithRespondsModel,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                colorScheme.primaryContainer,
                shapes.medium
            )
    ) {
        Column {
            BrieflyRow(
                respond.tags.joinToString(separator = ", ")
                { it.title }, Modifier.padding(
                    start = 16.dp, top = 12.dp
                ), respond.organizer.avatar
            )
            Column(Modifier.padding(start = 66.dp)) {
                Divider(Modifier)
                Buttons(Modifier.padding(vertical = 8.dp), (true))
                { callback?.onCancelClick(respond.id) }
            }
        }
    }
}

@Composable
fun ReceivedRespond(
    respond: RespondModel,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                colorScheme.primaryContainer,
                shapes.medium
            )
    ) {
        val user = respond.author
        Column {
            BrieflyRow(
                "${user.username}, ${user.age}",
                Modifier.padding(
                    start = 16.dp, top = 12.dp
                ), user.avatar, user.emoji
            )
            Column(Modifier.padding(start = 66.dp)) {
                Divider(Modifier)
                Text(
                    respond.comment, Modifier
                        .padding(end = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
                if(respond.photoAccess) LazyRow(modifier) {
                    val hiddens =
                        listOf(
                            //TODO ОТКУДА СКРЫТЫЕ ФОТО?
                            DemoAvatarModel, DemoAvatarModel,
                            DemoAvatarModel, DemoAvatarModel,
                        )
                    items(hiddens) { photo ->
                        HiddenImage(
                            photo, Modifier.padding(6.dp), photo.hasAccess
                        ) { respond.author.id?.let { callback?.onImageClick(it) } }
                    }
                }
                Buttons(
                    Modifier.padding(vertical = 8.dp), (false),
                    { callback?.onAcceptClick(respond.id) })
                { callback?.onCancelClick(respond.id) }
            }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    isMyRespond: Boolean,
    positive: (() -> Unit)? = null,
    negative: () -> Unit,
) {
    Row(modifier) {
        positive?.let {
            SmallButton(
                stringResource(R.string.notification_respond_accept_button),
                Modifier.padding(end = 4.dp),
                colorScheme.primary,
            ) { it() }
        }
        SmallButton(
            if(isMyRespond) stringResource(R.string.cancel_button)
            else stringResource(R.string.meeting_filter_delete_tag_label),
            Modifier,
            colorScheme.outlineVariant,
        ) { negative() }
    }
}