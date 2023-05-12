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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.transform.transformationOf
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.*
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
            userName = (profile?.username ?: ""),
            userAge = (profile?.age ?: -1),
            profileType = state.profileType,
            onSaveUsername = { callback?.onSaveUserName() }
        ) { callback?.onNameChange(it) }
        ErrorLabel(state.errorText)
        Row {
            ProfileImageContent(
                modifier = Modifier.weight(1f),
                image = profile?.avatar,
                type = state.profileType,
                observeState = state.observeState,
                onObserveChange = { bool ->
                    onChange?.let { it(bool) }
                },
                isError = state.isError
            ) { callback?.profileImage() }
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
                        if((state.profileType == CREATE))
                            14.dp else 18.dp
                    )
                )
                HiddenContent(
                    image = hidden,
                    profileType = state.profileType,
                    lockState = state.lockState
                ) { callback?.hiddenImages() }
            }
        }
        AboutMe(
            text = state.profile?.aboutMe,
            type = state.profileType,
            callback = callback
        )
    }
}

@Composable
private fun AboutMe(
    text: String?,
    type: ProfileType,
    callback: ProfileCallback?,
) {
    @Composable
    fun description() = Description(
        text = text ?: "",
        profileType = type,
        modifier = Modifier.padding(top = 20.dp),
        onSaveDescription = {
            callback?.onSaveDescription()
        }
    ) { callback?.onDescriptionChange(it) }
    
    when(type) {
        CREATE, USERPROFILE -> description()
        ORGANIZER, ANONYMOUS_ORGANIZER ->
            if(!text.isNullOrBlank())
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
    if(
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
            Row(Modifier, Center, CenterVertically) {
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
            endChar = if(userAge in 18..99 && !focus)
                ", $userAge" else ""
        )
    )
}

@Composable
private fun Description(
    text: String,
    profileType: ProfileType,
    modifier: Modifier = Modifier,
    onSaveDescription: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    val focusManager =
        LocalFocusManager.current
    
    Column(modifier) {
        Text(
            text = stringResource(R.string.profile_about_me),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        GTextField(
            value = text,
            onValueChange = { onTextChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            textOffset = true,
            readOnly = (profileType == ORGANIZER
                    || profileType == ANONYMOUS_ORGANIZER),
            shape = shapes.large, colors = textFieldColors(),
            textStyle = typography.bodyMedium,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
                onSaveDescription()
            }, keyboardOptions = Default.copy(
                imeAction = Done,
                keyboardType = Text,
                capitalization = Sentences
            ), placeholder = textFieldLabel(
                label = false,
                text = stringResource(R.string.about_me_placeholder),
                holderFont = typography.bodyMedium
                    .copy(colorScheme.onTertiary)
            )
        )
    }
}