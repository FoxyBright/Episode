package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_radio_active
import ru.rikmasters.gilty.shared.R.drawable.ic_radio_inactive
import ru.rikmasters.gilty.shared.R.string.delete_my_and_other_chat_button
import ru.rikmasters.gilty.shared.R.string.delete_my_chat_button
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

private const val header = "Заголовок"
private const val label = "Описание"
private const val content = "Любой контент"

@Preview
@Composable
private fun AlertDialogPreview() {
    GiltyTheme {
        GAlert(
            (true), Modifier.padding(10.dp),
            Pair(stringResource(R.string.save_button)) {},
            header, label, cancel = Pair(
                stringResource(R.string.cancel)
            ) {}
        )
    }
}

@Preview
@Composable
private fun AlertDialogWithContentPreview() {
    GiltyTheme {
        GAlert(
            (true), Modifier.padding(10.dp),
            success = Pair(stringResource(R.string.save_button)) {},
            header, cancel = Pair(
                stringResource(R.string.cancel)
            ) {}) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(White),
                Alignment.Center
            ) {
                Text(
                    content, Modifier,
                    colorScheme.tertiary,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun ListAlertDialogPreview() {
    GiltyTheme {
        Box(Modifier.fillMaxSize()) {
            GAlert(true, Modifier.padding(10.dp),
                Pair(stringResource(R.string.confirm_button)) {},
                header, cancel = Pair(stringResource(R.string.cancel_button)) {},
                list = listOf(
                    stringResource(delete_my_chat_button),
                    stringResource(delete_my_and_other_chat_button)
                ), listItemSelect = {})
        }
    }
}

@Composable
fun GAlert(
    show: Boolean,
    modifier: Modifier = Modifier,
    success: Pair<String, () -> Unit>,
    label: String? = null,
    title: String? = null,
    onDismissRequest: (() -> Unit)? = null,
    cancel: Pair<String, () -> Unit>? = null,
    list: List<String>? = null,
    selected: Int? = null,
    listItemSelect: ((Int) -> Unit)? = null,
    accentColors: Color = colorScheme.primary,
    onlyDarkTheme: Boolean = false,
    content: (@Composable () -> Unit)? = null
) {
    if (show) AlertDialog(
        onDismissRequest ?: {},
        confirmButton = {
            Text(
                success.first, Modifier
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { success.second() },
                if (onlyDarkTheme) ThemeExtra.colors.mainDayGreen else
                    accentColors,
                style = typography.labelSmall,
                fontWeight = SemiBold
            )
        }, modifier,
        dismissButton = {
            cancel?.let {
                Text(
                    it.first, Modifier
                        .padding(end = 16.dp)
                        .clickable(
                            MutableInteractionSource(), (null)
                        ) { it.second() },
                    if (onlyDarkTheme) ThemeExtra.colors.mainDayGreen else
                        accentColors,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
        }, (null), {
            title?.let {
                Text(
                    it, Modifier,
                    if (onlyDarkTheme) White else
                        colorScheme.tertiary,
                    style = typography.displayLarge,
                    fontWeight = SemiBold
                )
            }
        }, {
            Column {
                label?.let {
                    Text(
                        it, Modifier.padding(bottom = 16.dp),
                        if (onlyDarkTheme) Color(0xFF98989F) else
                            colorScheme.tertiary,
                        style = typography.labelSmall,
                        fontWeight = SemiBold
                    )
                }; content?.invoke()
                list?.let { items ->
                    selected?.let {
                        List(items, it, accentColors) {
                            listItemSelect?.let { s -> s(it) }
                        }
                    }
                }
            }
        },
        containerColor = if (onlyDarkTheme) ThemeExtra.colors.mainCard else
            colorScheme.primaryContainer
    )
}

@Composable
private fun List(
    list: List<String>,
    selected: Int,
    accentColors: Color,
    onSelect: ((Int) -> Unit),
) {
    LazyColumn {
        itemsIndexed(list) { index, item ->
            Row(
                Modifier
                    .padding(bottom = 30.dp),
                Start, CenterVertically
            ) {
                CheckBox(
                    (selected == index), Modifier
                        .clip(CircleShape), listOf(
                        ic_radio_active,
                        ic_radio_inactive
                    ), if (selected == index) accentColors
                    else colorScheme.tertiary
                ) { onSelect(index) }
                Text(
                    item, Modifier
                        .padding(start = 8.dp)
                        .clickable(
                            MutableInteractionSource(), (null)
                        ) { onSelect(index) },
                    colorScheme.tertiary, fontWeight = SemiBold,
                    style = typography.labelSmall
                )
            }
        }
    }
}