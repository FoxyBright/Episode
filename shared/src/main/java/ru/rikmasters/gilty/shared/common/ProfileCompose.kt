package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TextFieldDefaults.TextFieldDecorationBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import ru.rikmasters.gilty.shared.common.profileBadges.ProfileBadge
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.*
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
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
                state = ProfileState(DemoProfileModel),
                modifier = Modifier.padding(16.dp),
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
                    profile = DemoProfileModel,
                    profileType = ORGANIZER,
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
                    profile = DemoProfileModel,
                    profileType = USERPROFILE,
                ), modifier = Modifier.padding(16.dp)
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
    val activeAlbumId: Int? = null,
    val isAlbumVisible: Boolean = false,
)

interface ProfileCallback {
    
    fun onBack() {}
    fun onNext() {}
    fun onDisabledButtonClick() {}
    fun profileImage(menuItem: Int = 0) {}
    fun hiddenImages() {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
    fun onSaveUserName() {}
    fun onSaveDescription() {}
    fun onObserveChange(state: Boolean) {}
    fun onObserveClick() {}
    fun onProfileImageRefresh() {}
    fun onAlbumClick(id: Int) {}
    fun onAlbumLongClick(id: Int?) {}
}

@Composable
fun Profile(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null,
    hasHeader: Boolean = true,
    isMyProfile: Boolean = false,
    onChange: ((Boolean) -> Unit)? = null,
) {
    var menuState by remember {
        mutableStateOf(false)
    }
    var offset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var duplicateOpen by remember {
     mutableStateOf(true)
    }
    val context = LocalContext.current
    val profile = state.profile
    val rating = profile?.rating?.average.toString()
    val hidden = profile?.hidden?.thumbnail?.url
    Column(modifier) {
        if(hasHeader) {
            ProfileHeader(state, callback)
        }
        Row(Modifier.padding(horizontal = 16.dp)) {
            ProfileImageContent(
                modifier = Modifier.weight(1f),
                image = profile?.avatar,
                type = state.profileType,
                observeState = state.observeState,
                onObserveChange = { bool ->
                    onChange?.let { it(bool) }
                },
                isError = state.isError,
                isMyProfile = isMyProfile,
                onImageRefresh = {
                    callback?.onProfileImageRefresh()
                },
                onClick = { callback?.profileImage() },
                usProfClick = {
                    if(duplicateOpen) {
                        menuState = true
                        vibrate(context)
                        offset = it
                    }
                    duplicateOpen = true
                }
            )
            Spacer(
                Modifier.width(
                    if(state.profileType == CREATE)
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
                        if((state.profileType == CREATE)) 14.dp else 18.dp
                    )
                )
                HiddenContent(image = hidden,
                    profileType = state.profileType,
                    lockState = state.lockState,
                    onImageRefresh = {
                        callback?.onProfileImageRefresh()
                    }) { callback?.hiddenImages() }
            }
        }
        Spacer(Modifier.height(12.dp))
        if(state.isAlbumVisible) AlbumPictures(picturesWithEmojis = listOf(
            AlbumPictureWithEmoji(
                id = 1,
                image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                emoji = DemoEmojiModel
            ),
            AlbumPictureWithEmoji(
                id = 2,
                image = "https://flxt.tmsimg.com/assets/878203_v9_bc.jpg",
                emoji = DemoEmojiModel
            ),
            AlbumPictureWithEmoji(
                id = 3,
                image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                emoji = DemoEmojiModel,
                isVisible = false,
            ),
            AlbumPictureWithEmoji(
                id = 4,
                image = "https://media.npr.org/assets/img/2020/02/27/wide-use_hpromophoto_helenepambrun-72fdb64792139d94a06f18686d0bb3131a238a70-s1100-c50.jpg",
                emoji = DemoEmojiModel
            ),
        ),
            activeAlbumId = state.activeAlbumId,
            type = state.profileType,
            onAlbumClick = { id ->
                callback?.onAlbumClick(id)
            },
            onAlbumLongClick = { id ->
                callback?.onAlbumLongClick(id)
            })
        
        AboutMe(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.profile?.aboutMe,
            type = state.profileType,
            callback = callback
        )
        Spacer(Modifier.height(20.dp))
    }
    Menu(
        state = menuState,
        offset = offset,
        onDismiss = {
            menuState = false
            duplicateOpen = false
        },
        onSelect = { callback?.profileImage(it) }
    )
}

