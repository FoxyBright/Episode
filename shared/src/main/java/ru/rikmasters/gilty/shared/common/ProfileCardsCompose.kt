package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.*
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalDensity
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
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.ObserveCheckBox
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun HiddenPhotoContentPreview() {
    GiltyTheme {
        HiddenContent(
            Modifier.width(160.dp),
            (null), CREATE, (false)
        ) {}
    }
}

@Preview
@Composable
private fun ProfileImageContentPreview() {
    GiltyTheme {
        ProfileImageContent(
            Modifier.width(160.dp),
            DemoAvatarModel, ORGANIZER,
            (false),
            {}) {}
    }
}

@Preview
@Composable
private fun ProfileStatisticContentPreview() {
    GiltyTheme {
        Column {
            ProfileStatisticContent(
                Modifier
                    .padding(12.dp)
                    .width(160.dp),
                DemoProfileModel.rating.average,
                (100), (100), USERPROFILE,
                DemoProfileModel.rating.emoji
            )
            ProfileStatisticContent(
                Modifier
                    .padding(12.dp)
                    .width(160.dp),
                ("0.0"), (0), (0), CREATE
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
        BottomCenter
    ) {
        GCachedImage(
            url = image,
            modifier = Modifier.fillMaxSize(),
            contentScale = Crop
        )
        if(profileType != USERPROFILE) Lock(
            modifier = Modifier
                .align(TopStart)
                .padding(8.dp),
            state = lockState
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
            painterResource(
                if(state) R.drawable.ic_lock_open
                else R.drawable.ic_lock_close
            ), (null), Modifier
                .padding(8.dp)
                .size(16.dp),
            White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageContent(
    modifier: Modifier,
    image: AvatarModel?,
    type: ProfileType,
    observeState: Boolean,
    onObserveChange: (Boolean) -> Unit,
    isError: Boolean = false,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(
                if(type == CREATE)
                    200.dp else 254.dp
            )
            .fillMaxWidth(0.45f),
        enabled = true,
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
                Avatar(image, type)
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
                        CreateProfileCardRow(
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
) {
    Box(modifier) {
        GCachedImage(
            image?.thumbnail?.url,
            Modifier.fillMaxSize(),
            contentScale = Crop,
            placeholderColor = colorScheme.primaryContainer
        )
        image?.blockedAt?.let {
            if(type == USERPROFILE) Column(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                Arrangement.Center,
                CenterHorizontally
            ) {
                Image(
                    painterResource(R.drawable.ic_information),
                    (null), Modifier.size(40.dp)
                )
                Text(
                    stringResource(R.string.profile_blocked_photo),
                    Modifier.padding(top = 16.dp),
                    style = typography.bodyMedium,
                    color = White
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
        Arrangement.Center, CenterVertically
    ) {
        if(profileType != ORGANIZER)
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                color = when {
                    isError -> colorScheme.primary
                    profileType == CREATE -> colorScheme.onTertiary
                    else -> White
                },
                style = if(profileType == CREATE)
                    typography.headlineSmall
                else typography.bodyMedium,
                fontWeight = SemiBold,
                textAlign = TextAlign.Center
            )
        when(profileType) {
            CREATE -> {
                Box(
                    Modifier.background(
                        colorScheme.primary,
                        CircleShape
                    )
                ) {
                    Image(
                        painterResource(R.drawable.ic_image_box),
                        (null), Modifier
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
        { onClick?.let { it() } },
        modifier
            .height(
                if(profileType == CREATE)
                    93.dp else 118.dp
            )
            .fillMaxWidth()
            .clip(shapes.large), (true),
        shapes.large, cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier, Top, CenterHorizontally) {
            Row(
                Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = 8.dp),
                Start, CenterVertically
            ) {
                RatingText(rating, profileType)
                Cloud(profileType, Modifier, emoji)
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
                    Modifier.weight(1f),
                    profileType,
                    stringResource(R.string.profile_observers_in_profile),
                    observers
                )
                Observe(
                    Modifier.weight(1f),
                    profileType,
                    stringResource(R.string.profile_user_observe),
                    observed
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
        buildAnnotatedString {
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
        }, modifier, textAlign = Right,
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
    val style = with(LocalDensity.current) {
        typography.headlineSmall.copy(
            fontSize = if(profileType == CREATE)
                12.dp.toSp()
            else 14.dp.toSp()
        ) to typography.displaySmall.copy(
            fontSize = if(profileType == CREATE)
                8.dp.toSp()
            else 10.dp.toSp()
        )
    }
    
    Column(
        modifier, Top, CenterHorizontally
    ) {
        Text(
            digitalConverter(count),
            Modifier, colorScheme.tertiary,
            style = style.first,
            fontWeight = SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text, Modifier,
            colorScheme.tertiary,
            style = style.second,
            textAlign = TextAlign.Center
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
        MiniCloud(
            Modifier.padding(bottom = 2.dp),
        ) {
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
                    it, Modifier
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
        colors.chipGray,
        CircleShape
    )
) { content?.invoke() }
