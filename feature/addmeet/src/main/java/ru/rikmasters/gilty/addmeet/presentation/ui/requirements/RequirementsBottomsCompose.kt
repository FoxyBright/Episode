package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun GenderPreview() {
    GiltyTheme {
        SelectBottom(
            stringResource(R.string.sex), listOf(
                stringResource(R.string.female_sex),
                stringResource(R.string.male_sex),
                stringResource(R.string.others_sex)
            ), listOf(false, false, true), false
        )
    }
}

@Preview
@Composable
fun OrientationPreview() {
    GiltyTheme {
        SelectBottom(
            stringResource(R.string.orientation_title), listOf(
                stringResource(R.string.orientation_hetero),
                stringResource(R.string.orientation_gay),
                stringResource(R.string.orientation_lesbian),
                stringResource(R.string.orientation_bisexual),
                stringResource(R.string.orientation_asexual),
                stringResource(R.string.orientation_demisexual),
                stringResource(R.string.orientation_pansexual),
                stringResource(R.string.orientation_queer)
            ), listOf(
                false, false, false, false,
                false, false, false, true
            ), false
        )
    }
}

@Preview
@Composable
fun AgePreview() {
    GiltyTheme {
        AgeBottom("19", "40", Modifier,
            {}, {}, false
        ) {}
    }
}

@Composable
fun SelectBottom(
    title: String,
    genderList: List<String>,
    list: List<Boolean>,
    online: Boolean,
    onItemClick: ((Int) -> Unit)? = null
) {
    BottomContainer(title) {
        FlowLayout(
            Modifier
                .background(
                    colorScheme.primaryContainer,
                    shapes.large
                )
                .padding(8.dp)
        ) {
            list.forEachIndexed { index, it ->
                GiltyChip(
                    Modifier.padding(end = 12.dp),
                    genderList[index], it, online
                ) { onItemClick?.let { c -> c(index) } }
            }
        }
    }
}

@Composable
fun AgeBottom(
    from: String,
    to: String,
    modifier: Modifier = Modifier,
    fromChange: (String) -> Unit,
    toChange: (String) -> Unit,
    online: Boolean,
    onSave: () -> Unit
) {
    BottomContainer(stringResource(R.string.personal_info_age_placeholder)) {
        Column {
            Box(
                modifier.background(colorScheme.background),
                Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    Center, CenterVertically
                ) {
                    DivText("от")
                    ListItemPicker(
                        from, listGenerator(), Modifier
                    ) { fromChange(it) }
                    DivText("до")
                    ListItemPicker(
                        to, listGenerator(), Modifier
                    ) { toChange(it) }
                }
            }
            GradientButton(
                Modifier
                    .padding(top = 20.dp),
                stringResource(R.string.save_button),
                online = online
            ) { onSave() }
        }
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

@Composable
private fun DivText(text: String) {
    Column(horizontalAlignment = Alignment.End) {
        Divider(Modifier.width(22.dp), 2.dp, colorScheme.outline)
        Text(
            text, Modifier.padding(vertical = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium
        )
        Divider(Modifier.width(22.dp), 2.dp, colorScheme.outline)
    }
}

private fun listGenerator(
    range: IntRange = 18..100
): List<String> {
    val list = arrayListOf<String>()
    range.forEach { list.add(it.toString()) }
    return list
}