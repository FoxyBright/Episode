package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.*
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun HiddenPhotoContentPreview() {
    GiltyTheme {
        HiddenContent(
            modifier = Modifier.width(160.dp),
            image = null,
            profileType = CREATE,
            lockState = false
        ) {}
    }
}

@Preview
@Composable
private fun ProfileImageContentPreview() {
    GiltyTheme {
        ProfileImageContent(
            modifier = Modifier.width(160.dp),
            image = DemoAvatarModel,
            type = ORGANIZER,
            observeState = false,
            onObserveChange = {},
            isMyProfile = false
        ) {}
    }
}

@Preview
@Composable
private fun ProfileStatisticContentPreview() {
    GiltyTheme {
        Column {
            ProfileStatisticContent(
                modifier = Modifier
                    .padding(12.dp)
                    .width(160.dp),
                rating = DemoProfileModel
                    .rating.average,
                observers = 100,
                observed = 100,
                profileType = USERPROFILE,
                emoji = DemoProfileModel
                    .rating.emoji
            )
            ProfileStatisticContent(
                modifier = Modifier
                    .padding(12.dp)
                    .width(160.dp),
                rating = "0.0",
                observers = 0,
                observed = 0,
                profileType = CREATE
            )
        }
    }
}

@Composable
fun HiddenContent(
    modifier: Modifier = Modifier,
    image: String?,
    profileType: ProfileType,
    lockState: Boolean,
    onImageRefresh: () -> Unit = {},
    onCardClick: () -> Unit,
) {
    Box(
        modifier
            .height(
                if(profileType == CREATE)
                    93.dp else 118.dp
            )
            .fillMaxWidth()
            .clip(shapes.large)
            .background(colorScheme.primaryContainer)
            .clickable { onCardClick() },
        BottomStart
    ) {
        GCachedImage(
            url = image,
            modifier = Modifier.fillMaxSize(),
            contentScale = Crop
        ) { onImageRefresh() }
        if(profileType != USERPROFILE) Lock(
            modifier = Modifier
                .align(TopStart)
                .padding(8.dp),
            state = if(profileType == CREATE)
                true else lockState
        )
        val emptyImage = image.isNullOrBlank()
                || image.contains("null")
        CreateProfileCardRow(
            text = stringResource(R.string.profile_hidden_photo),
            profileType = when(profileType) {
                ORGANIZER, ANONYMOUS_ORGANIZER -> USERPROFILE
                USERPROFILE -> if(emptyImage)
                    CREATE else USERPROFILE
                CREATE -> CREATE
            }
        )
    }
}

