package ru.rikmasters.gilty.translation.shared.components

import android.view.SurfaceHolder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

@OptIn(ExperimentalMaterial3Api::class)
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
            color = ThemeExtra.colors.thirdOpaqueGray,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = if (enabled) painterResource(id = R.drawable.ic_video_active)
                    else painterResource(id = R.drawable.ic_video_inactive),
                    contentDescription = "turn on/off camera",
                    tint = ThemeExtra.colors.white
                )
            }
        }
    } else {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                painter = if (enabled) painterResource(id = R.drawable.ic_video_active)
                else painterResource(id = R.drawable.ic_video_inactive),
                contentDescription = "turn on/off camera",
                tint = ThemeExtra.colors.white
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicrophoneItem(
    enabled: Boolean,
    onClick: () -> Unit,
    roundedBackground: Boolean = false,
) {
    if (roundedBackground) {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = ThemeExtra.colors.thirdOpaqueGray,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = if (enabled) painterResource(id = R.drawable.ic_micro_active)
                    else painterResource(id = R.drawable.ic_micro_inactive),
                    contentDescription = "turn on/off microphone",
                    tint = ThemeExtra.colors.white
                )
            }
        }
    } else {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                painter = if (enabled) painterResource(id = R.drawable.ic_micro_active)
                else painterResource(id = R.drawable.ic_micro_inactive),
                contentDescription = "turn on/off microphone",
                tint = ThemeExtra.colors.white
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeFacingItem(
    onClick: () -> Unit,
    roundedBackground: Boolean = false,
) {
    if (roundedBackground) {
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(46.dp),
            color = ThemeExtra.colors.thirdOpaqueGray,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "change camera",
                    tint = ThemeExtra.colors.white
                )
            }
        }
    } else {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "change camera",
                tint = ThemeExtra.colors.white
            )
        }
    }
}

@Composable
fun CameraView(
    initCamera: (OpenGlView) -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val view = OpenGlView(it)
            view.keepScreenOn = true
            view.isKeepAspectRatio = true
            view.setAspectRatioMode(AspectRatioMode.AdjustRotate)
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
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(29.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close_translations),
            contentDescription = "Close preview",
            tint = Color.Unspecified
        )
    }
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

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerItem(
    time: String,
    onClick: () -> Unit,
    isHighlight: Boolean,
    addTimerTime: String,
) {
    val density = LocalDensity.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ThemeExtra.colors.thirdOpaqueGray,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 8.dp,
                top = 6.5.dp,
                bottom = 6.5.dp
            )
        ) {
            Row {
                Column {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer_clock),
                        contentDescription = "timer",
                        tint = ThemeExtra.colors.white
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    AnimatedVisibility(
                        visible = addTimerTime.isNotBlank(),
                        enter = slideInVertically {
                            with(density) { -40.dp.roundToPx() }
                        } + expandVertically(
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutVertically {
                            with(density) { -40.dp.roundToPx() }
                        } + shrinkVertically(
                            shrinkTowards = Alignment.Top
                        ) + fadeOut(
                            targetAlpha = 0.3f
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus_gradient),
                            contentDescription = "timer",
                            tint = Color.Unspecified
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = time,
                        color = ThemeExtra.colors.white,
                        style = if (isHighlight) ThemeExtra.typography.TranslationSmallButton.copy(
                            brush = Brush.horizontalGradient(Gradients.green())
                        ) else
                            ThemeExtra.typography.TranslationSmallButton
                    )
                    AnimatedVisibility(
                        visible = addTimerTime.isNotBlank()
                    ) {
                        Text(
                            text = addTimerTime,
                            color = ThemeExtra.colors.mainDayGreen,
                            style = if (isHighlight) ThemeExtra.typography.TranslationSmallButton.copy(
                                brush = Brush.horizontalGradient(Gradients.green())
                            ) else
                                ThemeExtra.typography.TranslationSmallButton
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ChatItem(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chat),
            contentDescription = "Chat",
            tint = ThemeExtra.colors.white
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersCountItem(
    membersCount: Int,
    onClick: () -> Unit,
) {
    BadgedBox(
        badge = {
            if (membersCount > 0) {
                Badge(
                    containerColor = ThemeExtra.colors.mainDayGreen,
                    contentColor = ThemeExtra.colors.white
                ) {
                    Text(
                        text = if (membersCount >= 1000) {
                            if (membersCount >= 1000000) {
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
        },
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Chat",
            tint = ThemeExtra.colors.white
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraOrientationRow(
    onChange: (StreamerFacing) -> Unit,
    selectedStreamerFacing: StreamerFacing,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            color = if (selectedStreamerFacing == StreamerFacing.FRONT) {
                ThemeExtra.colors.thirdOpaqueGray
            } else {
                Color.Transparent
            },
            shape = RoundedCornerShape(10.dp),
            onClick = { onChange(StreamerFacing.FRONT) },
            enabled = selectedStreamerFacing != StreamerFacing.FRONT
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
            color = if (selectedStreamerFacing == StreamerFacing.BACK) {
                ThemeExtra.colors.thirdOpaqueGray
            } else {
                Color.Transparent
            },
            shape = RoundedCornerShape(10.dp),
            onClick = { onChange(StreamerFacing.BACK) },
            enabled = selectedStreamerFacing != StreamerFacing.BACK
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
fun CommentPanel(
    onSendMessage: (String) -> Unit,
    modifier: Modifier,
) {
    var messageText by remember { mutableStateOf("") }
    Box(
        modifier
            .fillMaxWidth()
            .background(
                ThemeExtra.colors.messageBar,
                ThemeExtra.shapes.chatRoundedShape
            )
    ) {
        Box(
            Modifier
                .padding(vertical = 12.dp)
                .padding(start = 16.dp, end = 50.dp)
        ) {
            BasicTextField(
                messageText, { messageText = it },
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                maxLines = 1,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = ThemeExtra.colors.white
                ),
                cursorBrush = SolidColor(ThemeExtra.colors.mainDayGreen),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
            ) {
                if (messageText.isEmpty()) Text(
                    stringResource(id = R.string.translations_chat_commentary), Modifier,
                    Color(0xFFCAC4D0), style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal
                    )
                ); it()
            }
        }
        if (messageText.isNotBlank()) SendButton(
            ThemeExtra.colors.mainDayGreen,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
        ) {
            onSendMessage(messageText)
            messageText = ""
        }
    }
}

@Composable
private fun SendButton(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.ic_send),
            (null), Modifier
                .padding(vertical = 6.dp)
                .padding(start = 8.dp, end = 4.dp)
                .size(18.dp), Color.White
        )
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
    isOrganizer: Boolean
) {
    var expandedPopUp by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        GCachedImage(
            url = user.avatar?.thumbnail?.url,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 18.dp,
                        bottom = 18.dp,
                        end = 8.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
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
                }
                Box {
                    IconButton(onClick = { expandedPopUp = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_kebab),
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expandedPopUp,
                        onDismissRequest = { expandedPopUp = false },
                        modifier = Modifier.background(
                            color = ThemeExtra.colors.mainCard
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
                            onClick = {
                                expandedPopUp = false
                                onComplainClicked()
                            },
                            contentPadding = PaddingValues(8.dp)
                        )
                        if (isOrganizer) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(id = R.string.translations_members_delete),
                                        color = ThemeExtra.colors.white,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    expandedPopUp = false
                                    onDeleteClicked()
                                }
                            )
                        }
                    }
                }
            }
            Divider(color = Color(0xFF464649))
        }
    }
}

@Composable
fun MessageItem(
    messageModel: TranslationMessageModel,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
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