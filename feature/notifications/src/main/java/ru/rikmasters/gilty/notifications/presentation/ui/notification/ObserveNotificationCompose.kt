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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.item.Item
import ru.rikmasters.gilty.notifications.presentation.ui.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.EmojiRow
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
private fun ObserveNotificationPreview() {
    ObserveNotification(
        DemoNotificationMeetingOverModel,
        listOf(DemoMemberModel, DemoMemberModel),
        listOf(true, false)
    ) {}
}

@Composable
fun ObserveNotification(
    model: NotificationModel,
    participants: List<MemberModel>,
    participantsWrap: List<Boolean>,
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
) {
    Column(modifier) {
        if(model.type != NotificationType.RESPOND_ACCEPT)
            Item(
                NotificationItemState(
                    model, DragRowState(1f), MaterialTheme.shapes.medium,
                    getDifferenceOfTime(model.date)
                ), Modifier, (null), { emoji -> onEmojiClick?.let { it(emoji) } }, (null)
            )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(
                    colorScheme.primaryContainer,
                    MaterialTheme.shapes.large
                )
        ) {
            itemsIndexed(participants) { i, item ->
                Participant(
                    i, item, participants.size,
                    participantsWrap[i], { index -> onClick?.let { it(index) } }
                ) { emoji -> onEmojiClick?.let { it(emoji) } }
            }
        }
        Text(
            stringResource(R.string.notification_send_emotion),
            Modifier.padding(top = 6.dp, start = 16.dp),
            colorScheme.onTertiary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Participant(
    index: Int, member: MemberModel,
    size: Int, unwrap: Boolean,
    onClick: ((Int) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
) {
    Card(
        { onClick?.let { it(index) } },
        Modifier.fillMaxWidth(), (true),
        when(index) {
            0 -> shapes.largeTopRoundedShape
            size - 1 -> shapes.largeBottomRoundedShape
            else -> shapes.zero
        }, cardColors(colorScheme.primaryContainer)
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
                    "${member.username}, ${member.age}",
                    Modifier, member.avatar,
                    member.emoji,
                )
                Icon(
                    if(unwrap) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    (null), Modifier.size(24.dp),
                    colorScheme.onTertiary
                )
            }
            if(unwrap) EmojiRow(Modifier.padding(start = 60.dp, end = 20.dp))
            { emoji -> onEmojiClick?.let { it(emoji) } }
        }
    }; if(index < size - 1)
        Divider(Modifier.padding(start = 60.dp))
}