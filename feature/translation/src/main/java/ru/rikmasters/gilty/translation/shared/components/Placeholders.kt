package ru.rikmasters.gilty.translation.shared.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.visualizer.amplitude.AudioRecordView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun LowConnectionMicroOffVideoOff(
    meetingModel: FullMeetingModel?,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GCachedImage(
            url = meetingModel?.organizer?.avatar?.thumbnail?.url,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(
            modifier = Modifier.height(9.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_microphone_off_voice),
            contentDescription = "Microphone off",
            tint = Color.Unspecified
        )
        Spacer(
            modifier = Modifier.height(11.dp)
        )
        Row(
            modifier = Modifier.padding(
                vertical = 6.dp,
                horizontal = 16.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_weak_connection),
                contentDescription = "Microphone off",
                tint = ThemeExtra.colors.white
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.translations_weak_connection),
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeExtra.colors.white
            )
        }
    }
}

@Composable
fun LowConnectionMicroOff(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(80.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_microphone_off_voice),
                contentDescription = "Microphone off",
                tint = Color.Unspecified
            )
        }
        Surface(
            color = ThemeExtra.colors.thirdOpaqueGray,
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = 6.dp,
                    horizontal = 16.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_weak_connection),
                    contentDescription = "Microphone off",
                    tint = ThemeExtra.colors.white
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.translations_weak_connection),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeExtra.colors.white
                )
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun Reconnecting(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_reconnecting),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.translations_reconnecting),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.translations_reconnecting_wait),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NoConnection(
    onReconnectCLicked: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_no_connection),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.translations_no_connection),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.translations_no_connection_wait),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = ThemeExtra.colors.mainDayGreen,
            modifier = Modifier.clickable { onReconnectCLicked() }
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.translations_no_connection_refresh),
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeExtra.colors.white
            )
        }
    }
}

@Composable
fun Paused(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_pause),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.translation_paused),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.translation_paused_text),
            color = ThemeExtra.colors.white,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MicroWave() {
    Icon(
        painter = painterResource(id = R.drawable.ic_microphone_off_voice),
        contentDescription = "Microphone off",
        tint = Color.Unspecified
    )
    // TODO: Сделать полоску голоса
}

@Composable
fun ProfileAvatar(
    meetingModel: FullMeetingModel?,
    modifier: Modifier
) {
    GCachedImage(
        url = meetingModel?.organizer?.avatar?.thumbnail?.url,
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}
