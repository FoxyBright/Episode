package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun AlertDialogPreview() {
    GiltyTheme {
        GAlertDialog(
            ("Заголовок"), Modifier.padding(40.dp), ("Описание"),
            Pair("Отмена") {}, Pair("Сохранить") {}
        )
    }
}

@Preview
@Composable
private fun AlertDialogWithContentPreview() {
    GiltyTheme {
        GAlertDialog(
            ("Заголовок"), Modifier.padding(40.dp),
            success = Pair("Сохранить") {},
            cancel = Pair("Отмена") {}
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White), Center
            ) {
                Text(
                    "Любой контент",
                    Modifier, colorScheme.tertiary,
                    fontWeight = SemiBold,
                    style = typography.labelSmall
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
                Pair("Удалить у всех", false),
                Pair("Удалить", false)
            )
        }
        GAlertDialog(
            ("Заголовок"), Modifier.padding(40.dp),
            cancel = Pair("Отменить") {},
            success = Pair("Применить") {},
            list = list, listItemSelect = { active ->
                repeat(list.size) {
                    if (it == active) list[it] = Pair(list[it].first, true)
                    else list[it] = Pair(list[it].first, false)
                }
            }
        )
    }
}

@Composable
fun GAlertDialog(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    success: Pair<String, () -> Unit>,
    cancel: Pair<String, () -> Unit>? = null,
    list: List<Pair<String, Boolean>>? = null,
    listItemSelect: ((Int) -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Card(
        modifier, MaterialTheme.shapes.large,
        CardDefaults.cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(26.dp)) {
            Title(title, label)
            content?.invoke()
            list?.let { List(list) { listItemSelect?.let { s -> s(it) } } }
            Buttons(success, cancel)
        }
    }
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
    select: ((Int) -> Unit)? = null
) {
    Row(
        Modifier.padding(vertical = 16.dp),
        Start, CenterVertically
    ) {
        CheckBox(
            item.second, Modifier.clip(CircleShape),
            listOf(
                R.drawable.ic_radio_active,
                R.drawable.ic_radio_inactive
            ), if (item.second) colorScheme.primary
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

@Composable
private fun Title(
    title: String,
    label: String? = null,
) {
    Text(
        title, Modifier.padding(bottom = 16.dp),
        colorScheme.tertiary,
        style = typography.displayLarge,
        fontWeight = Bold
    )
    label?.let {
        Text(
            it, Modifier, colorScheme.tertiary,
            style = typography.labelSmall,
            fontWeight = SemiBold
        )
    }
}

@Composable
private fun Buttons(
    success: Pair<String, () -> Unit>,
    cancel: Pair<String, () -> Unit>? = null,
) {
    Row(
        Modifier
            .padding(top = 34.dp)
            .fillMaxWidth(), End
    ) {
        cancel?.let {
            Text(
                it.first, Modifier
                    .padding(end = 34.dp)
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { it.second() },
                colorScheme.primary, style = typography.labelSmall,
                fontWeight = SemiBold
            )
        }
        Text(
            success.first, Modifier.clickable(
                MutableInteractionSource(), (null)
            ) { success.second },
            colorScheme.primary, style = typography.labelSmall,
            fontWeight = SemiBold
        )
    }
}