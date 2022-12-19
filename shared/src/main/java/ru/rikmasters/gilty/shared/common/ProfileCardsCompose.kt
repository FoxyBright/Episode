package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.ObserveCheckBox
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun HiddenPhotoContentPreview() {
    GiltyTheme {
        var lookState by remember { mutableStateOf(true) }
        HiddenPhotoContent(Modifier.width(160.dp), lookState, null, ProfileType.CREATE, {}) {
            lookState = !lookState
        }
    }
}

@Preview
@Composable
private fun ProfileImageContentPreview() {
    GiltyTheme {
        val observeState = remember { mutableStateOf(false) }
        ProfileImageContent(
            Modifier.width(160.dp),
            "",
            ProfileType.ORGANIZER,
            false,
            { observeState.value = it }) {}
    }
}

@Preview
@Composable
private fun ProfileStatisticContentPreview() {
    GiltyTheme {
        ProfileStatisticContent(
            Modifier.width(160.dp),
            DemoProfileModel.rating.average, 100, 100, DemoProfileModel.emoji
        )
    }
}

@Composable
fun HiddenPhotoContent(
    modifier: Modifier,
    lockState: Boolean,
    image: String?,
    profileType: ProfileType,
    onCardClick: () -> Unit,
    onLockClick: (Boolean) -> Unit
) {
    Box(
        modifier
            .height(100.dp)
            .fillMaxWidth()
            .clip(shapes.large)
            .background(colorScheme.primaryContainer)
            .clickable { onCardClick() }, Alignment.BottomCenter
    ) {
        AsyncImage(
            image,
            stringResource(R.string.profile_hidden_photo),
            Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.ic_image_empty),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier
                .padding(start = 8.dp, top = 8.dp)
                .size(26.dp)
                .clip(CircleShape)
                .align(Alignment.TopStart),
        ) {
            Image(painterResource(R.drawable.transparency_circle), (null), Modifier.fillMaxSize())
            CheckBox(
                lockState,
                Modifier
                    .padding(4.dp)
                    .size(24.dp), listOf(
                    R.drawable.ic_lock_open,
                    R.drawable.ic_lock_close
                ), colorScheme.tertiary
            ) { onLockClick(it) }
        }
        if (profileType == ProfileType.ORGANIZER)
            CreateProfileCardRow(
                stringResource(R.string.profile_hidden_photo),
                ProfileType.USERPROFILE
            )
        else CreateProfileCardRow(stringResource(R.string.profile_hidden_photo), profileType)
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
            .height(214.dp)
            .fillMaxWidth(0.45f)
            .clip(shapes.large)
            .background(colorScheme.primaryContainer)
            .clickable { onClick() }, Alignment.BottomCenter
    ) {
        AsyncImage(
            image,
            stringResource(R.string.meeting_avatar),
            Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.ic_image_empty),
            contentScale = ContentScale.Crop
        )
        when (profileType) {
            ProfileType.CREATE -> {
                CreateProfileCardRow(stringResource(R.string.profile_user_photo), profileType)
            }

            ProfileType.ORGANIZER -> {
                CreateProfileCardRow(
                    stringResource(R.string.profile_organizer_observe),
                    profileType,
                    observeState,
                ) { onObserveChange(it) }
            }

            ProfileType.USERPROFILE -> {}
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
        Arrangement.Center, Alignment.CenterVertically
    ) {
        if (profileType != ProfileType.ORGANIZER)
            Text(
                text,
                Modifier.padding(end = 4.dp),
                colorScheme.onTertiary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        when (profileType) {
            ProfileType.CREATE -> {
                Box(
                    Modifier
                        .size(26.dp)
                        .background(colorScheme.primary, CircleShape)
                ) {
                    Image(
                        painterResource(R.drawable.ic_image_box),
                        null,
                        Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }

            ProfileType.ORGANIZER -> ObserveCheckBox(observeState) { bool -> onClick?.let { it(bool) } }

            ProfileType.USERPROFILE -> {}
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
            .height(100.dp)
            .fillMaxWidth()
            .clip(shapes.large), (true),
        shapes.large, cardColors(colorScheme.primaryContainer)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .wrapContentHeight()
            ) {
                Text(rating, style = ThemeExtra.typography.RatingText)
                Box(Modifier.padding(top = 14.dp)) {
                    Image(painterResource(R.drawable.ic_emoji), null)
                    Image(
                        if (emoji?.type == "D") painterResource(emoji.path.toInt())
                        else rememberAsyncImagePainter(emoji?.path), (null),
                        Modifier
                            .padding(top = 5.dp, end = 6.dp)
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        digitalConverter(observers),
                        Modifier.fillMaxWidth(),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.profile_observers),
                        Modifier.fillMaxWidth(),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        digitalConverter(observed),
                        Modifier.fillMaxWidth(),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        stringResource(R.string.profile_observe),
                        Modifier.fillMaxWidth(),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun digitalConverter(digit: Int): String {
    val number = "$digit"
    val firstChar: String
    val count: String
    return when {
        number.length > 9 -> ">999 m"
        number.length in 4..9 -> {
            when (number.length) {
                5, 8 -> {
                    firstChar = number.substring(0..1)
                    count = "${number[2]}"
                }

                6, 9 -> {
                    firstChar = number.substring(0..2)
                    count = "${number[3]}"
                }

                else -> {
                    firstChar = "${number[0]}"
                    count = "${number[1]}"
                }
            }
            "$firstChar,$count ${if (number.length in 3..6) "k" else "m"}"
        }

        else -> number
    }
}