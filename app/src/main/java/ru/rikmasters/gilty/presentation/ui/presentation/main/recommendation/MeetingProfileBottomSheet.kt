package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfileMeetingBottomSheetPreview() {

    GiltyTheme {
        ProfileMeetingBottomSheet(
            meetingModel = DemoFullMeetingModel, memberList = listOf(DemoMemberModel))
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMeetingBottomSheet(
    modifier: Modifier = Modifier,
    meetingModel: FullMeetingModel,
    memberList: List<MemberModel>
) {

    Column(
        modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = meetingModel.title,
                style = ThemeExtra.typography.H3,
                color = ThemeExtra.colors.mainTextColor)

            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_kebab), contentDescription = "", tint = ThemeExtra.colors.grayIcon)
            }
        }
        
        Row(Modifier.padding(top = 12.dp)) {

            AsyncImage(
                model = meetingModel.organizer.avatar.id,
                contentDescription = "avatar",
                modifier = Modifier
                    .size(150.dp, 155.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop)
            
            Spacer(modifier = Modifier.width(18.dp))
            
            Card(
                Modifier.size(150.dp, 155.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                    ThemeExtra.colors.cardBackground),
                shape = ThemeExtra.shapes.cardShape) {
            }
        }
        
        Text(text = "${meetingModel.organizer.username}, " +
                    "${meetingModel.organizer.age}",
            modifier = Modifier.padding(top = 9.dp),
            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        
        Card(
            Modifier
                .padding(top = 13.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(ThemeExtra.colors.cardBackground)) {
            
            Text(
                text = meetingModel.title,
                Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
        }

        Text(
            text = stringResource(id = R.string.meeting_terms),
            Modifier.padding(top = 28.dp),
            style = ThemeExtra.typography.H3)
        
        Card(
            Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(
                containerColor = ThemeExtra.colors.cardBackground)) {
            
            Text(
                text = when (meetingModel.type) {
                    MeetType.GROUP -> stringResource(id = R.string.meeting_group_type)
                    MeetType.ANONYMOUS -> stringResource(id = R.string.meeting_anon_type)
                    MeetType.PERSONAL -> stringResource(id = R.string.meeting_personal_type)
            },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp))

            Divider(Modifier.padding(start = 16.dp))

            Text(
                text = when (meetingModel.condition) {
                    ConditionType.FREE -> stringResource(id = R.string.condition_free)
                    ConditionType.DIVIDE -> stringResource(id = R.string.condition_divide)
                    ConditionType.MEMBER_PAY -> stringResource(id = R.string.condition_member_pay)
                    ConditionType.NO_MATTER -> stringResource(id = R.string.condition_no_matter)
                    ConditionType.ORGANIZER_PAY -> stringResource(id = R.string.condition_organizer_pay)
                },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp))

        }

        Row(Modifier.padding(top = 28.dp)) {
            Text(
                text = stringResource(id = R.string.members),
                style = ThemeExtra.typography.H3)
            
            Text(text = "${memberList.size}/${meetingModel.memberCount}",
                Modifier.padding(start = 8.dp),
                style = ThemeExtra.typography.H3,
                color = MaterialTheme.colorScheme.primary)

            Image(painter = painterResource(id =
            if (meetingModel.isPrivate) {
                R.drawable.ic_lock_close
            } else R.drawable.ic_lock_open), contentDescription = "",
                Modifier.padding(start = 8.dp))
        }

        memberList.forEach { member ->

            Card(onClick = { /*TODO*/ },
                Modifier.padding(top = 12.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = ThemeExtra.colors.cardBackground)) {

                AsyncImage(model = member.avatar.id, contentDescription = "")

            }

        }

    }
}