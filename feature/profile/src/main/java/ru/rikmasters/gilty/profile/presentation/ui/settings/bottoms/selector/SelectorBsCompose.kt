package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.BsContainer
import ru.rikmasters.gilty.shared.shared.GChip
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun SelectorBsPreview() {
    GiltyTheme {
        SelectorBsContent(
            SelectorBsState(
                ("Заголовок"),
                listOf("один", "два", "три"),
                (1)
            )
        )
    }
}

data class SelectorBsState(
    val title: String,
    val items: List<String>,
    val selected: Int?,
)

interface SelectorBsCallback {
    
    fun onItemSelect(item: Int)
}

@Composable
fun SelectorBsContent(
    state: SelectorBsState,
    modifier: Modifier = Modifier,
    callback: SelectorBsCallback? = null,
) {
    BsContainer(
        state.title, modifier
    ) {
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
            state.items.forEachIndexed { index, item ->
                GChip(
                    Modifier, item,
                    (state.selected == index)
                ) { callback?.onItemSelect(index) }
            }
        }
    }
}