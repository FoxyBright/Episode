package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.shared.GChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun GenderPreview() {
    GiltyTheme {
        SelectBottom(
            stringResource(R.string.sex),
            GenderType.shortGenderList
                .map { it.value },
            (1), false
        )
    }
}

@Preview
@Composable
fun AgePreview() {
    GiltyTheme {
        Box(
            Modifier.background(colorScheme.background)
        ) { AgeBottom("19", "40") }
    }
}

@Composable
fun AgeBottom(
    from: String,
    to: String,
    modifier: Modifier = Modifier,
    online: Boolean = false,
    fromChange: ((String) -> Unit)? = null,
    toChange: ((String) -> Unit)? = null,
    onSave: (() -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.personal_info_age_placeholder),
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(1f),
            color = colorScheme.tertiary,
            style = typography.labelLarge
        )
        AgeBottomContent(
            from = from,
            to = to,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(TopCenter),
            fromChange = fromChange,
            toChange = toChange
        )
        GradientButton(
            modifier = Modifier
                .padding(vertical = 28.dp)
                .align(BottomCenter),
            text = stringResource(R.string.save_button),
            online = online
        ) { onSave?.let { it() } }
    }
}

@Composable
private fun AgeBottomContent(
    from: String,
    to: String,
    modifier: Modifier = Modifier,
    fromChange: ((String) -> Unit)? = null,
    toChange: ((String) -> Unit)? = null,
) {
    arrayListOf<String>().let { list ->
        (18..99).forEach { list.add("$it") }
        list
    }.let { ageList ->
        Box(
            modifier.background(
                colorScheme.background
            ), Center
        ) {
            Row(
                modifier.padding(horizontal = 20.dp),
                Arrangement.Center
            ) {
                Row(
                    Modifier.weight(1f),
                    Arrangement.End
                ) {
                    ListItemPicker(
                        "от", listOf("от"),
                        Modifier.weight(1f),
                        align = Alignment.CenterEnd,
                        horizontalMargin = 2.dp
                    ) {}
                    ListItemPicker(
                        from.ifBlank {
                            ageList.first()
                        }, ageList, Modifier.weight(1f),
                        align = Alignment.CenterStart,
                        horizontalMargin = 2.dp,
                        doublePlaceHolders = true
                    ) { fromChange?.let { c -> c(it) } }
                }
                Row(
                    Modifier.weight(1f),
                    Arrangement.Start
                ) {
                    ListItemPicker(
                        "до", listOf("до"),
                        Modifier.weight(1f),
                        align = Alignment.CenterEnd,
                        horizontalMargin = 2.dp
                    ) {}
                    ListItemPicker(
                        to.ifBlank {
                            ageList.first()
                        }, ageList, Modifier.weight(2f),
                        align = Alignment.CenterStart,
                        doublePlaceHolders = true,
                        horizontalMargin = 2.dp
                    ) { toChange?.let { c -> c(it) } }
                }
            }
        }
    }
}

@Composable
fun SelectBottom(
    title: String,
    list: List<String>,
    select: Int?,
    online: Boolean,
    onItemClick: ((Int) -> Unit)? = null,
) {
    Column(Modifier.padding(16.dp)) {
        Text(
            title, Modifier.padding(top = 12.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
    }
    Column(Modifier.padding(horizontal = 16.dp)) {
        FlowLayout(
            Modifier
                .background(
                    colorScheme.primaryContainer,
                    shapes.large
                )
                .padding(top = 16.dp, bottom = 4.dp)
                .padding(horizontal = 16.dp),
            12.dp, 12.dp
        ) {
            repeat(list.size) {
                GChip(
                    Modifier, list[it],
                    (select == it), online, (false)
                ) { onItemClick?.let { c -> c(it) } }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}