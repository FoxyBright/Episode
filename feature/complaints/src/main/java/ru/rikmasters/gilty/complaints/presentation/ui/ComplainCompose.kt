package ru.rikmasters.gilty.complaints.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.shared.lazyItemsShapes
import ru.rikmasters.gilty.shared.shared.textFieldColors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private val list = listOf("item 1", "item 1", "item 1")
private val listBoolean = listOf(true, false, false)

@Preview(showBackground = true)
@Composable
private fun ComplainSelectPreview() {
    GiltyTheme {
        ComplainElements(
            ("Заголовок"), list,
            Modifier.padding(16.dp),
            (null), listBoolean, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ComplainElementsPreview() {
    GiltyTheme {
        ComplainElements(
            ("Заголовок"), list,
            Modifier.padding(16.dp),
            ("Описание")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ComplainTextPreview() {
    GiltyTheme {
        ComplainTextBox(
            ("Другое"), ("Описание проблемы"),
            Modifier.padding(16.dp),
            {}, {}
        ) {}
    }
}

@Composable
fun ComplainTextBox(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        RowActionBar(
            title, Modifier.padding(bottom = 12.dp)
        ) { onBack?.let { it() } }
        GTextField(
            text, { onTextChange(it) },
            Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            clear = onClear,
            label = if(text.isNotEmpty())
                label(true) else null,
            placeholder = label()
        )
    }
}

@Composable
private fun label(
    label: Boolean = false,
): @Composable (() -> Unit) {
    return {
        Text(
            stringResource(R.string.complaints_send_placeholder),
            Modifier, style = if(label)
                typography.headlineSmall
            else typography.bodyMedium
        )
    }
}

@Composable
fun ComplainElements(
    title: String,
    list: List<String>,
    modifier: Modifier = Modifier,
    description: String? = null,
    selectedList: List<Boolean>? = null,
    onBack: (() -> Unit)? = null,
    onSelect: ((index: Int) -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        RowActionBar(
            title, Modifier.padding(bottom = 12.dp),
            description = description
        ) { onBack?.let { it() } }
        LazyColumn(
            Modifier.background(
                colorScheme.primaryContainer, shapes.medium
            )
        ) {
            itemsIndexed(list) { i, item ->
                Column {
                    ComplainItem(
                        i, item, list.size,
                        Modifier, selectedList?.get(i)
                    ) { index -> onSelect?.let { it(index) } }
                    if(i < list.size - 1)
                        Divider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComplainItem(
    index: Int,
    item: String,
    size: Int,
    modifier: Modifier = Modifier,
    select: Boolean? = null,
    onClick: ((Int) -> Unit)? = null,
) {
    Card(
        { onClick?.let { it(index) } },
        modifier, (true), lazyItemsShapes(index, size),
        CardDefaults.cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                item, Modifier.weight(1f),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Icon(Modifier, select)
        }
    }
}

@Composable
private fun Icon(
    modifier: Modifier = Modifier,
    select: Boolean? = null,
) {
    select?.let {
        if(it) Icon(
            painterResource(R.drawable.ic_done),
            (null), modifier.size(24.dp),
            colorScheme.primary
        )
    } ?: Icon(
        Icons.Filled.KeyboardArrowRight,
        (null), modifier.size(24.dp),
        colorScheme.onTertiary
    )
}