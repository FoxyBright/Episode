package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme {
        ReceivedRespond(
            DemoRespondModelWithPhoto
        )
    }
}

@Preview
@Composable
private fun SentResponds() {
    GiltyTheme {
        LazyColumn {
            sentRespond(
                (""), DemoUserModel, listOf(
                    DemoRespondModelWithPhoto
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.sentRespond(
    name: String,
    organizer: UserModel,
    responds: List<RespondWithPhotos>,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    items(responds) { respond ->
        Card(
            { callback?.onRespondClick(organizer.id!!) },
            modifier.fillMaxWidth(), (true), shapes.medium,
            cardColors(colorScheme.primaryContainer)
        ) {
            Column {
                BrieflyRow(
                    name, Modifier.padding(
                        start = 16.dp, top = 12.dp
                    ), organizer.avatar?.thumbnail?.url
                )
                Column(Modifier.padding(start = 66.dp)) {
                    Divider(Modifier)
                    Buttons(Modifier.padding(vertical = 8.dp), (true))
                    { callback?.onCancelClick(respond.id) }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReceivedRespond(
    respond: RespondWithPhotos,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    Card(
        { callback?.onRespondClick(respond.author.id!!) },
        modifier, (true), shapes.medium,
        cardColors(colorScheme.primaryContainer)
    ) {
        val user = respond.author
        Column {
            BrieflyRow(
                "${user.username}${
                    if(user.age in 18..99) {
                        ", ${user.age}"
                    } else ""
                }", Modifier.padding(
                    start = 16.dp, top = 12.dp
                ), user.avatar?.thumbnail?.url,
                user.emoji
            )
            Column(Modifier.padding(start = 66.dp)) {
                Divider(Modifier)
                if(respond.comment.isNotBlank()) Text(
                    respond.comment, Modifier
                        .padding(end = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
                if(respond.photos.isNotEmpty() && respond.photoAccess)
                    LazyRow(modifier) {
                        items(respond.photos) { photo ->
                            HiddenImage(
                                photo, Modifier.padding(6.dp),
                                !photo.hasAccess
                            ) {
                                if(!photo.hasAccess)
                                    callback?.onImageClick(photo.url)
                            }
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