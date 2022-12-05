package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfileMeetingBottomSheetPreview() {
    GiltyTheme {
        ProfileMeetingBottomSheet(
            Modifier,
            ProfileMeetingBottomSheetState(
                DemoFullMeetingModel,
                listOf(DemoMemberModel, DemoMemberModel, DemoMemberModel, DemoMemberModel),
                18, "2 часа"
            )
        )
    }
}

data class ProfileMeetingBottomSheetState(
    val meetingModel: FullMeetingModel,
    val memberList: List<MemberModel>,
    val distance: Int,
    val eventDuration: String
)

interface ProfileMeetingBottomSheetCallback {
    
    fun onConfirm()
    fun onMemberClick()
    fun openMap()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMeetingBottomSheet(
    modifier: Modifier = Modifier,
    state: ProfileMeetingBottomSheetState,
    callback: ProfileMeetingBottomSheetCallback? = null
) {
    LazyColumn(
        modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        item {
            MeetingBottomSheetTopBarCompose(
                Modifier, MeetingBottomSheetTopBarState(
                    state.meetingModel, state.eventDuration
                )
            )
        }
        item {
            Card(
                Modifier
                    .padding(top = 13.dp)
                    .fillMaxWidth(),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    state.meetingModel.title,
                    Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        item {
            Text(
                stringResource(R.string.meeting_terms),
                Modifier.padding(top = 28.dp),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Card(
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                MaterialTheme.shapes.extraSmall,
                CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    when(state.meetingModel.type) {
                        MeetType.GROUP -> stringResource(id = R.string.meeting_group_type)
                        MeetType.ANONYMOUS -> stringResource(id = R.string.meeting_anon_type)
                        MeetType.PERSONAL -> stringResource(id = R.string.meeting_personal_type)
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider(Modifier.padding(start = 16.dp))
                Text(
                    when(state.meetingModel.condition) {
                        ConditionType.FREE -> stringResource(R.string.condition_free)
                        ConditionType.DIVIDE -> stringResource(R.string.condition_divide)
                        ConditionType.MEMBER_PAY -> stringResource(R.string.condition_member_pay)
                        ConditionType.NO_MATTER -> stringResource(R.string.condition_no_matter)
                        ConditionType.ORGANIZER_PAY -> stringResource(R.string.condition_organizer_pay)
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        item {
            Row(Modifier.padding(top = 28.dp)) {
                Text(
                    stringResource(R.string.meeting_members),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "${state.memberList.size}/${state.meetingModel.memberCount}",
                    Modifier.padding(start = 8.dp),
                    MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
                Image(
                    painterResource(
                        if(state.meetingModel.isPrivate) R.drawable.ic_lock_close
                        else R.drawable.ic_lock_open
                    ), null,
                    Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
            }
        }
        itemsIndexed(state.memberList) { index, member ->
            Card(
                Modifier.fillMaxWidth(),
                when(index) {
                    0 -> ThemeExtra.shapes.largeTopRoundedShape
                    state.memberList.size - 1 -> ThemeExtra.shapes.largeBottomRoundedShape
                    else -> ThemeExtra.shapes.zero
                }, CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { callback?.onMemberClick() },
                    verticalAlignment = CenterVertically
                ) {
                    AsyncImage(
                        member.avatar.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(12.dp, 8.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        painterResource(R.drawable.gb),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        "${member.username}, ${member.age}",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if(index < state.memberList.size - 1) Divider(Modifier.padding(start = 60.dp))
            }
        }
        item {
            Row(Modifier.padding(top = 28.dp, bottom = 14.dp)) {
                Text(
                    stringResource(R.string.meeting_distance_from_user),
                    Modifier.padding(end = 4.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    stringResource(R.string.meeting_filter_label_distance, state.distance),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        item {
            Card(
                { callback?.openMap() },
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp), Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.meeting_watch_on_mup_button),
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        null, Modifier,
                        MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
        item {
            GradientButton(
                Modifier.padding(vertical = 28.dp),
                stringResource(R.string.meeting_join_button_name)
            ) { callback?.onConfirm() }
        }
    }
}