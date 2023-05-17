package ru.rikmasters.gilty.translation.presentation.ui.content

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.common.extentions.simpleVerticalScrollbar
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun UsersBottomSheetContent(
    configuration: Configuration,
    membersCount: Int,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    membersList: LazyPagingItems<FullUserModel>?,
    onComplainClicked: (FullUserModel) -> Unit,
    onDeleteClicked: (FullUserModel) -> Unit,
) {
    val scrollState = rememberLazyListState()
    Surface(
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        ),
        color = ThemeExtra.colors.blackSeventy
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = (configuration.screenHeightDp * 0.66).dp,
                    max = (configuration.screenHeightDp * 0.33).dp
                )
                .wrapContentWidth(unbounded = false)
                .wrapContentHeight(unbounded = true)
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
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.simpleVerticalScrollbar(
                    state = scrollState,
                    width = 3.dp
                )
            ) {
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
                LazyColumn(
                    state = scrollState
                ) {
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
                }
            }
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
                vertical = 10.dp,
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
                    containerColor = Color.Transparent
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