package ru.rikmasters.gilty.profile.presentation.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.image_categories_settings
import ru.rikmasters.gilty.shared.R.string.edit_button
import ru.rikmasters.gilty.shared.R.string.settings_interest_label
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun SettingsPreview() {
    GiltyTheme {
        SettingsContent(
            SettingsState(DemoProfileModel, (false))
        )
    }
}

data class SettingsState(
    val profile: ProfileModel,
    val notification: Boolean,
)

interface SettingsCallback {
    fun onBack()
    fun editCategories()
    fun onNotificationChange(it: Boolean)
    fun onAboutAppClick()
    fun onIconAppClick()
    fun onGenderClick()
    fun onAgeClick()
    fun onOrientationClick()
    fun onPhoneClick()
    fun onExit()
    fun onDelete()
}

@Composable
fun SettingsContent(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .scrollable(
                rememberScrollState(), Vertical
            )
    ) {
        item { Categories(Modifier, callback) }
        item { Information(state, Modifier, callback) }
        item { Additionally(state, Modifier, callback) }
        item { Buttons(Modifier, callback) }
    }
}

@Composable
private fun Categories(
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null
) {
    Column(modifier.fillMaxWidth()) {
        IconButton(
            { callback?.onBack() },
            Modifier.padding(top = 32.dp)
        ) {
            Icon(
                painterResource(R.drawable.ic_back),
                (null), Modifier, colorScheme.tertiary
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            SpaceBetween
        ) {
            Text(
                stringResource(settings_interest_label),
                Modifier, colorScheme.tertiary,
                style = typography.labelLarge
            )
            Text(
                stringResource(edit_button),
                Modifier.clickable { callback?.editCategories() },
                colorScheme.primary,
                style = typography.bodyMedium
            )
        }
        Image(
            painterResource(image_categories_settings),
            (null), Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .clip(shapes.large),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
private fun Information(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null
) {
    Element(FilterModel(stringResource(R.string.personal_info_title)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            Card(
                stringResource(R.string.sex),
                state.profile.gender.value,
                ThemeExtra.shapes.mediumTopRoundedShape
            ) { callback?.onGenderClick() }
            Divider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.personal_info_age_placeholder),
                "${state.profile.age} лет",
                ThemeExtra.shapes.zero
            ) { callback?.onAgeClick() }
            Divider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.orientation_title),
                state.profile.orientation?.display,
                ThemeExtra.shapes.zero
            ) { callback?.onOrientationClick() }
            Divider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.phone_number),
                state.profile.phone,
                ThemeExtra.shapes.mediumBottomRoundedShape,
                arrow = false
            ) { callback?.onPhoneClick() }
        }
    }, modifier.padding(top = 28.dp))
}

@Composable
private fun Additionally(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null
) {
    Element(FilterModel(stringResource(R.string.add_meet_conditions_additionally)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            Card(
                stringResource(R.string.settings_about_app_label),
                (null), ThemeExtra.shapes.mediumTopRoundedShape
            ) { callback?.onAboutAppClick() }
            Divider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.settings_app_icon_label),
                (null), ThemeExtra.shapes.zero
            ) { callback?.onIconAppClick() }
            Divider(Modifier.padding(start = 16.dp))
            CheckBoxCard(
                stringResource(R.string.notification_screen_name),
                modifier.fillMaxWidth(),
                state.notification,
                ThemeExtra.shapes.mediumBottomRoundedShape
            ) { callback?.onNotificationChange(it) }
        }
    }, modifier.padding(top = 28.dp))
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Card(
    label: String,
    text: String? = null,
    shape: Shape,
    modifier: Modifier = Modifier,
    arrow: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier.fillMaxWidth(), (true), shape,
        CardDefaults.cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween,
            CenterVertically
        ) {
            Text(
                label, Modifier, colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Row(verticalAlignment = CenterVertically) {
                if (!text.isNullOrBlank()) Text(
                    text, Modifier, colorScheme.primary,
                    style = typography.bodyMedium,
                )
                if (arrow) Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null
) {
    Column(
        modifier.fillMaxWidth(),
        Top, CenterHorizontally
    ) {
        Text(
            stringResource(R.string.exit_button),
            Modifier
                .padding(top = 28.dp, bottom = 16.dp)
                .clickable { callback?.onExit() },
            colorScheme.primary,
            style = typography.bodyLarge
        )
        Text(
            stringResource(R.string.settings_delete_account_label),
            Modifier
                .padding(bottom = 28.dp)
                .clickable { callback?.onDelete() },
            colorScheme.tertiary,
            style = typography.bodyLarge
        )
    }
}