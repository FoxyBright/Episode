package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT
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
        MyNotification(state, callback)
        PagingParticipantsList(state, callback)
    }
}

@Composable
private fun PagingParticipantsList(
    state: ObserveNotificationState,
    callback: NotificationsCallback?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        val load = state.participants.loadState
        when {
            load.refresh is Error -> Unit
            load.append is Error -> Unit
            load.refresh is Loading -> loader(load)
            else -> {
                val itemCount = state.participants.itemCount
                participantsList(
                    participants = state.participants,
                    participantsStates = state.participantsStates,
                    itemCount = itemCount,
                    emojiList = state.emojiList,
                    notification = state.notification,
                    callback = callback
                )
                if(load.append is Loading) loader(load)
                takeEmotionLabel(itemCount)
                itemSpacer(20.dp)
            }
        }
    }
}

private fun LazyListScope.participantsList(
    participants: LazyPagingItems<UserModel>,
    participantsStates: List<Int>,
    itemCount: Int,
    emojiList: List<EmojiModel>,
    notification: NotificationModel,
    callback: NotificationsCallback?,
) {
    itemsIndexed(participants) { index, item ->
        item?.let { member ->
            Participant(
                index = index,
                member = member,
                size = itemCount,
                unwrap = participantsStates
                    .contains(index),
                memberEmoji = member
                    .meetRating?.emoji,
                emojiList = emojiList,
                onClick = { part ->
                    callback?.onParticipantClick(part)
                }
            ) { emoji ->
                member.id?.let {
                    callback?.onEmojiClick(notification, emoji, it)
                }
            }
        }
    }
}

private fun LazyListScope.takeEmotionLabel(
    count: Int,
) {
    item {
        if(count != 0) Text(
            text = stringResource(
                R.string.notification_send_emotion
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 16.dp),
            style = typography.labelSmall
                .copy(colorScheme.onTertiary)
        )
    }
}

@Composable
private fun MyNotification(
    state: ObserveNotificationState,
    callback: NotificationsCallback?,
    modifier: Modifier = Modifier,
) {
    if(state.notification.parent.meeting?.memberState
        != IS_ORGANIZER
    ) {
        NotificationItem(
            state = NotificationItemState(
                notification = state.notification,
                rowState = DragRowState(1f),
                shape = shapes.medium,
                duration = getDifferenceOfTime(
                    state.notification.date
                ),
                emojiList = state.notificationEmojiList
            ),
            modifier = modifier,
            callback = callback
        )
    }
}

private fun LazyListScope.loader(
    load: CombinedLoadStates,
) {
    item { PagingLoader(load) }
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
        Content(
            member = member,
            memberEmoji = memberEmoji,
            unwrap = unwrap,
            emojiList = emojiList,
            onEmojiClick = onEmojiClick
        )
    }
    Divide(index, size)
}

@Composable
private fun Divide(index: Int, size: Int) {
    if(index < size - 1) Row {
        GDivider(
            modifier = Modifier.width(60.dp),
            color = colorScheme.primaryContainer
        )
        GDivider(Modifier.weight(1f))
    }
}

@Composable
private fun Content(
    member: UserModel,
    memberEmoji: EmojiModel?,
    unwrap: Boolean,
    emojiList: List<EmojiModel>,
    onEmojiClick: ((EmojiModel) -> Unit)?,
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
                image = member.avatar
                    ?.thumbnail?.url ?: "",
                emoji = member.emoji,
                group = member.group ?: DEFAULT,
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
}