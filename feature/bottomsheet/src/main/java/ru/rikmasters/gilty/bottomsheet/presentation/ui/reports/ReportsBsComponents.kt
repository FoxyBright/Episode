package ru.rikmasters.gilty.bottomsheet.presentation.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components.BoxLabel
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportArrowState.ARROW
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportArrowState.CHECK
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportArrowState.NONE
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.report.FalseInformation
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.MEETING
import ru.rikmasters.gilty.shared.model.report.ReportSubtype
import ru.rikmasters.gilty.shared.model.report.ReportSubtype.PHOTO_ANOTHER_USER
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.shared.lazyItemsShapes
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private val list = FalseInformation(MEETING).subTypes

@Preview
@Composable
private fun ComplainSelectPreview() {
    GiltyTheme {
        ComplainElements(
            ("Заголовок"), list,
            Modifier.padding(16.dp),
            (null), PHOTO_ANOTHER_USER
        )
    }
}

@Preview
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

@Preview
@Composable
private fun ComplainTextPreview() {
    GiltyTheme {
        ComplainTextBox(
            ("Другое"), ("Описание проблемы"),
            Modifier.padding(16.dp), {}, {}
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
            title, Modifier
                .offset(x = -(12).dp)
                .padding(bottom = 26.dp),
            distanceBetween = 8.dp
        ) { onBack?.let { it() } }
        GTextField(
            text, {
                if(it.length <= 120)
                    onTextChange(it)
            }, Modifier.fillMaxWidth(),
            colors = complaintsTextFieldColors(),
            clear = onClear,
            keyboardOptions = Default.copy(
                imeAction = Done, keyboardType = Text,
                capitalization = Sentences
            ),
            label = if(text.isNotEmpty())
                label(true) else null,
            placeholder = label()
        )
        BoxLabel(
            "${text.length}/120", Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            TextAlign.End
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun complaintsTextFieldColors() =
    TextFieldDefaults.textFieldColors(
        textColor = colorScheme.tertiary,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.scrim,
        focusedLabelColor = colorScheme.onTertiary,
        disabledTrailingIconColor = Color.Transparent,
        focusedTrailingIconColor = Color.Transparent,
        unfocusedTrailingIconColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorLabelColor = colorScheme.primary,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = colorScheme.scrim,
        disabledTextColor = colorScheme.scrim,
    )

@Composable
fun ComplainElements(
    title: String,
    list: List<ReportSubtype>,
    modifier: Modifier = Modifier,
    description: String? = null,
    select: ReportSubtype? = null,
    state: ReportArrowState = NONE,
    onBack: (() -> Unit)? = null,
    onSelect: ((ReportSubtype) -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        RowActionBar(
            title,
            Modifier
                .offset(x = -(12).dp)
                .padding(bottom = 26.dp),
            distanceBetween = 8.dp,
            description = description
        ) { onBack?.let { it() } }
        LazyColumn(
            Modifier.background(
                colorScheme.primaryContainer,
                shapes.medium
            )
        ) {
            itemsIndexed(list) { i, item ->
                Column {
                    ComplainItem(
                        i, item, list.size, Modifier,
                        select?.let {
                            if(it.display == item.display)
                                CHECK else NONE
                        } ?: state
                    ) { onSelect?.let { it(item) } }
                    if(i < list.size - 1)
                        GDivider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

enum class ReportArrowState {
    ARROW, NONE, CHECK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComplainItem(
    index: Int,
    item: ReportSubtype,
    size: Int,
    modifier: Modifier = Modifier,
    state: ReportArrowState,
    onClick: () -> Unit,
) {
    Card(
        onClick, modifier, (true),
        lazyItemsShapes(index, size),
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(Max)
                .padding(16.dp, 20.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                item.display, Modifier.weight(1f),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Icon(Modifier, state)
        }
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
private fun Icon(
    modifier: Modifier = Modifier,
    state: ReportArrowState,
) {
    when(state) {
        ARROW -> Icon(
            Filled.KeyboardArrowRight,
            (null), modifier.size(28.dp),
            colorScheme.onTertiary
        )
        else -> Icon(
            painterResource(R.drawable.ic_done),
            (null), modifier.size(24.dp),
            if(state == CHECK)
                colorScheme.primary
            else colorScheme.primaryContainer
        )
    }
}