package ru.rikmasters.gilty.profile.presentation.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons.Filled
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
        show = state.exitAlert,
        success = Pair(stringResource(R.string.exit_button))
        { callback?.onExitSuccess() },
        onDismissRequest = { callback?.onExitDismiss() },
        title = stringResource(R.string.settings_exit_alert),
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onExitDismiss() },
    )
    GAlert(
        show = state.deleteAlert,
        success = Pair(stringResource(R.string.meeting_filter_delete_tag_label))
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
            onClick = { callback?.onBack() },
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_back
                ),
                contentDescription = null,
                modifier = Modifier,
                tint = colorScheme.tertiary
            )
        }
        Text(
            text = stringResource(settings_interest_label),
            modifier = Modifier.padding(
                start = 16.dp,
                bottom = 16.dp
            ),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        Image(
            painter = painterResource(
                image_categories_settings
            ),
            contentDescription = null,
            modifier = Modifier
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
    Element(
        FilterModel(
            stringResource(R.string.personal_info_title)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        colorScheme.primaryContainer,
                        shapes.medium
                    )
            ) {
                Card(
                    label = stringResource(R.string.sex),
                    shape = ThemeExtra.shapes
                        .mediumTopRoundedShape,
                    modifier = Modifier,
                    text = state.gender?.value ?: ""
                ) { callback?.onGenderClick() }
                GDivider(Modifier.padding(start = 16.dp))
                Card(
                    label = stringResource(R.string.personal_info_age_placeholder),
                    shape = ThemeExtra.shapes.zero,
                    modifier = Modifier,
                    text = ageHolder(state.age)
                ) { callback?.onAgeClick() }
                GDivider(Modifier.padding(start = 16.dp))
                Card(
                    label = stringResource(R.string.orientation_title),
                    shape = ThemeExtra.shapes.zero,
                    modifier = Modifier,
                    text = state.orientation?.name
                ) { callback?.onOrientationClick() }
                GDivider(Modifier.padding(start = 16.dp))
                Card(
                    label = stringResource(R.string.phone_number),
                    shape = ThemeExtra.shapes
                        .mediumBottomRoundedShape,
                    modifier = Modifier,
                    text = state.phone,
                    arrow = false
                ) { callback?.onPhoneClick() }
            }
        }, modifier.padding(top = 28.dp)
    )
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
                label = stringResource(R.string.settings_about_app_label),
                shape = ThemeExtra.shapes
                    .mediumTopRoundedShape,
                modifier = Modifier,
                text = null
            ) { callback?.onAboutAppClick() }
            GDivider(Modifier.padding(start = 16.dp))
            // TODO Функциональность смены иконки приложения
            //            Card(
            //                stringResource(R.string.settings_app_icon_label),
            //                ThemeExtra.shapes.zero,
            //                Modifier, (null)
            //            ) { callback?.onIconAppClick() }
            //            GDivider(Modifier.padding(start = 16.dp))
            CheckBoxCard(
                label = stringResource(R.string.notification_screen_name),
                modifier = modifier.fillMaxWidth(),
                state = state.notification,
                shape = ThemeExtra.shapes
                    .mediumBottomRoundedShape
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
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        enabled = true,
        shape = shape,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier,
                color = colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Row(verticalAlignment = CenterVertically) {
                if(!text.isNullOrBlank()) Text(
                    text = text,
                    modifier = Modifier,
                    color = colorScheme.primary,
                    style = typography.bodyMedium,
                )
                if(arrow) Icon(
                    imageVector = Filled.KeyboardArrowRight,
                    contentDescription = (null),
                    modifier = Modifier.size(28.dp),
                    tint = colorScheme.onTertiary
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
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Top,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.exit_button),
            modifier = Modifier
                .padding(top = 28.dp, bottom = 16.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { callback?.onExit() },
            color = colorScheme.primary,
            style = typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.settings_delete_account_label),
            modifier = Modifier
                .padding(bottom = 28.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { callback?.onDelete() },
            color = colorScheme.tertiary,
            style = typography.bodyLarge
        )
    }
}