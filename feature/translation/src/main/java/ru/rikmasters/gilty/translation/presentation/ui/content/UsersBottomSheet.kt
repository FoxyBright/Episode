package ru.rikmasters.gilty.translation.presentation.ui.content

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.BackBlur
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.common.extentions.simpleVerticalScrollbar
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.model.BottomSheetState

@Composable
fun UsersBottomSheetContent(
    configuration: Configuration,
    membersCount: Int,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    membersList: LazyPagingItems<FullUserModel>?,
    onComplainClicked: (FullUserModel) -> Unit,
    onDeleteClicked: (FullUserModel) -> Unit,
    state: BottomSheetState,
    messagesList: LazyPagingItems<TranslationMessageModel>?,
    onSendMessage: (String) -> Unit
) {
    val scrollState = rememberLazyListState()
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = (configuration.screenHeightDp * 0.66).dp,
                        max = (configuration.screenHeightDp * 0.33).dp
                    )
                    .wrapContentWidth(unbounded = false)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .height(5.dp)
                        .width(40.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(11.dp),
                    color = ThemeExtra.colors.bottomSheetGray
                ) {}
                if (state == BottomSheetState.USERS) {
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                Column(
                    modifier = Modifier.simpleVerticalScrollbar(
                        state = scrollState,
                        width = 3.dp
                    )
                ) {
                    if ((membersCount == 0 && state == BottomSheetState.USERS && membersList?.loadState?.refresh is LoadState.NotLoading)
                        || (state == BottomSheetState.CHAT && messagesList?.itemCount == 0 && messagesList.loadState.refresh is LoadState.NotLoading)) {
                        Text(
                            text = if (state == BottomSheetState.USERS) {
                                stringResource(id = R.string.translations_members)
                            } else {
                                stringResource(id = R.string.translations_chat)
                            },
                            style = ThemeExtra.typography.TranslationTitlePreview,
                            color = ThemeExtra.colors.white,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(53.dp))
                        Image(
                            painter = painterResource(id = R.drawable.broken_heart),
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state == BottomSheetState.USERS) {
                                stringResource(id = R.string.translations_members_no_users)
                            } else {
                                stringResource(id = R.string.translations_members_no_messages)
                            },
                            color = ThemeExtra.colors.bottomSheetGray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(84.dp))
                    } else {
                        if (state == BottomSheetState.USERS) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.translations_members),
                                    style = ThemeExtra.typography.TranslationTitlePreview,
                                    color = ThemeExtra.colors.white
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = membersCount.toString(),
                                    style = ThemeExtra.typography.TranslationTitlePreview,
                                    color = ThemeExtra.colors.mainNightGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            SearchBar(
                                onSearchValueChanged = onSearchValueChange,
                                searchValue = searchValue
                            )
                            Spacer(modifier = Modifier.height(28.dp))
                        }
                        LazyColumn(
                            state = scrollState
                        ) {
                            if (state == BottomSheetState.USERS) {
                                membersList?.let {
                                    items(membersList) { fullUserModel ->
                                        fullUserModel?.let {
                                            MemberItem(
                                                user = it,
                                                onComplainClicked = { onComplainClicked(it) },
                                                onDeleteClicked = { onDeleteClicked(it) }
                                            )
                                        }
                                    }
                                }
                            } else {
                                messagesList?.let {
                                    items(messagesList) { messageModel ->
                                        messageModel?.let {
                                            MessageItem(
                                                messageModel = it
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (state == BottomSheetState.CHAT) {
                CommentPanel(
                    onSendMessage = onSendMessage,
                    modifier = Modifier
                        .align(
                            Alignment.BottomCenter
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPanel(
    onSendMessage: (String) -> Unit,
    modifier: Modifier
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
                .fillMaxWidth()
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
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_send_rounded),
                contentDescription = "send message",
                tint = Color.Unspecified,
                modifier = Modifier
                    .clickable {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                    .padding(4.dp)
            )
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearchValueChanged: (String) -> Unit,
    searchValue: String
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
    onDeleteClicked: () -> Unit
) {
    var expandedPopUp by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize()) {
        Row {
            GCashedImage(
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
                            painter = painterResource(id = EmojiModel.getEmoji(it.type).path.toInt()),
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
    messageModel: TranslationMessageModel
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        GCashedImage(
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
                        painter = painterResource(id = EmojiModel.getEmoji(it.type).path.toInt()),
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