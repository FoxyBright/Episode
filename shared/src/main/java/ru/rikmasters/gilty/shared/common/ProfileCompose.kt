package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.*
import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun EditProfilePreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            Profile(
                ProfileState(DemoProfileModel),
                Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            Profile(
                ProfileState(
                    DemoProfileModel,
                    profileType = ORGANIZER
                ), Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun UserProfilePreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            Profile(
                ProfileState(
                    DemoProfileModel,
                    profileType = USERPROFILE
                ), Modifier.padding(16.dp)
            )
        }
    }
}

data class ProfileState(
    val profile: ProfileModel?,
    val profileType: ProfileType = CREATE,
    var observeState: Boolean = false,
    val lockState: Boolean = false,
    val isError: Boolean = false,
    val errorText: String = "",
)

interface ProfileCallback {

    fun onBack() {}
    fun onNext() {}
    fun onDisabledButtonClick() {}
    fun profileImage() {}
    fun hiddenImages() {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
    fun onSaveUserName() {}
    fun onSaveDescription() {}
    fun onObserveChange(state: Boolean) {}
    fun onObserveClick() {}
    fun onProfileImageRefresh() {}
    fun onAlbumClick(id:Int) {}
}

@Composable
fun Profile(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null,
    onChange: ((Boolean) -> Unit)? = null,
) {
    val profile = state.profile
    val rating = profile?.rating?.average.toString()
    val hidden = profile?.hidden?.thumbnail?.url

    Column(modifier) {
        TopBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            userName = (profile?.username ?: ""),
            userAge = (profile?.age ?: -1),
            profileType = state.profileType,
            onSaveUsername = { callback?.onSaveUserName() }
        ) { callback?.onNameChange(it) }
        ErrorLabel(state.errorText)
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileImageContent(
                modifier = Modifier.weight(1f),
                image = profile?.avatar,
                type = state.profileType,
                observeState = state.observeState,
                onObserveChange = { bool ->
                    onChange?.let { it(bool) }
                },
                isError = state.isError,
                onImageRefresh = {
                    callback?.onProfileImageRefresh()
                }
            ) { callback?.profileImage() }
            Spacer(
                Modifier.width(
                    if (state.profileType == CREATE)
                        14.dp else 16.dp
                )
            )
            Column(Modifier.weight(1f)) {
                ProfileStatisticContent(
                    rating = rating,
                    observers = profile?.countWatchers ?: 0,
                    observed = profile?.countWatching ?: 0,
                    profileType = state.profileType,
                    emoji = profile?.rating?.emoji
                ) { callback?.onObserveClick() }
                Spacer(
                    Modifier.height(
                        if ((state.profileType == CREATE))
                            14.dp else 18.dp
                    )
                )
                HiddenContent(
                    image = hidden,
                    profileType = state.profileType,
                    lockState = state.lockState,
                    onImageRefresh = {
                        callback?.onProfileImageRefresh()
                    }
                ) { callback?.hiddenImages() }
            }
        }
        Spacer(Modifier.height(12.dp))

        AlbumPictures(
            listOf(
                AlbumPictureWithEmoji(
                    image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                    emoji = DemoEmojiModel
                ),
                AlbumPictureWithEmoji(
                    image = "https://flxt.tmsimg.com/assets/878203_v9_bc.jpg",
                    emoji = DemoEmojiModel
                ),
                AlbumPictureWithEmoji(
                    image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                    emoji = DemoEmojiModel
                ),
                AlbumPictureWithEmoji(
                    image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                    emoji = DemoEmojiModel
                ),
            ),
            state.profileType,
            onAlbumClick = { id ->
                callback?.onAlbumClick(id)
            }
        )

