package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.MemberModel
import ru.rikmasters.gilty.presentation.ui.shared.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItem
import ru.rikmasters.gilty.presentation.ui.shared.DateTimeCard
import ru.rikmasters.gilty.presentation.ui.theme.Gradients
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun ProfileMeetingBottomSheetPreview() {
    GiltyTheme {
        ProfileMeetingBottomSheet(Modifier, DemoFullMeetingModel, listOf(DemoMemberModel), "2 часа")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileMeetingBottomSheet(
    modifier: Modifier = Modifier,
    meetingModel: FullMeetingModel,
    memberList: List<MemberModel>,
    eventDuration: String
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                meetingModel.title,
                color = ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
            IconButton({ /*TODO*/ }) {
                Icon(
                    painterResource(R.drawable.ic_kebab),
                    null,
                    tint = ThemeExtra.colors.grayIcon
                )
            }
        }
        Row(Modifier.padding(top = 12.dp)) {
            AsyncImage(
                meetingModel.organizer.avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .weight(1f)
                    .size(180.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gb)
            )
            Spacer(Modifier.width(18.dp))
            Card(
                Modifier
                    .weight(1f)
                    .size(180.dp),
                ThemeExtra.shapes.cardShape,
                CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    CategoryItem(
                        DemoCategoryModel,
                        true,
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(
                                (CATEGORY_ELEMENT_SIZE / 6).dp,
                                (-CATEGORY_ELEMENT_SIZE / 6).dp
                            )
                    )
                    Column(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.meeting_profile_bottom_today_label),
                            Modifier.padding(bottom = 8.dp),
                            ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.Body1Bold
                        )
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            DateTimeCard(
                                DemoMeetingModel.dateTime,
                                Gradients().primary(),
                                true,
                                Modifier.weight(1f)
                            )
                            Box(
                                Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(ThemeExtra.colors.grayIcon)
                            ) {
                                Text(
                                    eventDuration,
                                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = ThemeExtra.typography.SubHeadEb,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        Text(
            "${meetingModel.organizer.username}, ${meetingModel.organizer.age}",
            Modifier.padding(top = 9.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Card(
            Modifier
                .padding(top = 13.dp)
                .fillMaxWidth(),
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
        ) {
            Text(
                meetingModel.title,
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
                when (meetingModel.type) {
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
                when (meetingModel.condition) {
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
                "${memberList.size}/${meetingModel.memberCount}",
                Modifier.padding(start = 8.dp),
                MaterialTheme.colorScheme.primary,
                style = ThemeExtra.typography.H3
            )
            Image(
                painterResource(
                    if (meetingModel.isPrivate) {
                        R.drawable.ic_lock_close
                    } else R.drawable.ic_lock_open
                ), null,
                Modifier.padding(start = 8.dp)
            )
        }
        memberList.forEach {
            Card(
                { /*TODO*/ },
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                AsyncImage(
                    it.avatar.id,
                    stringResource(R.string.meeting_avatar),
                    Modifier.size(40.dp),
                    painterResource(R.drawable.gb),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}