@Composable
fun ProfileHeader(state: ProfileState, callback: ProfileCallback?) {
    Column {
        TopBar(modifier = Modifier.padding(horizontal = 16.dp),
            userName = (state.profile?.username ?: ""),
            userAge = (state.profile?.age ?: -1),
            profileType = state.profileType,
            profileGroup = state.profile?.group
                ?: UserGroupTypeModel.DEFAULT,
            onSaveUsername = { callback?.onSaveUserName() }) {
            callback?.onNameChange(
                it
            )
        }
        if(state.errorText.isNotEmpty())
            ErrorLabel(state.errorText)
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
    fun description() = Description(text = text ?: "",
        profileType = type,
        modifier = modifier.padding(top = 20.dp),
        onSaveDescription = {
            callback?.onSaveDescription()
        }) { callback?.onDescriptionChange(it) }
    
    when(type) {
        CREATE, USERPROFILE -> description()
        ORGANIZER, ANONYMOUS_ORGANIZER -> if(!text.isNullOrBlank()) description()
    }
}

@Composable
private fun ErrorLabel(errorText: String) {
    Text(
        text = errorText,
        modifier = Modifier.offset(y = -(10).dp, x = 16.dp),
        color = colorScheme.primary,
        style = typography.titleSmall.copy(
            fontSize = 10.dp.toSp()
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    userName: String,
    userAge: Int,
    profileType: ProfileType,
    profileGroup: UserGroupTypeModel,
    modifier: Modifier = Modifier,
    onSaveUsername: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var focus by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    if(profileType != ORGANIZER && profileType != ANONYMOUS_ORGANIZER) Row(
        modifier = modifier
            .fillMaxWidth()
            .offset((-16).dp),
        verticalAlignment = CenterVertically,
    ) {
        BasicTextField(
            value = userName,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .onFocusChanged { focus = it.isFocused },
            onValueChange = onTextChange,
            textStyle = typography.headlineLarge,
            visualTransformation = transformationOf(
                mask = CharArray(userName.length) { '#' }.concatToString(),
                endChar = if(userAge in 18..99 && !focus) ", $userAge" else ""
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
                onSaveUsername()
            },
            keyboardOptions = Default.copy(
                imeAction = Done,
                keyboardType = Text,
                capitalization = Sentences
            ),
            interactionSource = interactionSource,
            singleLine = true,
            decorationBox = @Composable { innerTextField ->
                TextFieldDecorationBox(
                    value = userName,
                    visualTransformation = transformationOf(
                        mask = CharArray(userName.length) { '#' }.concatToString(),
                        endChar = if(userAge in 18..99 && !focus) ", $userAge" else ""
                    ),
                    innerTextField = innerTextField,
                    placeholder = {
                        Row(Modifier, Arrangement.Center, CenterVertically) {
                            Text(
                                text = stringResource(R.string.user_name),
                                modifier = Modifier.padding(end = 8.dp),
                                color = colorScheme.onTertiary,
                                style = typography.headlineLarge.copy(
                                    fontSize = when(profileType) {
                                        CREATE -> 23
                                        USERPROFILE -> 28
                                        else -> 20
                                    }.dp.toSp()
                                )
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = null,
                                modifier = Modifier.padding(top = 4.dp),
                                tint = colorScheme.onTertiary
                            )
                        }
                    },
                    singleLine = true,
                    enabled = true,
                    interactionSource = interactionSource,
                    colors = transparentTextFieldColors(),
                )
            }
        )

        ProfileBadge(
            modifier = Modifier.offset(x = (-12).dp), group = profileGroup
        )
    }
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
    val style = typography.bodyMedium.copy(
        fontSize = (if(profileType == CREATE) 12 else 16).dp.toSp()
    )
    
    Column(modifier) {
        Text(
            text = stringResource(R.string.profile_about_me),
            style = typography.labelLarge.copy(
                color = colorScheme.tertiary,
                fontSize = if(profileType == CREATE) 17.dp.toSp() else 20.dp.toSp()
            )
        )
        Box(
            modifier = Modifier
                .imePadding()
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
                        vertical = if(profileType == CREATE) 12.dp else 14.dp,
                        horizontal = if(profileType == CREATE) 16.dp else 14.dp,
                    ),
                onValueChange = { if(it.length <= 120) onTextChange(it) },
                readOnly = when(profileType) {
                    ORGANIZER, ANONYMOUS_ORGANIZER -> true
                    else -> false
                },
                textStyle = style.copy(colorScheme.tertiary),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                    onSaveDescription()
                },
                keyboardOptions = Default.copy(
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
                    },
                    colors = textFieldColors()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumPictures(
    picturesWithEmojis: List<AlbumPictureWithEmoji>,
    activeAlbumId: Int?,
    type: ProfileType,
    onAlbumClick: (Int) -> Unit,
    onAlbumLongClick: (Int?) -> Unit,
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
                if(activeAlbumId == null || activeAlbumId == item.id) AlbumPictureItem(
                    modifier = Modifier
                        .animateItemPlacement()
                        .size((screenWidth / 4).dp)
                        .clip(
                            lazyRowAlbumItemsShapes(
                                index = index,
                                size = if(activeAlbumId != null) 1 else picturesWithEmojis.size
                            )
                        ),
                    albumPictureWithEmoji = item,
                    onClick = {
                        if(activeAlbumId == null) onAlbumClick(item.id)
                        else onAlbumLongClick(null)
                    },
                    onLongClick = {
                        onAlbumLongClick(item.id)
                    })
            }
            item { Spacer(modifier = Modifier.width(16.dp)) }
            if(activeAlbumId != null) {
                item {
                    AlbumDescription(
                        modifier = Modifier
                            .size(
                                (screenWidth / 4 * 3 - (12 * 3)).dp,
                                (screenWidth / 4).dp
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        name = "Токиосити ван",
                        isVisible = false,
                        emojis = listOf(DemoEmojiModel, DemoEmojiModel)
                    )
                }
            }
        }
    }
    
    when(type) {
        CREATE, USERPROFILE -> album()
        ORGANIZER, ANONYMOUS_ORGANIZER -> {}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumPictureItem(
    modifier: Modifier = Modifier,
    albumPictureWithEmoji: AlbumPictureWithEmoji,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Box(
        modifier = modifier.combinedClickable(
            onClick = onClick, // open Another screen,
            onLongClick = onLongClick,
        )
    ) {
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
fun AlbumDescription(
    modifier: Modifier = Modifier,
    name: String,
    isVisible: Boolean,
    emojis: List<EmojiModel>,
) {
    Box(
        modifier = modifier.background(
            colorScheme.primaryContainer
        ), contentAlignment = Center
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), SpaceBetween, CenterVertically
        ) {
            Column {
                Text(text = name)
                Row(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray)
                        .then(
                            Modifier.padding(
                                start = 4.dp, top = 4.dp, bottom = 4.dp
                            )
                        )
                ) {
                    emojis.forEach {
                        GEmojiImage(
                            emoji = it, modifier = Modifier
                                .size(26.dp)
                                .padding(end = 4.dp)
                        )
                    }
                }
            }
            AlbumIcon(
                modifier = Modifier.size(52.dp),
                onClick = {},
                content = {
                    Image(
                        painter = painterResource(
                            R.drawable.ic_image_box
                        ), contentDescription = null, modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                })
            AlbumIcon(
                modifier = Modifier.size(52.dp),
                onClick = {},
                content = {
                    Image(
                        painter = painterResource(
                            if(isVisible) R.drawable.ic_open_eye
                            else R.drawable.ic_closed_eye
                        ), contentDescription = null, modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                })
        }
    }
}

@Composable
fun AlbumIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x88000000))
            .clickable { onClick() },
        contentAlignment = Center
    ) { content() }
}

@Composable
fun EmojiAlbumFloat(
    modifier: Modifier = Modifier,
    emoji: EmojiModel,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0x88000000)),
        contentAlignment = Center
    ) {
        GEmojiImage(
            modifier = Modifier.padding(4.dp), emoji = emoji
        )
    }
}

data class AlbumPictureWithEmoji(
    val id: Int = 1,
    val image: String,
    val emoji: EmojiModel,
    val isVisible: Boolean = true,
)