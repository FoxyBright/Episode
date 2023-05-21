package ru.rikmasters.gilty.translation.presentation.ui.components

import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.AspectRatioMode
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.Facing

@Composable
fun CameraItem(
    enabled: Boolean,
    onClick: () -> Unit,
    roundedBackground: Boolean = false
) {
    if (roundedBackground) {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = ThemeExtra.colors.thirdOpaqueGray
        ) {
            Icon(
                painter = if (enabled) painterResource(id = R.drawable.ic_video_active)
                else painterResource(id = R.drawable.ic_video_inactive),
                contentDescription = "turn on/off camera",
                tint = ThemeExtra.colors.white,
                modifier = Modifier.clickable { onClick() }
            )
        }
    } else {
        Icon(
            painter = if (enabled) painterResource(id = R.drawable.ic_video_active)
            else painterResource(id = R.drawable.ic_video_inactive),
            contentDescription = "turn on/off camera",
            tint = ThemeExtra.colors.white,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun MicrophoneItem(
    enabled: Boolean,
    onClick: () -> Unit,
    roundedBackground: Boolean = false
) {
    if (roundedBackground) {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = ThemeExtra.colors.thirdOpaqueGray
        ) {
            Icon(
                painter = if (enabled) painterResource(id = R.drawable.ic_micro_active)
                else painterResource(id = R.drawable.ic_micro_inactive),
                contentDescription = "turn on/off microphone",
                tint = ThemeExtra.colors.white,
                modifier = Modifier.clickable { onClick() }
            )
        }
    } else {
        Icon(
            painter = if (enabled) painterResource(id = R.drawable.ic_micro_active)
            else painterResource(id = R.drawable.ic_micro_inactive),
            contentDescription = "turn on/off microphone",
            tint = ThemeExtra.colors.white,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun ChangeFacingItem(
    onClick: () -> Unit,
    roundedBackground: Boolean = false
) {
    if (roundedBackground) {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = ThemeExtra.colors.thirdOpaqueGray
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "change camera",
                tint = ThemeExtra.colors.white,
                modifier = Modifier.clickable { onClick() }
            )
        }
    } else {
        Icon(
            painter = painterResource(id = R.drawable.ic_refresh),
            contentDescription = "change camera",
            tint = ThemeExtra.colors.white,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun CameraView(
    initCamera: (OpenGlView) -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val view = OpenGlView(it)
            view.keepScreenOn = true
            view.isKeepAspectRatio = true
            view.setAspectRatioMode(AspectRatioMode.Fill)
            view.holder.addCallback(surfaceHolderCallback)
            view
        },
        update = { view ->
            Log.d("TEST","UPDATED $view")
            initCamera(view)
        }
    )
}

@Composable
fun CloseButton(
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_close_translations),
        contentDescription = "Close preview",
        tint = Color.Unspecified,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

@Composable
fun AvatarItem(
    src: String?,
    radius: Dp
) {
    GCashedImage(
        url = src,
        modifier = Modifier
            .size(radius)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun UserNameItem(
    username: String?,
    age: Int?,
    emoji: EmojiModel?,
    categoryName: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$username, $age",
                color = ThemeExtra.colors.white,
                style = ThemeExtra.typography.TranslationSmallButton
            )
            Spacer(modifier = Modifier.width(6.dp))
            emoji?.let {
                Image(
                    painter = painterResource(id = EmojiModel.getEmoji(it.type).path.toInt()),
                    contentDescription = "emoji"
                )
            }
        }
        Spacer(modifier = Modifier.height(0.5.dp))
        Text(
            text = categoryName,
            color = ThemeExtra.colors.white,
            style = ThemeExtra.typography.TranslationSmallButton
        )
    }
}

@Composable
fun StreamerItem(
    meetingModel: FullMeetingModel
) {
    Row (verticalAlignment = Alignment.CenterVertically) {
        AvatarItem(
            src = meetingModel.organizer.thumbnail?.url,
            radius = 41.dp
        )
        Spacer(modifier = Modifier.width(11.dp))
        UserNameItem(
            username = meetingModel.organizer.username,
            age = meetingModel.organizer.age,
            emoji = meetingModel.organizer.emoji,
            categoryName = meetingModel.category.name
        )
    }
}

@Composable
fun TimerItem(
    time: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ThemeExtra.colors.thirdOpaqueGray
    ) {
        Row(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 8.dp,
                top = 6.5.dp,
                bottom = 6.5.dp
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_timer_clock),
                contentDescription = "timer",
                tint = ThemeExtra.colors.white
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = time,
                color = ThemeExtra.colors.white,
                style = ThemeExtra.typography.TranslationSmallButton
            )
        }
    }
}


@Composable
fun ChatItem( onClick: () -> Unit ) {
    Icon(
        painter = painterResource(id = R.drawable.ic_chat),
        contentDescription = "Chat",
        tint = ThemeExtra.colors.white,
        modifier = Modifier.clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersCountItem(
    membersCount: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (membersCount > 0) {
                Badge(
                    containerColor = ThemeExtra.colors.mainDayGreen,
                    contentColor = ThemeExtra.colors.white
                ) {
                    Text(
                        text = membersCount.toString(),
                        style = ThemeExtra.typography.TranslationBadge,
                        color = ThemeExtra.colors.white
                    )
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Chat",
            tint = ThemeExtra.colors.white,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun CameraOrientationRow(
    onChange: () -> Unit,
    selectedFacing: Facing
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .clickable { onChange() },
            color = if (selectedFacing == Facing.FRONT) {
                ThemeExtra.colors.thirdOpaqueGray
            } else {
                Color.Transparent
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = 6.dp,
                    horizontal = 16.dp
                ),
                text = stringResource(id = R.string.translations_front_camera),
                color = ThemeExtra.colors.white,
                style = ThemeExtra.typography.TranslationSmallButton
            )
        }
        Surface(
            modifier = Modifier
                .clickable { onChange() },
            color = if (selectedFacing == Facing.BACK) {
                ThemeExtra.colors.thirdOpaqueGray
            } else {
                Color.Transparent
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = 6.dp,
                    horizontal = 16.dp
                ),
                text = stringResource(id = R.string.translations_main_camera),
                color = ThemeExtra.colors.white,
                style = ThemeExtra.typography.TranslationSmallButton
            )
        }
    }
}

@Composable
fun BottomSheetDragItem(
    modifier: Modifier
) {
    Surface(
        modifier = modifier
            .height(5.dp)
            .width(40.dp),
        shape = RoundedCornerShape(11.dp),
        color = ThemeExtra.colors.bottomSheetGray
    ) {}
}
