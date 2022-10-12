package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.enumeration.ConditionType
import ru.rikmasters.gilty.presentation.model.enumeration.MeetType
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.MemberModel
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfileMeetingBottomSheetPreview() {
    GiltyTheme {
        ProfileMeetingBottomSheet(
            Modifier,
            ProfileMeetingBottomSheetState(
                DemoFullMeetingModel,
                listOf(DemoMemberModel, DemoMemberModel),
                "2 часа"
            )
        )
    }
}

data class ProfileMeetingBottomSheetState(
    val meetingModel: FullMeetingModel,
    val memberList: List<MemberModel>,
    val eventDuration: String
)

interface ProfileMeetingBottomSheetCallback {
    fun onConfirm()
    fun onMemberClick()
}

@Composable
fun ProfileMeetingBottomSheet(
    modifier: Modifier = Modifier,
    state: ProfileMeetingBottomSheetState,
    callback: ProfileMeetingBottomSheetCallback? = null
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        MeetingBottomSheetTopBarCompose(
            Modifier,
            state.meetingModel,
            state.eventDuration
        )
        Card(
            Modifier
                .padding(top = 13.dp)
                .fillMaxWidth(),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
        ) {
            Text(
                state.meetingModel.title,
                Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            stringResource(R.string.meeting_terms),
            Modifier.padding(top = 28.dp),
            style = ThemeExtra.typography.H3
        )
        Card(
            Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            MaterialTheme.shapes.extraSmall,
            CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
        ) {
            Text(
                when (state.meetingModel.type) {
                    MeetType.GROUP -> stringResource(id = R.string.meeting_group_type)
                    MeetType.ANONYMOUS -> stringResource(id = R.string.meeting_anon_type)
                    MeetType.PERSONAL -> stringResource(id = R.string.meeting_personal_type)
                },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Divider(Modifier.padding(start = 16.dp))
            Text(
                when (state.meetingModel.condition) {
                    ConditionType.FREE -> stringResource(R.string.condition_free)
                    ConditionType.DIVIDE -> stringResource(R.string.condition_divide)
                    ConditionType.MEMBER_PAY -> stringResource(R.string.condition_member_pay)
                    ConditionType.NO_MATTER -> stringResource(R.string.condition_no_matter)
                    ConditionType.ORGANIZER_PAY -> stringResource(R.string.condition_organizer_pay)
                },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        Row(Modifier.padding(top = 28.dp)) {
            Text(
                stringResource(R.string.members),
                style = ThemeExtra.typography.H3
            )
            Text(
                "${state.memberList.size}/${state.meetingModel.memberCount}",
                Modifier.padding(start = 8.dp),
                MaterialTheme.colorScheme.primary,
                style = ThemeExtra.typography.H3
            )
            Image(
                painterResource(
                    if (state.meetingModel.isPrivate) {
                        R.drawable.ic_lock_close
                    } else R.drawable.ic_lock_open
                ), null,
                Modifier.padding(start = 8.dp)
            )
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(MaterialTheme.shapes.large)
                .background(ThemeExtra.colors.cardBackground)
        ) {
            itemsIndexed(state.memberList) { index, member ->
                Row(
                    Modifier.fillMaxWidth().clickable { callback?.onMemberClick() },
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (index < state.memberList.size - 1)
                    Divider(Modifier.padding(start = 60.dp))
            }
        }
        GradientButton(
            { callback?.onConfirm() },
            Modifier
                .padding(16.dp, 28.dp)
                .align(Alignment.CenterHorizontally),
            stringResource(R.string.meeting_join_button_name)
        )

    }
}