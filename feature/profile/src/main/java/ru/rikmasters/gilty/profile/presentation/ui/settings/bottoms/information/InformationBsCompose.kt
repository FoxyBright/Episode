package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.information

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.BsContainer
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.lazyItemsShapes
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun InformationPreview() {
    GiltyTheme {
        InformationBsContent(
            InformationBsState(
                listOf(
                    stringResource(R.string.settings_about_app_privacy_policy),
                    stringResource(R.string.settings_about_app_agreement),
                    stringResource(R.string.settings_about_app_rules),
                    stringResource(R.string.settings_about_app_help)
                )
            )
        )
    }
}

data class InformationBsState(
    val list: List<String>,
)

interface InformationBsCallback {
    
    fun onItemClick(item: Int)
}

@Composable
fun InformationBsContent(
    state: InformationBsState,
    modifier: Modifier = Modifier,
    callback: InformationBsCallback? = null,
) {
    BsContainer(
        stringResource(R.string.settings_about_app_label),
        modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            LazyColumn {
                itemsIndexed(state.list) { index, item ->
                    InformationItem(
                        item, lazyItemsShapes(
                            index, state.list.size
                        )
                    ) { callback?.onItemClick(index) }
                    if(index < state.list.size - 1) Divider(
                        Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InformationItem(
    text: String,
    shape: Shape,
    onItemClick: (() -> Unit)? = null,
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