        AboutMe(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.profile?.aboutMe,
            type = state.profileType,
            callback = callback
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun AboutMe(
    modifier: Modifier = Modifier,
    text: String?,
    type: ProfileType,
    callback: ProfileCallback?,
) {
    @Composable
    fun description() = Description(
        text = text ?: "",
        profileType = type,
        modifier = modifier.padding(top = 20.dp),
        onSaveDescription = {
            callback?.onSaveDescription()
        }
    ) { callback?.onDescriptionChange(it) }

    when (type) {
        CREATE, USERPROFILE -> description()
        ORGANIZER, ANONYMOUS_ORGANIZER ->
            if (!text.isNullOrBlank())
                description()
    }
}

@Composable
private fun ErrorLabel(errorText: String) {
    Text(
        text = errorText,
        modifier = Modifier.offset(y = -(10).dp),
        color = colorScheme.primary,
        style = typography.titleSmall
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    userName: String,
    userAge: Int,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
    onSaveUsername: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var focus by remember { mutableStateOf(false) }
    if (
        profileType != ORGANIZER
        && profileType != ANONYMOUS_ORGANIZER
    ) TextField(
        value = userName,
        onValueChange = onTextChange,
        modifier = modifier
            .offset((-16).dp)
            .fillMaxWidth()
            .onFocusChanged { focus = it.isFocused },
        colors = transparentTextFieldColors(),
        textStyle = typography.headlineLarge,
        placeholder = {
            Row(Modifier, Arrangement.Center, CenterVertically) {
                Text(
                    text = stringResource(R.string.user_name),
                    modifier = Modifier.padding(end = 8.dp),
                    color = colorScheme.onTertiary,
                    style = typography.headlineLarge
                )
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 4.dp),
                    tint = colorScheme.onTertiary
                )
            }
        },
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
            onSaveUsername()
        },
        keyboardOptions = Default.copy(
            imeAction = Done,
            keyboardType = Text,
            capitalization = Sentences
        ),
        singleLine = true,
        visualTransformation = transformationOf(
            mask = CharArray(userName.length) { '#' }
                .concatToString(),
            endChar = if (userAge in 18..99 && !focus)
                ", $userAge" else ""
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Description(
    text: String,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
    onSaveDescription: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val style = if (profileType == CREATE)
        typography.headlineSmall
    else typography.bodyMedium

    Column(modifier) {
        Text(
            text = stringResource(R.string.profile_about_me),
            style = typography.labelLarge.copy(
                colorScheme.tertiary,
                if (profileType == CREATE) 17.sp else 20.sp
            )
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .background(
                    color = colorScheme.primaryContainer,
                    shape = shapes.large
                )
        ) {
            BasicTextField(
                value = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = if (profileType == CREATE)
                            12.dp else 14.dp,
                        horizontal = if (profileType == CREATE)
                            16.dp else 14.dp,
                    ),
                onValueChange = { if (it.length <= 120) onTextChange(it) },
                readOnly = when (profileType) {
                    ORGANIZER, ANONYMOUS_ORGANIZER -> true
                    else -> false
                },
                textStyle = style.copy(colorScheme.tertiary),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSaveDescription()
                }, keyboardOptions = Default.copy(
                    imeAction = Done,
                    keyboardType = Text,
                    capitalization = Sentences
                ),
                cursorBrush = SolidColor(colorScheme.primary)
            ) { innerTextField ->
                TextFieldDecorationBox(
                    value = text,
                    visualTransformation = None,
                    innerTextField = innerTextField,
                    contentPadding = PaddingValues(vertical = 2.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.about_me_placeholder),
                            color = colorScheme.onTertiary,
                            style = style.copy(colorScheme.onTertiary)
                        )
                    },
                    shape = shapes.large,
                    singleLine = true,
                    enabled = true,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }, colors = textFieldColors()
                )
            }
        }
    }
}
@Composable
fun AlbumPictures(
    picturesWithEmojis: List<AlbumPictureWithEmoji>,
    type: ProfileType,
    onAlbumClick:(Int)->Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth by remember {
        mutableStateOf(configuration.screenWidthDp)
    }

    @Composable
    fun album() {
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                Spacer(modifier = Modifier.width(16.dp))
            }
            itemsIndexed(picturesWithEmojis) { index, item ->
                AlbumPictureItem(
                    modifier = Modifier
                        .size((screenWidth / 4).dp)
                        .clip(
                            lazyRowAlbumItemsShapes(
                                index = index,
                                size = picturesWithEmojis.size
                            )
                        ),
                    albumPictureWithEmoji = item,
                    onClick = {
                        onAlbumClick(item.id)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }

    when (type) {
        CREATE, USERPROFILE -> album()
        ORGANIZER, ANONYMOUS_ORGANIZER -> {}
    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumPictureItem(
    modifier: Modifier = Modifier,
    albumPictureWithEmoji: AlbumPictureWithEmoji,
    onClick:()->Unit
) {
    Box(modifier = modifier.combinedClickable(
        onClick = onClick, // open Another screen,
        onLongClick = {
                      // Show descriotion
        },
    )) {
        GCachedImage(
            modifier = modifier,
            url = albumPictureWithEmoji.image,
            contentScale = ContentScale.Crop
        )
        EmojiAlbumFloat(
            modifier = Modifier
                .size(28.dp)
                .align(TopStart)
                .padding(start = 6.dp, top = 6.dp),
            albumPictureWithEmoji.emoji
        )
    }
}
@Composable
fun EmojiAlbumFloat(
    modifier: Modifier = Modifier,
    emoji: EmojiModel
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        GEmojiImage(modifier = Modifier.padding(4.dp), emoji = emoji)
    }

}
data class AlbumPictureWithEmoji(
    val id:Int = 1,
    val image: String,
    val emoji: EmojiModel
)