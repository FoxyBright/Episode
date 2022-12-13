package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TransparentTextFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class ProfileState(
    val name: String = "",
    val hiddenPhoto: String? = null,
    val profilePhoto: String? = null,
    val description: String = "",
    val rating: String = "0",
    val observers: Int = 0,
    val observed: Int = 0,
    val emoji: EmojiModel? = null,
    val profileType: ProfileType = ProfileType.CREATE,
    var observeState: Boolean = false,
    val enabled: Boolean = true,
    val occupiedName: Boolean = false
)

interface ProfileCallback: NavigationInterface {
    
    fun profileImage() {}
    fun hiddenImages() {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
    fun onObserveChange(state: Boolean) {}
    fun onObserveClick() {}
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun EditProfilePreview() {
    GiltyTheme { Profile(ProfileState()) }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        val user = DemoProfileModel
        Profile(
            ProfileState(
                "${user.username}, ${user.age}",
                description = user.aboutMe,
                enabled = false,
                profileType = ProfileType.ORGANIZER
            )
        )
    }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun UserProfilePreview() {
    GiltyTheme {
        val user = DemoProfileModel
        Profile(
            ProfileState(
                "${user.username}, ${user.age}",
                description = user.aboutMe,
                enabled = false,
                profileType = ProfileType.USERPROFILE
            )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Profile(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null,
    onChange: ((Boolean) -> Unit)? = null
) {
    Column(modifier) {
        Row {
            if(state.profileType == ProfileType.ORGANIZER)
                IconButton(
                    { callback?.onBack() },
                    Modifier.padding(top = 10.dp, end = 16.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier,
                        colorScheme.tertiary
                    )
                }
            TextField(
                state.name,
                { callback?.onNameChange(it) },
                Modifier
                    .offset((-16).dp)
                    .fillMaxWidth(),
                colors = TransparentTextFieldColors(),
                textStyle = typography.displayLarge,
                placeholder = {
                    Row(Modifier, Center, CenterVertically) {
                        Text(
                            stringResource(R.string.user_name),
                            Modifier.padding(end = 8.dp),
                            colorScheme.onTertiary,
                            style = typography.titleLarge
                        )
                        Icon(
                            painterResource(R.drawable.ic_edit),
                            stringResource(R.string.edit_button),
                            Modifier.padding(top = 4.dp),
                            colorScheme.outlineVariant
                        )
                    }
                },
                readOnly = !state.enabled,
                singleLine = true
            )
        }
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
                Modifier,
                state.profilePhoto ?: "",
                state.profileType,
                state.observeState,
                { bool -> onChange?.let { it(bool) } },
                { callback?.profileImage() })
            Spacer(Modifier.width(14.dp))
            Column {
                ProfileStatisticContent(
                    Modifier,
                    state.rating,
                    state.observers,
                    state.observed,
                    state.emoji
                ) { callback?.onObserveClick() }
                Spacer(Modifier.height(18.dp))
                HiddenPhotoContent(
                    Modifier,
                    state.hiddenPhoto,
                    state.profileType
                ) { callback?.hiddenImages() }
            }
        }
        Text(
            stringResource(R.string.profile_about_me),
            Modifier.padding(top = 20.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        val focusManager = LocalFocusManager.current
        TextField(
            state.description,
            { callback?.onDescriptionChange(it) },
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            readOnly = (state.profileType == ProfileType.ORGANIZER),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldColors(),
            textStyle = typography.bodyMedium,
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
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