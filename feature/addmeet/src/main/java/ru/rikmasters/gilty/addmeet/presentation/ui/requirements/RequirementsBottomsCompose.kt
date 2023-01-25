package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*
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
            ), (1), false
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
            ), (2), false
        )
    }
}

@Preview
@Composable
fun AgePreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background,
                RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp
                )
            )
        ) { AgeBottom("19", "40") }
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
    BottomContainer(title) {
        FlowLayout(
            Modifier
                .background(
                    colorScheme.primaryContainer,
                    shapes.large
                )
                .padding(8.dp), 8.dp
        ) {
            repeat(list.size) {
                GiltyChip(
                    Modifier.padding(end = 12.dp),
                    list[it], (select == it), online
                ) { onItemClick?.let { c -> c(it) } }
            }
        }
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
                    DivText(stringResource(R.string.age_from))
                    ListItemPicker(
                        from, listGenerator(), Modifier
                    ) { f -> fromChange?.let { it(f) } }
                    DivText(stringResource(R.string.age_to))
                    ListItemPicker(
                        to, listGenerator(), Modifier
                    ) { t -> toChange?.let { it(t) } }
                }
            }
            GradientButton(
                Modifier.padding(top = 20.dp),
                stringResource(R.string.save_button),
                online = online
            ) { onSave?.let { it() } }
        }
    }
}

@Composable
private fun BottomContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Element(
        FilterModel(title) {
            Box {
                Box(Modifier.padding(bottom = 40.dp)) {
                    content()
                }
            }
        }, modifier.padding(16.dp, 28.dp)
    )
}

@Composable
private fun DivText(text: String) {
    Column(Modifier, Top, End) {
        Div(); Text(
        text, Modifier.padding(vertical = 8.dp),
        colorScheme.tertiary,
        style = typography.displayLarge
    ); Div()
    }
}

@Composable
private fun Div() {
    Divider(
        Modifier.width(30.dp),
        2.dp, colorScheme.outline
    )
}

private fun listGenerator(
    range: IntRange = 18..99,
): List<String> {
    val list = arrayListOf<String>()
    range.forEach { list.add(it.toString()) }
    return list
}