package ru.rikmasters.gilty.profile.presentation.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.NumberPicker
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun SelectBottom(
    title: String,
    genderList: List<String>,
    list: List<Boolean>,
    onItemClick: ((Int) -> Unit)? = null
) {
    BottomContainer(title) {
        FlowLayout(
            Modifier
                .background(
                    colorScheme.primaryContainer,
                    MaterialTheme.shapes.large
                )
                .padding(8.dp)
        ) {
            list.forEachIndexed { index, it ->
                GiltyChip(
                    Modifier.padding(end = 12.dp),
                    genderList[index], it
                ) { onItemClick?.let { c -> c(index) } }
            }
        }
    }
}

@Composable
fun IconsBottom(onItemClick: ((Int) -> Unit)?) {
    BottomContainer(
        stringResource(R.string.settings_app_icon_label)
    ) {
        Row{
            IconItem { onItemClick?.let { c -> c(it) } }
            IconItem(true, Modifier.padding(start = 16.dp))
            { onItemClick?.let { c -> c(it) } }
        }
    }
}

@Composable
private fun IconItem(
    dark: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: ((Int) -> Unit)? = null
) {
    Column(
        modifier, Top,
        CenterHorizontally
    ) {
        Image(
            painterResource(
                if (dark) R.drawable.ic_logo_dark
                else R.drawable.ic_logo_white
            ), (null),
            Modifier
                .size(70.dp)
                .clickable { onClick?.let { it(0) } }
        )
        Text(
            stringResource(if (dark) R.string.settings_dark_icon_label else R.string.settings_white_icon_label),
            Modifier
                .padding(top = 16.dp)
                .clickable { onClick?.let { it(1) } },
            if (dark) colorScheme.primary
            else colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
        )
    }
}

@Composable
fun AboutAppBottom(onItemClick: ((Int) -> Unit)?) {
    BottomContainer(
        stringResource(R.string.settings_about_app_label)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    MaterialTheme.shapes.medium
                )
        ) {
            AboutItem(
                stringResource(R.string.settings_about_app_privacy_policy),
                ThemeExtra.shapes.mediumTopRoundedShape,
            ) { onItemClick?.let { it(0) } }
            Divider(Modifier.padding(start = 16.dp))
            AboutItem(
                stringResource(R.string.settings_about_app_agreement),
                ThemeExtra.shapes.zero,
            ) { onItemClick?.let { it(1) } }
            Divider(Modifier.padding(start = 16.dp))
            AboutItem(
                stringResource(R.string.settings_about_app_rules),
                ThemeExtra.shapes.zero,
            ) { onItemClick?.let { it(2) } }
            Divider(Modifier.padding(start = 16.dp))
            AboutItem(
                stringResource(R.string.settings_about_app_help),
                ThemeExtra.shapes.mediumBottomRoundedShape,
            ) { onItemClick?.let { it(3) } }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AboutItem(
    text: String,
    shape: Shape,
    onItemClick: (() -> Unit)? = null
) {
    Card(
        { onItemClick?.let { it() } },
        Modifier, (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                text, Modifier.weight(1f),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Icon(
                Filled.KeyboardArrowRight,
                (null), Modifier.size(24.dp),
                colorScheme.tertiary
            )
        }
    }
}

@Composable
fun AgeBottom(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int) -> Unit,
    range: IntRange = 18..100,
    onSave: () -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .padding(16.dp),
        Alignment.TopCenter
    ) {
        NumberPicker(
            Modifier.padding(top = 40.dp),
            value = value ?: range.first,
            onValueChange = { onValueChange(it) },
            range = range
        )
        GradientButton(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            stringResource(R.string.save_button), true
        ) { onSave() }
    }
}

@Composable
private fun BottomContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Element(
        FilterModel(title) {
            Box {
                Box(Modifier.padding(bottom = 40.dp)) {
                    content.invoke()
                }
            }
        }, modifier.padding(16.dp, 28.dp)
    )
}