package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ProfilePreview() {
    GiltyTheme {
        ProfileContent(
            ProfileState(profile = DemoProfileModel),
            (true)
        )
    }
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
                title = stringResource(
                    R.string.create_profile_title
                ),
                modifier = Modifier.background(
                    colorScheme.background
                ),
                titleStyle = typography
                    .titleLarge.copy(
                        fontSize = 28.dp.toSp()
                    )
            ) { callback?.onBack() }
        },
    ) {
        Column(
            Modifier.verticalScroll(
                rememberScrollState(),
            )
        ) {
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .height(800.dp)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(30.dp),
                colors = cardColors(colorScheme.background),
                border = BorderStroke(
                    width = 6.dp,
                    color = colorScheme.primaryContainer
                )
            ) {
                Profile(
                    state = state,
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(top = 50.dp),
                    callback = callback
                )
            }
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

