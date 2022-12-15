package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private const val header = "Заголовок"
private const val label = "Описание"
private const val content = "Любой контент"

@Preview
@Composable
private fun AlertDialogPreview() {
    GiltyTheme {
        GAlert(true, {}, header,
            Modifier.padding(10.dp), label,
            Pair(stringResource(R.string.cancel)) {},
            Pair(stringResource(R.string.save_button)) {})
    }
}

@Preview
@Composable
private fun AlertDialogWithContentPreview() {
    GiltyTheme {
        GAlert(true, { }, header,
            Modifier.padding(10.dp),
            success = Pair(stringResource(R.string.save_button)) {},
            cancel = Pair(stringResource(R.string.cancel)) {}) {
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
        val list = remember {
            mutableStateListOf(
                Pair("Удалить у меня", true),
                Pair("Удалить у всех", false)
            )
        }
        Box(Modifier.fillMaxSize()) {
            GAlert(true, {}, header, Modifier.padding(10.dp),
                cancel = Pair(stringResource(R.string.cancel_button)) {},
                success = Pair(stringResource(R.string.confirm_button)) {},
                list = list, listItemSelect = { active ->
                    repeat(list.size) {
                        if(it == active) list[it] = Pair(list[it].first, true)
                        else list[it] = Pair(list[it].first, false)
                    }
                })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GAlert(
    show: Boolean,
    onDismissRequest: (() -> Unit)? = null,
    title: (String)? = null,
    modifier: Modifier = Modifier,
    label: String? = null,
    success: Pair<String, () -> Unit>,
    cancel: Pair<String, () -> Unit>? = null,
    list: List<Pair<String, Boolean>>? = null,
    listItemSelect: ((Int) -> Unit)? = null,
    accentColors: Color = colorScheme.primary,
    content: (@Composable () -> Unit)? = null,
) {
    if(show) AlertDialog(
        onDismissRequest ?: {},
        confirmButton = {
            Text(
                success.first, Modifier
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { success.second() },
                accentColors,
                style = typography.labelSmall,
                fontWeight = SemiBold
            )
        }, modifier,
        dismissButton = {
            cancel?.let {
                Text(
                    it.first,
                    Modifier
                        .padding(end = 16.dp)
                        .clickable(
                            MutableInteractionSource(), (null)
                        ) { it.second() },
                    accentColors,
                    style = typography.labelSmall,
                    fontWeight = SemiBold
                )
            }
        }, (null), {
            title?.let {
                Text(
                    it, Modifier,
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
                        colorScheme.tertiary,
                        style = typography.labelSmall,
                        fontWeight = SemiBold
                    )
                }; content?.invoke()
                list?.let {
                    List(list)
                    { listItemSelect?.let { s -> s(it) } }
                }
            }
        }, containerColor = colorScheme.primaryContainer
    )
}

@Composable
private fun List(
    list: List<Pair<String, Boolean>>,
    select: ((Int) -> Unit)? = null
) {
    LazyColumn {
        itemsIndexed(list) { index, item ->
            ListItem(index, item)
            { select?.let { select -> select(it) } }
        }
    }
}

@Composable
private fun ListItem(
    index: Int,
    item: Pair<String, Boolean>,
    accentColors: Color = colorScheme.primary,
    select: ((Int) -> Unit)? = null
) {
    Row(
        Modifier.padding(horizontal = 16.dp),
        Start, CenterVertically
    ) {
        CheckBox(
            item.second, Modifier.clip(CircleShape),
            listOf(
                R.drawable.ic_radio_active,
                R.drawable.ic_radio_inactive
            ), if(item.second) accentColors
            else colorScheme.tertiary
        ) { select?.let { it(index) } }
        Text(
            item.first,
            Modifier
                .padding(start = 14.dp)
                .clickable(
                    MutableInteractionSource(), (null)
                ) { select?.let { it(index) } },
            colorScheme.tertiary, fontWeight = SemiBold,
            style = typography.labelSmall
        )
    }
}