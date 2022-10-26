package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.ui.HiddenPhotoContent
import ru.rikmasters.gilty.presentation.ui.ProfileImageContent
import ru.rikmasters.gilty.presentation.ui.ProfileStatisticContent
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

data class ProfileState(
    val name: String = "",
    val hiddenPhoto: String? = null,
    val profilePhoto: String? = null,
    val lockState: Boolean = false,
    val description: String = "",
    val rating: String = "0",
    val observers: Int = 0,
    val observed: Int = 0,
    val emoji: EmojiModel? = null,
    val profileType: ProfileType = ProfileType.CREATE,
    var observeState: Boolean = false,
    val enabled: Boolean = true
)

interface ProfileCallback : NavigationInterface {
    fun profileImage() {}
    fun hiddenImages() {}
    fun onLockClick(state: Boolean) {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
    fun onObserveChange(state: Boolean) {}
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun EditProfilePreview() {
    GiltyTheme {
        val lockState = remember { mutableStateOf(false) }
        var observeState by remember { mutableStateOf(false) }
        val name = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }
        val profileState = ProfileState(
            name.value,
            lockState = lockState.value,
            description = description.value,
            enabled = true,
            observeState = observeState,
            profileType = ProfileType.CREATE
        )
        Profile(profileState, Modifier, object : ProfileCallback {
            override fun onNameChange(text: String) {
                name.value = text
            }

            override fun onLockClick(state: Boolean) {
                lockState.value = state
            }

            override fun onObserveChange(state: Boolean) {
                observeState = state
            }

            override fun onDescriptionChange(text: String) {
                description.value = text
            }
        })
    }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun OrganizerProfilePreview() {
    GiltyTheme {
        val user = DemoProfileModel
        Profile(
            ProfileState(
                "${user.username}, ${user.age}",
                lockState = false,
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
                lockState = false,
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
            if (state.profileType == ProfileType.ORGANIZER)
                IconButton(
                    { callback?.onBack() },
                    Modifier.padding(top = 10.dp, end = 16.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier,
                        ThemeExtra.colors.mainTextColor
                    )
                }
            TextField(
                state.name,
                { callback?.onNameChange(it) },
                Modifier
                    .offset((-16).dp)
                    .fillMaxWidth(),
                colors = TransparentTextFieldColors(),
                textStyle = ThemeExtra.typography.ExtraHeader,
                placeholder = {
                    Row(Modifier, Arrangement.Center, Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.user_name),
                            Modifier.padding(end = 8.dp),
                            ThemeExtra.colors.secondaryTextColor,
                            style = ThemeExtra.typography.H1
                        )
                        Icon(
                            painterResource(R.drawable.ic_edit),
                            stringResource(R.string.edit),
                            Modifier.padding(top = 4.dp),
                            ThemeExtra.colors.grayIcon
                        )
                    }
                },
                readOnly = !state.enabled,
                singleLine = true
            )
        }
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
                    state.emoji?.path
                )
                Spacer(Modifier.height(14.dp))
                HiddenPhotoContent(
                    Modifier,
                    state.lockState,
                    state.hiddenPhoto,
                    state.profileType,
                    { callback?.hiddenImages() },
                    { callback?.onLockClick(it) })
            }
        }
        Text(
            stringResource(R.string.profile_about_me),
            Modifier.padding(top = 20.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.H3
        )
        TextField(
            state.description,
            { callback?.onDescriptionChange(it) },
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            readOnly = !state.enabled,
            shape = MaterialTheme.shapes.large,
            colors = TextFieldColors(),
            textStyle = ThemeExtra.typography.LabelText,
            placeholder = {
                Text(
                    stringResource(R.string.about_me_placeholder),
                    color = ThemeExtra.colors.secondaryTextColor,
                    style = ThemeExtra.typography.Body1Medium
                )
            }
        )
    }
}