package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_image_box
import ru.rikmasters.gilty.shared.R.drawable.ic_lock_close
import ru.rikmasters.gilty.shared.R.drawable.transparency_circle
import ru.rikmasters.gilty.shared.R.string.profile_observe
import ru.rikmasters.gilty.shared.R.string.profile_observers_in_profile
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.CREATE
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.USERPROFILE
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.ObserveCheckBox
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun HiddenPhotoContentPreview() {
    GiltyTheme {
        HiddenPhotoContent(
            Modifier.width(160.dp),
            (null), CREATE
        ) {}
    }
}

@Preview
@Composable
private fun ProfileImageContentPreview() {
    GiltyTheme {
        ProfileImageContent(
            Modifier.width(160.dp),
            (""), ORGANIZER, (false),
            {}) {}
    }
}

@Preview
@Composable
private fun ProfileStatisticContentPreview() {
    GiltyTheme {
        ProfileStatisticContent(
            Modifier.width(160.dp),
            DemoProfileModel.rating.average,
            (100), (100), DemoProfileModel.emoji
        )
    }
}

@Composable
fun HiddenPhotoContent(
    modifier: Modifier,
    image: String?,
    profileType: ProfileType,
    onCardClick: () -> Unit,
) {
    Box(
        modifier
            .height(118.dp)
            .fillMaxWidth()
            .clip(shapes.large)
            .background(colorScheme.primaryContainer)
            .clickable { onCardClick() }, BottomCenter
    ) {
        AsyncImage(
            image, stringResource(R.string.profile_hidden_photo),
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Box(
            Modifier
                .padding(start = 8.dp, top = 8.dp)
                .size(26.dp)
                .clip(CircleShape)
                .align(TopStart),
        ) {
            if(profileType != USERPROFILE) {
                Image(
                    painterResource(transparency_circle),
                    (null), Modifier.fillMaxSize()
                )
                Icon(
                    painterResource(ic_lock_close),
                    (null), Modifier
                        .padding(4.dp)
                        .size(24.dp),
                    colorScheme.tertiary
                )
            }
        }
        if(profileType == ORGANIZER)
            CreateProfileCardRow(
                stringResource(R.string.profile_hidden_photo),
                USERPROFILE
            )
        else CreateProfileCardRow(
            stringResource(R.string.profile_hidden_photo),
            profileType
        )
    }
}

@Composable
fun ProfileImageContent(
    modifier: Modifier,
    image: String,
    profileType: ProfileType,
    observeState: Boolean,
    onObserveChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier
            .height(254.dp)
            .fillMaxWidth(0.45f)
            .clip(shapes.large)
            .background(colorScheme.primaryContainer)
            .clickable { onClick() }, BottomCenter
    ) {
        AsyncImage(
            image,
            stringResource(R.string.meeting_avatar),
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        when(profileType) {
            CREATE -> {
                CreateProfileCardRow(
                    stringResource(R.string.profile_user_photo),
                    profileType
                )
            }
            
            ORGANIZER -> {
                CreateProfileCardRow(
                    stringResource(R.string.profile_organizer_observe),
                    profileType,
                    observeState,
                ) { onObserveChange(it) }
            }
            
            USERPROFILE -> {}
        }
    }
}

@Composable
private fun CreateProfileCardRow(
    text: String,
    profileType: ProfileType,
    observeState: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null
) {
    Row(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        Arrangement.Center, CenterVertically
    ) {
        if(profileType != ORGANIZER)
            Text(
                text, Modifier.padding(end = 4.dp),
                colorScheme.onTertiary,
                style = typography.headlineSmall,
                fontWeight = SemiBold
            )
        when(profileType) {
            CREATE -> {
                Box(
                    Modifier
                        .size(26.dp)
                        .background(
                            colorScheme.primary,
                            CircleShape
                        )
                ) {
                    Image(
                        painterResource(ic_image_box),
                        null,
                        Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }
            
            ORGANIZER -> ObserveCheckBox(observeState)
            { bool -> onClick?.let { it(bool) } }
            
            USERPROFILE -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticContent(
    modifier: Modifier,
    rating: String,
    observers: Int,
    observed: Int,
    emoji: EmojiModel? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier
            .height(118.dp)
            .fillMaxWidth()
            .clip(shapes.large), (true),
        shapes.large, cardColors(colorScheme.primaryContainer)
    ) {
        Column {
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                Start, CenterVertically
            ) {
                Text(
                    rating, Modifier.weight(1f),
                    style = ThemeExtra.typography.RatingText,
                    textAlign = Right
                )
                Cloud(Modifier.weight(1f), emoji)
            }
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp, top = 4.dp)
            ) {
                Observe(
                    Modifier.weight(1f),
                    stringResource(profile_observers_in_profile),
                    observers
                )
                Observe(
                    Modifier.weight(1f),
                    stringResource(profile_observe),
                    observed
                )
            }
        }
    }
}

@Composable
private fun Observe(
    modifier: Modifier = Modifier,
    text: String, count: Int
) {
    Column(
        modifier, Top, CenterHorizontally
    ) {
        Text(
            digitalConverter(count),
            Modifier, colorScheme.tertiary,
            style = typography.labelSmall,
            fontWeight = SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text, Modifier,
            colorScheme.tertiary,
            style = typography.titleSmall,
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
private fun Cloud(modifier: Modifier, emoji: EmojiModel?) {
    Row(modifier, Start, Bottom) {
        MiniCloud(Modifier.size(6.dp))
        MiniCloud(
            Modifier
                .padding(bottom = 4.dp)
                .padding(horizontal = 2.dp)
                .size(10.dp)
        )
        MiniCloud(
            Modifier.padding(bottom = 2.dp),
        ) {
            emoji?.let {
                GEmojiImage(
                    it, Modifier
                        .padding(10.dp, 8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniCloud(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null
) = Box(
    modifier.background(
        ThemeExtra.colors.chipGray, CircleShape
    )
) { content?.invoke() }
