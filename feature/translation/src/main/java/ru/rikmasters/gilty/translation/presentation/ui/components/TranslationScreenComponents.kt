package ru.rikmasters.gilty.translation.presentation.ui.components

import android.view.SurfaceHolder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.rtplibrary.view.AspectRatioMode
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.Facing

@Composable
fun CameraItem(
    enabled: Boolean,
    onClick: () -> Unit,
    roundedBackground: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clickable { onClick() }
    ) {
        if(roundedBackground) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(46.dp),
                color = ThemeExtra.colors.thirdOpaqueGray
            ) {}
        }
        Icon(
            painter = if(enabled) painterResource(id = R.drawable.ic_video_active)
            else painterResource(id = R.drawable.ic_video_inactive),
            contentDescription = "turn on/off camera",
            tint = ThemeExtra.colors.white,
            modifier = Modifier
                .clickable { onClick() }
                .align(Alignment.Center)
        )
    }
}

@Composable
fun MicrophoneItem(
    enabled: Boolean,
    onClick: () -> Unit,
    roundedBackground: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clickable { onClick() }
    ) {
        if(roundedBackground) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(46.dp),
                color = ThemeExtra.colors.thirdOpaqueGray
            ) {}
        }
        Icon(
            painter = if(enabled) painterResource(id = R.drawable.ic_micro_active)
            else painterResource(id = R.drawable.ic_micro_inactive),
            contentDescription = "turn on/off microphone",
            tint = ThemeExtra.colors.white,
            modifier = Modifier
                .clickable { onClick() }
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ChangeFacingItem(
    onClick: () -> Unit,
    roundedBackground: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clickable { onClick() }
    ) {
        if(roundedBackground) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(46.dp),
                color = ThemeExtra.colors.thirdOpaqueGray
            ) {}
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_refresh),
            contentDescription = "change camera",
            tint = ThemeExtra.colors.white,
            modifier = Modifier
                .clickable { onClick() }
                .align(Alignment.Center)
        )
    }
}

@Composable
fun CameraView(
    initCamera: (OpenGlView) -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = ThemeExtra.colors.preDarkColor
            ),
        factory = {
            val view = OpenGlView(it)
            view.keepScreenOn = true
            view.isKeepAspectRatio = true
            view.setAspectRatioMode(AspectRatioMode.Fill)
            view.holder.addCallback(surfaceHolderCallback)
            view
        },
        update = { view ->
            initCamera(view)
        }
    )
}

