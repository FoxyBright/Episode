package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItem
import ru.rikmasters.gilty.presentation.ui.shared.DateTimeCard
import ru.rikmasters.gilty.presentation.ui.theme.Gradients
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingBottomSheetTopBarComposePreview() {
    GiltyTheme {
        MeetingBottomSheetTopBarCompose(
            Modifier,
            DemoFullMeetingModel,
            "2 часа"
        )
    }
}

@Composable
fun MeetingBottomSheetTopBarCompose(
    modifier: Modifier = Modifier,
    meetingModel: FullMeetingModel,
    eventDuration: String
) {
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
        ) {
            Text(
                meetingModel.title,
                color = ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
            IconButton({ }) {
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
                        DemoShortCategoryModel,
                        true,
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(25.dp, -(25).dp)
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
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.Body1Bold,
        )
    }
}