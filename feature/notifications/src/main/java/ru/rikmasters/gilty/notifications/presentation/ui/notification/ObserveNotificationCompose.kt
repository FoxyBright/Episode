package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
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
            listOf(DemoUserModel, DemoUserModel),
            listOf(0), EmojiModel.list
        )
    )
}

data class ObserveNotificationState(
    val notification: NotificationModel,
    val participants: List<UserModel>,
    val participantsStates: List<Int>,
    val emojiList: List<EmojiModel>,
)

@Composable
fun ObserveNotification(
    state: ObserveNotificationState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null,
) {
    Column(modifier) {
        if(state.notification.parent.meeting?.memberState != IS_ORGANIZER)
            NotificationItem(
                NotificationItemState(
                    state.notification, DragRowState(1f),
                    MaterialTheme.shapes.medium,
                    getDifferenceOfTime(state.notification.date),
                    state.emojiList
                ), Modifier, callback
            )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(
                    colorScheme.primaryContainer,
                    MaterialTheme.shapes.large
                ),
        ) {
            itemsIndexed(state.participants) { index, participant ->
                Participant(
                    index, participant, state.participants.size,
                    state.participantsStates.contains(index),
                    participant.meetRating?.emoji, state.emojiList,
                    { part -> callback?.onParticipantClick(part) }
                ) { emoji ->
                    participant.id?.let {
                        callback?.onEmojiClick(
                            state.notification, emoji, it
                        )
                    }
                }
            }
        }
        if(state.participants.isNotEmpty()) Text(
            stringResource(R.string.notification_send_emotion),
            Modifier.padding(top = 6.dp, start = 16.dp),
            colorScheme.onTertiary,
            style = typography.labelSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Participant(
    index: Int, member: UserModel,
    size: Int, unwrap: Boolean,
    memberEmoji: EmojiModel?,
    emojiList: List<EmojiModel>,
    onClick: ((Int) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
) {
    Card(
        { onClick?.let { it(index) } },
        Modifier.fillMaxWidth(), (true),
        lazyItemsShapes(index, size, 14.dp),
        cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(bottom = 12.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp),
                SpaceBetween, CenterVertically
            ) {
                BrieflyRow(
                    ("${member.username}, ${member.age}"),
                    Modifier, member.avatar?.thumbnail?.url,
                    member.emoji,
                )
                memberEmoji?.let {
                    GEmojiImage(
                        memberEmoji,
                        Modifier.size(24.dp)
                    )
                } ?: run {
                    Icon(
                        if(unwrap) Filled.KeyboardArrowDown
                        else Filled.KeyboardArrowRight,
                        (null), Modifier.size(24.dp),
                        colorScheme.onTertiary
                    )
                }
            }
            if(unwrap && memberEmoji == null) EmojiRow(
                emojiList, Modifier.padding(
                    start = 60.dp, end = 20.dp
                )
            ) { emoji -> onEmojiClick?.let { it(emoji) } }
        }
    }; if(index < size - 1)
        Divider(Modifier.padding(start = 60.dp))
}