@Composable
fun CloseButton(
    onClick: () -> Unit,
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
    radius: Dp,
) {
    GCachedImage(
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
    categoryName: String,
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
    meetingModel: FullMeetingModel,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarItem(
            src = meetingModel.organizer.avatar?.thumbnail?.url,
            radius = 30.dp
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

@OptIn(ExperimentalTextApi::class)
@Composable
fun TimerItem(
    time: String,
    onClick: () -> Unit,
    isHighlight: Boolean,
    addTimerTime: String,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ThemeExtra.colors.thirdOpaqueGray,
        modifier = Modifier.clickable { onClick() }
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
            Column {
                Text(
                    text = time,
                    color = ThemeExtra.colors.white,
                    style = if(isHighlight) ThemeExtra.typography.TranslationSmallButton.copy(
                        brush = Brush.horizontalGradient(Gradients.green())
                    ) else
                        ThemeExtra.typography.TranslationSmallButton
                )
                if(addTimerTime.isNotBlank()) {
                    Text(
                        text = addTimerTime,
                        color = ThemeExtra.colors.mainDayGreen,
                        style = if(isHighlight) ThemeExtra.typography.TranslationSmallButton.copy(
                            brush = Brush.horizontalGradient(Gradients.green())
                        ) else
                            ThemeExtra.typography.TranslationSmallButton
                    )
                }
            }
        }
    }
}


@Composable
fun ChatItem(onClick: () -> Unit) {
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
    onClick: () -> Unit,
) {
    BadgedBox(
        badge = {
            if(membersCount > 0) {
                Badge(
                    containerColor = ThemeExtra.colors.mainDayGreen,
                    contentColor = ThemeExtra.colors.white
                ) {
                    Text(
                        text = if(membersCount >= 1000) {
                            if(membersCount >= 1000000) {
                                "${membersCount / 1000000}КK"
                            } else {
                                "${membersCount / 1000}К"
                            }
                        } else {
                            membersCount.toString()
                        },
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
    selectedFacing: Facing,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .clickable { onChange() },
            color = if(selectedFacing == Facing.FRONT) {
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
            color = if(selectedFacing == Facing.BACK) {
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
    modifier: Modifier,
) {
    Surface(
        modifier = modifier
            .height(5.dp)
            .width(40.dp),
        shape = RoundedCornerShape(11.dp),
        color = ThemeExtra.colors.bottomSheetGray
    ) {}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPanel(
    onSendMessage: (String) -> Unit,
    modifier: Modifier,
) {
    var messageText by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(48.dp),
        color = ThemeExtra.colors.messageBar,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(50.dp)
        ) {
            TextField(
                value = messageText,
                onValueChange = {
                    messageText = it
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ThemeExtra.colors.white,
                    containerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.Transparent,
                    cursorColor = ThemeExtra.colors.white
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.translations_chat_commentary),
                        color = Color(0xFFCAC4D0),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_send_rounded),
                contentDescription = "send message",
                tint = Color.Unspecified,
                modifier = Modifier
                    .weight(1f)
                    .size(32.dp)
                    .padding(start = 4.dp)
                    .clickable {
                        onSendMessage(messageText)
                        messageText = ""
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearchValueChanged: (String) -> Unit,
    searchValue: String,
) {
    Surface(
        color = ThemeExtra.colors.mainCard,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.magnifier),
                contentDescription = "search",
                tint = ThemeExtra.colors.zirkon
            )
            Spacer(modifier = Modifier.width(24.dp))
            TextField(
                value = searchValue,
                onValueChange = {
                    onSearchValueChanged(it)
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ThemeExtra.colors.white,
                    containerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.Transparent,
                    cursorColor = ThemeExtra.colors.white
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_placeholder),
                        color = ThemeExtra.colors.zirkon,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                singleLine = true
            )
        }
    }
}

@Composable
fun MemberItem(
    user: FullUserModel,
    onComplainClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var expandedPopUp by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize()) {
        Row {
            GCachedImage(
                url = user.avatar?.thumbnail?.url,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(
                    modifier = Modifier.padding(
                        top = 18.dp,
                        bottom = 18.dp,
                        end = 18.dp,
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.username}, ${user.age}",
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    user.emoji?.let {
                        Image(
                            painter = painterResource(
                                id = EmojiModel.getEmoji(
                                    it.type
                                ).path.toInt()
                            ),
                            contentDescription = "Emoji"
                        )
                    }
                    IconButton(onClick = { expandedPopUp = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_kebab),
                            contentDescription = "More"
                        )
                    }
                }
                Divider()
            }
        }
    }
    DropdownMenu(
        expanded = expandedPopUp,
        onDismissRequest = { expandedPopUp = false },
        modifier = Modifier.background(
            color = ThemeExtra.colors.mainCard,
            shape = RoundedCornerShape(14.dp)
        )
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.translations_members_complain),
                    color = ThemeExtra.colors.white,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = onComplainClicked
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.translations_members_delete),
                    color = ThemeExtra.colors.white,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onClick = onDeleteClicked
        )
    }
}

@Composable
fun MessageItem(
    messageModel: TranslationMessageModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        GCachedImage(
            url = messageModel.author.avatar?.thumbnail?.url,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row {
                Text(
                    text = "${messageModel.author.username}, ${messageModel.author.age}",
                    color = ThemeExtra.colors.white,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                messageModel.author.emoji?.let {
                    Image(
                        painter = painterResource(
                            id = EmojiModel.getEmoji(
                                it.type
                            ).path.toInt()
                        ),
                        contentDescription = "Emoji"
                    )
                }
            }
            Text(
                text = messageModel.text,
                color = ThemeExtra.colors.white,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}