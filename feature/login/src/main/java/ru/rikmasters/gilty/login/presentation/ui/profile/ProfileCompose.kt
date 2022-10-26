package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.Profile
import ru.rikmasters.gilty.shared.shared.ProfileCallback
import ru.rikmasters.gilty.shared.shared.ProfileState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfilePreview() {
    GiltyTheme {
        val lockState = remember { mutableStateOf(false) }
        val name = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }
        val profileState = ProfileState(
            name = name.value,
            lockState = lockState.value,
            description = description.value,
            enabled = true
        )
        ProfileContent(profileState, Modifier, object : ProfileCallback {
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
fun ProfileContent(
    state: ProfileState,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ActionBar(
                stringResource(R.string.create_profile_title)
            )
            { callback?.onBack() }
            Card(
                Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                RoundedCornerShape(30.dp),
                CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                border = BorderStroke(6.dp, ThemeExtra.colors.cardBackground)
            ) {
                Profile(
                    state,
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 50.dp),
                    callback
                )
            }
        }
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
}
