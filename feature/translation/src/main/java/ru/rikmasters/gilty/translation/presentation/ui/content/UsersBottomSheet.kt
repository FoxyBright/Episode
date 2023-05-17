package ru.rikmasters.gilty.translation.presentation.ui.content

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun UsersBottomSheetContent(
    configuration: Configuration,
    membersCount: Int,
    onSearch: (String) -> Unit,
    users: LazyPagingItems<FullUserModel>,
    onMoreClicked: (FullUserModel) -> Unit
) {
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
                .padding(start = 16.dp)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.translations_members),
                    style = ThemeExtra.typography.TranslationTitlePreview,
                    color = ThemeExtra.colors.white
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.translations_members),
                    style = ThemeExtra.typography.TranslationTitlePreview,
                    color = ThemeExtra.colors.mainNightGreen
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            SearchBar(onSearch = onSearch)
            Spacer(modifier = Modifier.height(28.dp))
            LazyColumn {
                items(users) { fullUserModel ->
                    fullUserModel?.let {
                        MemberItem(user = fullUserModel, onMoreClicked = onMoreClicked)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf("") }
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
                contentDescription = "search"
            )
            Spacer(modifier = Modifier.width(24.dp))
            TextField(
                value = textFieldValue,
                onValueChange = {
                    onSearch(it)
                    textFieldValue = it
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ThemeExtra.colors.zirkon,
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun MemberItem(
    user: FullUserModel,
    onMoreClicked: (FullUserModel) -> Unit
) {
    Row {
        GCashedImage(
            url = user.avatar?.thumbnail?.url,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${user.username}, ${user.age}",
                    color = ThemeExtra.colors.white,
                    style = ThemeExtra.typography.TranslationSmallButton
                )
                Spacer(modifier = Modifier.width(6.dp))
                user.emoji?.let {
                    Image(
                        painter = painterResource(id = EmojiModel.getEmoji(it.type).path.toInt()),
                        contentDescription = "Emoji"
                    )
                }
                IconButton(onClick = {onMoreClicked(user)}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_kebab),
                        contentDescription = "More"
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
            }
            Divider()
        }
    }
}