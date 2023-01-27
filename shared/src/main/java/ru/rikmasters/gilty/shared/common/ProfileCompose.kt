package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.textFieldColors
import ru.rikmasters.gilty.shared.shared.transparentTextFieldColors
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
                )
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
                )
            )
        }
    }
}

data class ProfileState(
    val profile: ProfileModel?,
    val profileType: ProfileType = CREATE,
    var observeState: Boolean = false,
    val occupiedName: Boolean = false,
)

interface ProfileCallback {
    
    fun onBack() {}
    fun onNext() {}
    fun profileImage() {}
    fun hiddenImages() {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
    fun onObserveChange(state: Boolean) {}
    fun onObserveClick() {}
}

@Composable
fun Profile(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null,
    onChange: ((Boolean) -> Unit)? = null,
) {
    Column(modifier) {
        TopBar(
            (state.profile?.username ?: ""),
            state.profileType, Modifier,
            { callback?.onBack() }
        ) { callback?.onNameChange(it) }
        if(state.occupiedName)
            Text(
                stringResource(R.string.profile_user_name_is_occupied),
                Modifier
                    .padding(bottom = 6.dp)
                    .offset(y = -(10).dp),
                colorScheme.primary,
                style = typography.titleSmall
            )
        Row {
            ProfileImageContent(
                Modifier.weight(1f),
                (state.profile?.avatar?.thumbnail?.url.toString()),
                state.profileType,
                state.observeState,
                { bool -> onChange?.let { it(bool) } },
                { callback?.profileImage() })
            Spacer(
                Modifier.width(
                    if(state.profileType == CREATE)
                        14.dp else 16.dp
                )
            )
            Column(Modifier.weight(1f)) {
                ProfileStatisticContent(
                    Modifier,
                    (state.profile?.rating?.average.toString()),
                    state.profile?.countWatchers ?: 0,
                    state.profile?.countWatching ?: 0,
                    state.profileType,
                    state.profile?.rating?.emoji
                ) { callback?.onObserveClick() }
                Spacer(
                    Modifier.height(
                        if(state.profileType == CREATE)
                            14.dp else 18.dp
                    )
                )
                HiddenContent(
                    Modifier,
                    state.profile?.hidden?.thumbnail?.url,
                    state.profileType
                ) { callback?.hiddenImages() }
            }
        }
        when(state.profileType) {
            CREATE, USERPROFILE -> Descript(state, callback)
            ORGANIZER, ANONYMOUS_ORGANIZER ->
                if((state.profile?.aboutMe ?: "").isNotBlank())
                    Descript(state, callback)
        }
    }
}

@Composable
private fun Descript(
    state: ProfileState,
    callback: ProfileCallback?,
) {
    Description(
        (state.profile?.aboutMe ?: ""),
        state.profileType, Modifier.padding(top = 20.dp)
    ) { callback?.onDescriptionChange(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    text: String,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    Row(modifier) {
        if(profileType == ORGANIZER
            || profileType == ANONYMOUS_ORGANIZER
        ) IconButton(
            onBack, Modifier.padding(
                top = 6.dp, end = 16.dp
            )
        ) {
            Icon(
                painterResource(R.drawable.ic_back),
                stringResource(R.string.action_bar_button_back),
                Modifier, colorScheme.tertiary
            )
        }
        TextField(
            text, onTextChange,
            Modifier
                .offset((-16).dp)
                .fillMaxWidth(),
            colors = transparentTextFieldColors(),
            textStyle = typography.headlineLarge,
            placeholder = {
                Row(Modifier, Center, CenterVertically) {
                    Text(
                        stringResource(R.string.user_name),
                        Modifier.padding(end = 8.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineLarge
                    )
                    Icon(
                        painterResource(R.drawable.ic_edit),
                        (null), Modifier.padding(top = 4.dp),
                        colorScheme.onTertiary
                    )
                }
            },
            readOnly = profileType == ORGANIZER
                    || profileType == ANONYMOUS_ORGANIZER,
            singleLine = true,
        )
    }
}

@Composable
private fun Description(
    text: String,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
) {
    val focusManager =
        LocalFocusManager.current
    Column(modifier) {
        Text(
            stringResource(R.string.profile_about_me),
            Modifier, colorScheme.tertiary,
            style = typography.labelLarge
        )
        GTextField(
            text, { onTextChange(it) },
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            readOnly = (profileType == ORGANIZER
                    || profileType == ANONYMOUS_ORGANIZER),
            shape = shapes.large, colors = textFieldColors(),
            textStyle = typography.bodyMedium,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            }, keyboardOptions = Default.copy(
                imeAction = Done, keyboardType = Text
            ), placeholder = {
                Text(
                    stringResource(R.string.about_me_placeholder),
                    color = colorScheme.onTertiary,
                    style = typography.bodyMedium
                )
            }
        )
    }
}