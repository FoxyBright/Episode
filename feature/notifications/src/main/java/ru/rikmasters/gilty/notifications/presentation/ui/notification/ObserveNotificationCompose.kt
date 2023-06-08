package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModelList
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.*

@Preview
@Composable
private fun ObserveNotificationPreview() {
    ObserveNotification(
        ObserveNotificationState(
            DemoNotificationMeetingOverModel,
            pagingPreview(DemoUserModelList),
            listOf(0), EmojiModel.list, EmojiModel.list,
        )
    )
}

data class ObserveNotificationState(
    val notification: NotificationModel,
    val participants: LazyPagingItems<UserModel>,
    val participantsStates: List<Int>,
    val notificationEmojiList: List<EmojiModel>,
    val emojiList: List<EmojiModel>,
)

@Composable
fun ObserveNotification(
    state: ObserveNotificationState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null,
) {
    Column(modifier) {
        if(state.notification.parent.meeting?.memberState
            != IS_ORGANIZER
        ) {
            NotificationItem(
                NotificationItemState(
                    state.notification,
                    DragRowState(1f),
                    shapes.medium,
                    getDifferenceOfTime(state.notification.date),
                    state.notificationEmojiList
                ), Modifier, callback
            )
        }
        val itemCount = state.participants.itemCount
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            when {
                state.participants.loadState.refresh is LoadState.Error -> {}
                state.participants.loadState.append is LoadState.Error -> {}
                state.participants.loadState.refresh is LoadState.Loading -> {
                    item { PagingLoader(state.participants.loadState) }
                }
                else -> {
                    itemsIndexed(state.participants) { index, participant ->
                        participant?.let {
                            Participant(
                                index = index,
                                member = participant,
                                size = itemCount,
                                unwrap = state.participantsStates
                                    .contains(index),
                                memberEmoji = participant
                                    .meetRating?.emoji,
                                emojiList = state.emojiList,
                                onClick = { part ->
                                    callback?.onParticipantClick(part)
                                }
                            ) { emoji ->
                                participant.id?.let {
                                    callback?.onEmojiClick(
                                        state.notification,
                                        emoji, it
                                    )
                                }
                            }
                        }
                    }
                    if(state.participants.loadState.append is LoadState.Loading) {
                        item { PagingLoader(state.participants.loadState) }
                    }
                    item {
                        if(itemCount != 0) Text(
                            text = stringResource(
                                R.string.notification_send_emotion
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, start = 16.dp),
                            color = colorScheme.onTertiary,
                            style = typography.labelSmall
                        )
                    }
                    itemSpacer(20.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Participant(
    index: Int,
    member: UserModel,
    size: Int,
    unwrap: Boolean,
    memberEmoji: EmojiModel?,
    emojiList: List<EmojiModel>,
    onClick: ((Int) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
) {
    Card(
        onClick = { onClick?.let { it(index) } },
        modifier = Modifier.fillMaxWidth(),
        enabled = true,
        shape = lazyItemsShapes(index, size, 14.dp),
        colors = cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(bottom = 12.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
                SpaceBetween,
                CenterVertically
            ) {
                BrieflyRow(
                    text = "${member.username}${
                        if(member.age in 18..99) {
                            ", ${member.age}"
                        } else ""
                    }",
                    modifier = Modifier,
                    image = member.avatar?.thumbnail?.url,
                    emoji = member.emoji,
                    //isOnline = member.isOnline?: false,
                    group = member.group?: UserGroupTypeModel.DEFAULT,
                )
                memberEmoji?.let {
                    GEmojiImage(
                        emoji = memberEmoji,
                        modifier = Modifier.size(24.dp)
                    )
                } ?: run {
                    Icon(
                        imageVector = if(unwrap)
                            Filled.KeyboardArrowDown
                        else Filled.KeyboardArrowRight,
                        contentDescription = (null),
                        modifier = Modifier.size(28.dp),
                        tint = colorScheme.onTertiary
                    )
                }
            }
            if(unwrap && memberEmoji == null)
                EmojiRow(
                    emojiList = emojiList,
                    modifier = Modifier.padding(
                        start = 60.dp,
                        end = 20.dp
                    )
                ) { emoji -> onEmojiClick?.let { it(emoji) } }
        }
    }; if(index < size - 1) Row {
        GDivider(
            modifier = Modifier.width(60.dp),
            color = colorScheme.primaryContainer
        )
        GDivider(Modifier.weight(1f))
    }
}
