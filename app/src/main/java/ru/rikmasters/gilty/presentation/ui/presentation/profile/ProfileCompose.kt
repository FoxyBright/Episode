package ru.rikmasters.gilty.presentation.ui.presentation.profile

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.TextFieldColors
import ru.rikmasters.gilty.presentation.ui.shared.TransparentTextFieldColors
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class ProfileState(
    val name: String = "",
    val hiddenPhoto: Int? = null,
    val ProfilePhoto: Int? = null,
    val lockState: Boolean = false,
    val description: String = "",
    val rating: String = "0",
    val observers: Int = 0,
    val observed: Int = 0,
    val enabled: Boolean = true
)

interface ProfileCallback : NavigationInterface {
    fun profileImage() {}
    fun hiddenImages() {}
    fun onLockClick(state: Boolean) {}
    fun onNameChange(text: String) {}
    fun onDescriptionChange(text: String) {}
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfilePreview() {
    GiltyTheme {
        val lockState = remember { mutableStateOf(false) }
        val name = remember { mutableStateOf("alina.loon, 27") }
        val description = remember { mutableStateOf("instagram @cristy") }
        val profileState = ProfileState(
            name.value,
            lockState = lockState.value,
            description = description.value,
            enabled = false
        )
        Profile(profileState, Modifier, object : ProfileCallback {
            override fun onNameChange(text: String) {
                name.value = text
            }

            override fun onLockClick(state: Boolean) {
                lockState.value = state
            }

            override fun onDescriptionChange(text: String) {
                description.value = text
            }
        })
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Profile(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null
) {
    Column(modifier) {
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
                        style = ThemeExtra.typography.ExtraHeader
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
        Row {
            ProfileImageContent(Modifier, state.ProfilePhoto)
            { callback?.profileImage() }
            Spacer(Modifier.width(14.dp))
            Column {
                ProfileStatisticContent(
                    Modifier,
                    state.rating,
                    state.observers,
                    state.observed
                )
                Spacer(Modifier.height(14.dp))
                HiddenPhotoContent(
                    Modifier,
                    state.lockState,
                    state.hiddenPhoto,
                    { callback?.hiddenImages() },
                    { callback?.onLockClick(it) })
            }
        }
        Text(
            stringResource(R.string.about_me),
            Modifier.padding(top = 20.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.Body1Bold
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
                    style = ThemeExtra.typography.LabelText,
                    fontWeight = FontWeight.Medium
                )
            }
        )
    }
}