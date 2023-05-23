package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Profile
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfilePreview() {
    GiltyTheme { ProfileContent(ProfileState(DemoProfileModel), (true)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileState,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    callback: ProfileCallback? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ActionBar(
                title = stringResource(R.string.create_profile_title)
            ) { callback?.onBack() }
        },
    ) {
        Card(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .offset(y = 24.dp)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(30.dp),
            colors = cardColors(colorScheme.background),
            border = BorderStroke(6.dp, colorScheme.primaryContainer)
        ) {
            Profile(
                state, Modifier
                    .scrollable(
                        rememberScrollState(),
                        Vertical
                    )
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 50.dp),
                callback
            )
        }
    }
    Box(Modifier.fillMaxSize()) {
        GradientButton(
            modifier = Modifier
                .align(BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 46.dp),
            text = stringResource(R.string.next_button),
            enabled = isActive,
            onDisabledClick = {
                callback?.onDisabledButtonClick()
            }
        ) { callback?.onNext() }
    }
}