@Composable
private fun Lock(
    modifier: Modifier = Modifier,
    state: Boolean,
) {
    Box(
        modifier.background(
            Color(0x4D000000),
            CircleShape
        )
    ) {
        Icon(
            painter = painterResource(
                if(state)
                    R.drawable.ic_lock_open
                else
                    R.drawable.ic_lock_close
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(16.dp),
            tint = White
        )
    }
}

@Composable
fun Menu(
    state: Boolean,
    offset: Offset,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    GPopUpMenu(
        menuState = state,
        collapse = onDismiss,
        items = listOf(
            Triple(
                stringResource(R.string.profile_menu_watch_photo_button),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(0) },
            Triple(
                stringResource(R.string.edit_button),
                colorScheme.tertiary
            ) { onDismiss(); onSelect(1) }
        ),
        modifier = Modifier.offset(
            (offset.x / density).dp,
            (offset.y / density).dp
        )
    )
}

@Composable
fun ProfileImageContent(
    modifier: Modifier,
    image: AvatarModel?,
    type: ProfileType,
    observeState: Boolean,
    onObserveChange: (Boolean) -> Unit,
    isError: Boolean = false,
    isMyProfile: Boolean,
    onImageRefresh: () -> Unit = {},
    onClick: () -> Unit = {},
    usProfClick: (Offset) -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(
                if(type == CREATE)
                    200.dp else 254.dp
            )
            .fillMaxWidth(0.45f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if(type != USERPROFILE)
                            onClick()
                        else usProfClick(it)
                    },
                )
            },
        shape = shapes.large,
        colors = cardColors(
            containerColor = colorScheme
                .primaryContainer
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if(isError)
                colorScheme.primary
            else colorScheme.primaryContainer
        )
    ) {
        Column {
            Box {
                Avatar(
                    image = image,
                    type = type,
                    modifier = Modifier,
                    onImageRefresh = onImageRefresh
                )
                when(type) {
                    USERPROFILE, ANONYMOUS_ORGANIZER -> Unit
                    
                    CREATE -> {
                        CreateProfileCardRow(
                            text = stringResource(R.string.profile_user_photo),
                            modifier = Modifier.align(BottomCenter),
                            isError = isError,
                            profileType = type
                        )
                    }
                    
                    ORGANIZER -> {
                        if(!isMyProfile) CreateProfileCardRow(
                            text = stringResource(R.string.profile_organizer_observe),
                            modifier = Modifier.align(BottomCenter),
                            profileType = type,
                            observeState = observeState,
                        ) { onObserveChange(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Avatar(
    image: AvatarModel?,
    type: ProfileType,
    modifier: Modifier = Modifier,
    onImageRefresh: () -> Unit,
) {
    Box(modifier) {
        GCachedImage(
            url = image?.thumbnail?.url,
            modifier = Modifier.fillMaxSize(),
            contentScale = Crop,
            placeholderColor = colorScheme
                .primaryContainer
        ) { onImageRefresh() }
        image?.blockedAt?.let {
            if(type == USERPROFILE) Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        R.drawable.ic_information
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(
                        R.string.profile_blocked_photo
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp),
                    style = typography
                        .bodyMedium.copy(
                            color = White,
                            fontSize = 16.dp.toSp()
                        ),
                )
            }
        }
    }
}

@Composable
private fun CreateProfileCardRow(
    text: String,
    modifier: Modifier = Modifier,
    profileType: ProfileType,
    observeState: Boolean = false,
    isError: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null,
) {
    Row(
        modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        Start, CenterVertically
    ) {
        if(profileType != ORGANIZER)
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                style = typography.bodyMedium.copy(
                    color = when {
                        isError -> colorScheme.primary
                        profileType == CREATE ->
                            colorScheme.onTertiary
                        else -> White
                    },
                    fontSize = (if(profileType == CREATE)
                        12 else 16).dp.toSp(),
                    fontWeight = SemiBold,
                    textAlign = TextAlign.Start
                )
            )
        when(profileType) {
            CREATE -> {
                Box(
                    Modifier.background(
                        color = colorScheme.primary,
                        shape = CircleShape
                    )
                ) {
                    Image(
                        painter = painterResource(
                            R.drawable.ic_image_box
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(4.dp)
                    )
                }
            }
            
            ORGANIZER -> ObserveCheckBox(observeState)
            { bool -> onClick?.let { it(bool) } }
            
            USERPROFILE, ANONYMOUS_ORGANIZER -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticContent(
    modifier: Modifier = Modifier,
    rating: String,
    observers: Int,
    observed: Int,
    profileType: ProfileType,
    emoji: EmojiModel? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(
        onClick = { onClick?.let { it() } },
        modifier = modifier
            .height(
                if(profileType == CREATE)
                    93.dp else 118.dp
            )
            .fillMaxWidth()
            .clip(shapes.large),
        enabled = true,
        shape = shapes.large,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Column(
            Modifier, Top,
            CenterHorizontally
        ) {
            Row(
                Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = 8.dp),
                Start, CenterVertically
            ) {
                RatingText(
                    text = rating,
                    profileType = profileType
                )
                Cloud(
                    profileType = profileType,
                    modifier = Modifier,
                    emoji = emoji
                )
            }
            Row(
                Modifier
                    .padding(
                        horizontal = if(profileType == CREATE)
                            14.dp else 16.dp
                    )
                    .padding(bottom = 8.dp, top = 4.dp)
            ) {
                Observe(
                    modifier = Modifier.weight(1f),
                    profileType = profileType,
                    text = stringResource(R.string.profile_observers_in_profile),
                    count = observers
                )
                Observe(
                    modifier = Modifier.weight(1f),
                    profileType = profileType,
                    text = if(profileType == USERPROFILE || profileType == CREATE) stringResource(R.string.profile_user_observe) else stringResource(R.string.profile_observe),
                    count = observed
                )
            }
        }
    }
}

@Composable
private fun RatingText(
    text: String,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
) {
    val style = ThemeExtra
        .typography.RatingText.copy(
            fontSize = if(profileType == CREATE)
                38.dp.toSp()
            else 42.dp.toSp()
        )
    val string = text.ifBlank { "0.0" }
    Text(
        text = buildAnnotatedString {
            withStyle(style.toSpanStyle()) {
                append(string.first())
            }
            withStyle(
                style.copy(
                    fontSize = if(profileType == CREATE)
                        35.dp.toSp() else 45.dp.toSp()
                ).toSpanStyle()
            ) { append('.') }
            withStyle(style.toSpanStyle()) {
                append(string.last())
            }
        },
        modifier = modifier,
        textAlign = Right,
        style = style,
        maxLines = 1
    )
}


@Composable
private fun Observe(
    modifier: Modifier = Modifier,
    profileType: ProfileType,
    text: String, count: Int,
) {
    Column(
        modifier, Top,
        CenterHorizontally
    ) {
        Text(
            text = digitalConverter(count),
            style = typography.headlineSmall.copy(
                color = colorScheme.tertiary,
                fontSize = if(profileType == CREATE)
                    12.dp.toSp() else 14.dp.toSp(),
                fontWeight = SemiBold,
                textAlign = TextAlign.Center,
            ),
        )
        Text(
            text = text,
            style = typography.displaySmall.copy(
                color = colorScheme.tertiary,
                fontSize = if(profileType == CREATE)
                    8.dp.toSp() else 10.dp.toSp(),
                textAlign = TextAlign.Center
            )
        )
    }
}

fun digitalConverter(digit: Int): String {
    val number = "$digit"
    val firstChar: String
    val count: String
    return when {
        number.length > 9 -> ">999 m"
        number.length in 4..9 -> {
            when(number.length) {
                5, 8 -> {
                    firstChar = number
                        .substring(0..1)
                    count = "${number[2]}"
                }
                
                6, 9 -> {
                    firstChar = number
                        .substring(0..2)
                    count = "${number[3]}"
                }
                
                else -> {
                    firstChar = "${number[0]}"
                    count = "${number[1]}"
                }
            }
            "$firstChar,$count ${
                if(number.length
                    in 3..6
                ) "k" else "m"
            }"
        }
        
        else -> number
    }
}

@Composable
private fun Cloud(
    profileType: ProfileType,
    modifier: Modifier,
    emoji: EmojiModel?,
) {
    val create = profileType == CREATE
    Row(modifier, Start, Bottom) {
        MiniCloud(Modifier.size(if(create) 4.dp else 6.dp))
        MiniCloud(
            Modifier
                .padding(bottom = 4.dp)
                .padding(horizontal = 2.dp)
                .size(if(create) 8.dp else 10.dp)
        )
        MiniCloud(Modifier.padding(bottom = 2.dp)) {
            val paddings: Pair<Dp, Dp>
            val size: Dp
            if(create) {
                paddings = Pair(8.dp, 6.dp)
                size = 18.dp
            } else {
                paddings = Pair(10.dp, 8.dp)
                size = 24.dp
            }
            emoji?.let {
                GEmojiImage(
                    emoji = it,
                    modifier = Modifier
                        .padding(
                            paddings.first,
                            paddings.second
                        )
                        .size(size)
                )
            } ?: Box(
                Modifier
                    .padding(
                        paddings.first,
                        paddings.second
                    )
                    .size(size)
            )
        }
    }
}

@Composable
private fun MiniCloud(
    modifier: Modifier = Modifier,
    content: @Composable
    (() -> Unit)? = null,
) = Box(
    modifier.background(
        color = colors.grayButton,
        shape = CircleShape
    )
) { content?.invoke() }
