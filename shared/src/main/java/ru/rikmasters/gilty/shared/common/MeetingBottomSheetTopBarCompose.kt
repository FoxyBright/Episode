package ru.rikmasters.gilty.shared.common

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingBottomSheetTopBarComposePreview() {
    GiltyTheme {
        MeetingBottomSheetTopBarCompose(
            Modifier,
            DemoFullMeetingModel,
            ("2 часа"), (true)
        )
    }
}

@Composable
fun MeetingBottomSheetTopBarCompose(
    modifier: Modifier = Modifier,
    meetingModel: FullMeetingModel,
    eventDuration: String,
    private: Boolean = false
) {
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
        ) {
            Text(
                meetingModel.title,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton({ /* TODO Здесь должен быть PopUp */ }) {
                Icon(
                    painterResource(R.drawable.ic_kebab),
                    null,
                    tint = MaterialTheme.colorScheme.outlineVariant
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
            Meet(
                Modifier
                    .weight(1f)
                    .size(180.dp),
                eventDuration
            )
        }
        Row(verticalAlignment = CenterVertically) {
            BrieflyRow(
                text = "${meetingModel.organizer.username}, " +
                        "${meetingModel.organizer.age}",
                emoji = meetingModel.organizer.emoji
            )
            if (private) Text(
                stringResource(R.string.meeting_anon_type), Modifier,
                MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun Meet(
    modifier: Modifier = Modifier,
    eventDuration: String
) {
    Card(
        modifier, ThemeExtra.shapes.cardShape,
        CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            CategoryItem(
                DemoShortCategoryModel,
                true,
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(25.dp, -(25).dp)
            )
            MeetDetails(
                Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(12.dp),
                eventDuration
            )
        }
    }
}

@Composable
private fun MeetDetails(
    modifier: Modifier = Modifier,
    eventDuration: String
) {
    Column(modifier) {
        Text(
            stringResource(R.string.meeting_profile_bottom_today_label),
            Modifier.padding(bottom = 8.dp),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            DateTimeCard(
                DemoFullMeetingModel.dateTime,
                Gradients.red(),
                true,
                Modifier.weight(1f)
            )
            Box(
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    eventDuration,
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    Color.White, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}