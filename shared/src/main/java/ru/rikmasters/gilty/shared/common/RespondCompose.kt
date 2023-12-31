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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.shared.*

/*
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
                (""),
                DemoUserModel,
                listOf(
                    DemoRespondModelWithPhoto
                )
            )
        }
    }
}

 */

@OptIn(ExperimentalMaterial3Api::class)
fun LazyListScope.sentRespond(
    meetId: String,
    name: String,
    organizer: UserModel,
    responds: List<RespondWithPhotos>,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    items(responds) { respond ->
        Card(
            onClick = {
                callback?.onRespondClick(
                    authorId = organizer.id ?: "",
                    meetId = meetId
                )
            },
            modifier = modifier.fillMaxWidth(),
            shape = shapes.medium,
            colors = cardColors(
                colorScheme.primaryContainer
            )
        ) {
            Column {
                BrieflyRow(
                    text = name,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 12.dp
                    ),
                    image = organizer.avatar
                        ?.thumbnail?.url ?: "",
                    
                    group = organizer.group ?: DEFAULT,
                )
                Column(Modifier.padding(start = 66.dp)) {
                    GDivider(Modifier)
                    Buttons(
                        Modifier.padding(vertical = 8.dp),
                        (true)
                    ) { callback?.onCancelClick(respond.id) }
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
        onClick = {
            callback?.onRespondClick(
                respond.author.id ?: "",
            )
        },
        modifier = modifier,
        shape = shapes.medium,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        val user = respond.author
        Column {
            BrieflyRow(
                text = "${user.username}${
                    if(user.age in 18..99) {
                        ", ${user.age}"
                    } else ""
                }",
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 12.dp
                ),
                image = user.avatar
                    ?.thumbnail?.url ?: "",
                emoji = user.emoji,
                
                group = user.group ?: DEFAULT,
            )
            Column(Modifier.padding(start = 66.dp)) {
                GDivider(Modifier)
                if(respond.comment.isNotBlank()) Text(
                    respond.comment,
                    Modifier
                        .padding(end = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
                // TODO: error states
                val photos = respond.photos?.collectAsLazyPagingItems()
                photos?.let {
                    if(it.loadState.refresh is LoadState.Loading) {
                        // TODO: заменить чем-нибудь
                        Spacer(
                            modifier = Modifier.height(60.dp)
                        )
                    } else {
                        val photosCount = it.itemCount
                        if(photosCount != 0) {
                            LazyRow(modifier) {
                                items(it) { photo ->
                                    photo?.let {
                                        HiddenImage(
                                            photo,
                                            Modifier.padding(6.dp),
                                            !photo.hasAccess
                                        ) {
                                            if(!photo.hasAccess) {
                                                callback?.onImageClick(
                                                    photo
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Buttons(
                    Modifier.padding(vertical = 8.dp),
                    (false),
                    { callback?.onAcceptClick(respond.id) }
                ) { callback?.onCancelClick(respond.id) }
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
                colorScheme.primary
            ) { it() }
        }
        SmallButton(
            if(isMyRespond) stringResource(R.string.cancel_button)
            else stringResource(R.string.meeting_filter_delete_tag_label),
            Modifier,
            colorScheme.outlineVariant
        ) { negative() }
    }
}
