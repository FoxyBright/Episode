package ru.rikmasters.gilty.translation.presentation.ui.content.expired

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.presentation.ui.components.ChatItem
import ru.rikmasters.gilty.translation.presentation.ui.components.CloseButton
import ru.rikmasters.gilty.translation.presentation.ui.components.MembersCountItem
import ru.rikmasters.gilty.translation.presentation.ui.components.StreamerItem
import ru.rikmasters.gilty.translation.presentation.ui.components.TimerItem

@Composable
fun TranslationExpired(
    thumbnailUrl: String?,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    membersCount: Int,
    onCloseClicked: () -> Unit,
    onTurnOnClicked: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    ThemeExtra.colors.preDarkColor,
                    Color(0xFF070707)
                )
            )
        )) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            thumbnailUrl?.let {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        meetingModel?.let {
                            StreamerItem(meetingModel = meetingModel)
                            Spacer(modifier = Modifier.weight(1f))
                            TimerItem(time = remainTime, onClick = {}, isHighlight = false, addTimerTime = "")
                            Spacer(modifier = Modifier.width(12.dp))
                            CloseButton(onClick = onCloseClicked)
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChatItem(onClick = {})
                        MembersCountItem(
                            membersCount = membersCount,
                            onClick = {}
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bad),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        text = stringResource(id = R.string.translations_expired_time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ThemeExtra.colors.white
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        text = stringResource(id = R.string.translations_expired_time_append),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = ThemeExtra.colors.white
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GradientButton(
                        text = stringResource(id = R.string.translations_start_strean),
                        online = true,
                        onClick = onTurnOnClicked
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}