package ru.rikmasters.gilty.profile.presentation.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.image_categories_settings
import ru.rikmasters.gilty.shared.R.string.settings_interest_label
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun SettingsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            val user = DemoProfileModel
            SettingsContent(
                SettingsState(
                    user.gender,
                    user.age.toString(),
                    user.orientation,
                    user.phone.toString(),
                    (true), (false), (false)
                )
            )
        }
    }
}

data class SettingsState(
    val gender: GenderType?,
    val age: String,
    val orientation: OrientationModel?,
    val phone: String,
    val notification: Boolean,
    val exitAlert: Boolean,
    val deleteAlert: Boolean,
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
    fun onExitDismiss()
    fun onExitSuccess()
    fun onDeleteSuccess()
    fun onDeleteDismiss()
}

@Composable
fun SettingsContent(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null,
) {
    LazyColumn(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        item { Categories(Modifier, callback) }
        item { Information(state, Modifier, callback) }
        item { Additionally(state, Modifier, callback) }
        item { Buttons(Modifier, callback) }
    }
    GAlert(
        state.exitAlert, Modifier,
        Pair(stringResource(R.string.exit_button))
        { callback?.onExitSuccess() },
        onDismissRequest = { callback?.onExitDismiss() },
        title = stringResource(R.string.settings_exit_alert),
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onExitDismiss() },
    )
    GAlert(
        state.deleteAlert, Modifier,
        Pair(stringResource(R.string.meeting_filter_delete_tag_label))
        { callback?.onDeleteSuccess() },
        onDismissRequest = { callback?.onDeleteDismiss() },
        title = stringResource(R.string.settings_delete_account_alert),
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onDeleteDismiss() },
    )
}

@Composable
private fun Categories(
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null,
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
        Text(
            stringResource(settings_interest_label),
            Modifier.padding(start = 16.dp, bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        Image(
            painterResource(image_categories_settings),
            (null), Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .clip(shapes.large)
                .clickable { callback?.editCategories() },
            contentScale = FillWidth
        )
    }
}

@Composable
private fun Information(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null,
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
                ThemeExtra.shapes.mediumTopRoundedShape,
                Modifier, state.gender?.value ?: ""
            ) { callback?.onGenderClick() }
            GDivider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.personal_info_age_placeholder),
                ThemeExtra.shapes.zero,
                Modifier, ageHolder(state.age)
            ) { callback?.onAgeClick() }
            GDivider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.orientation_title),
                ThemeExtra.shapes.zero,
                Modifier, state.orientation?.name
            ) { callback?.onOrientationClick() }
            GDivider(Modifier.padding(start = 16.dp))
            Card(
                stringResource(R.string.phone_number),
                ThemeExtra.shapes.mediumBottomRoundedShape,
                Modifier, state.phone,
                arrow = false
            ) { callback?.onPhoneClick() }
        }
    }, modifier.padding(top = 28.dp))
}

@Composable
private fun Additionally(
    state: SettingsState,
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null,
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
                ThemeExtra.shapes.mediumTopRoundedShape,
                Modifier, (null)
            ) { callback?.onAboutAppClick() }
            GDivider(Modifier.padding(start = 16.dp))
            
            // TODO Функциональность смены иконки приложения
            //            Card(
            //                stringResource(R.string.settings_app_icon_label),
            //                ThemeExtra.shapes.zero,
            //                Modifier, (null)
            //            ) { callback?.onIconAppClick() }
            
            GDivider(Modifier.padding(start = 16.dp))
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
    shape: Shape,
    modifier: Modifier = Modifier,
    text: String? = null,
    arrow: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        onClick, modifier.fillMaxWidth(),
        (true), shape,
        cardColors(colorScheme.primaryContainer)
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
                if(!text.isNullOrBlank()) Text(
                    text, Modifier, colorScheme.primary,
                    style = typography.bodyMedium,
                )
                if(arrow) Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun ageHolder(age: String): String {
    if(age.isBlank()) return ""
    if(age == "-1") return stringResource(R.string.condition_no_matter)
    return "$age ${
        when(age.last()) {
            '1' -> "год"
            '2', '3', '4' -> "года"
            else -> "лет"
        }
    }"
}

@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    callback: SettingsCallback? = null,
) {
    Column(
        modifier.fillMaxWidth(),
        Top, CenterHorizontally
    ) {
        Text(
            stringResource(R.string.exit_button),
            Modifier
                .padding(top = 28.dp, bottom = 16.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { callback?.onExit() },
            colorScheme.primary,
            style = typography.bodyLarge
        )
        Text(
            stringResource(R.string.settings_delete_account_label),
            Modifier
                .padding(bottom = 28.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { callback?.onDelete() },
            colorScheme.tertiary,
            style = typography.bodyLarge
        )
    }
}