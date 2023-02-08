package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.BsContainer
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun IconsBsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            IconsBsContent(
                IconsBsState(
                    listOf(
                        Pair(
                            R.drawable.ic_logo_dark,
                            stringResource(R.string.settings_dark_icon_label)
                        ),
                        Pair(
                            R.drawable.ic_logo_white,
                            stringResource(R.string.settings_white_icon_label)
                        ),
                    ), (1)
                )
            )
        }
    }
}

data class IconsBsState(
    val icons: List<Pair<Int, String>>,
    val selected: Int,
)

interface IconsBsCallback {
    
    fun onItemClick(icon: Int)
}

@Composable
fun IconsBsContent(
    state: IconsBsState,
    modifier: Modifier = Modifier,
    callback: IconsBsCallback? = null,
) {
    BsContainer(
        stringResource(R.string.settings_app_icon_label),
        modifier
    ) {
        LazyRow(Modifier) {
            itemsIndexed(state.icons) { index, icon ->
                IconItem(
                    Modifier.padding(end = 16.dp),
                    icon.first, icon.second,
                    (index == state.selected)
                ) { callback?.onItemClick(index) }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun IconItem(
    modifier: Modifier = Modifier,
    image: Int, label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick, Modifier,
        shape = shapes.large,
        colors = cardColors(Transparent)
    ) {
        Column(modifier, Top, CenterHorizontally) {
            Image(
                painterResource(image),
                (null), Modifier.size(70.dp)
            )
            Text(
                label, Modifier
                    .padding(top = 16.dp),
                if(selected) colorScheme.primary
                else colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = Bold
            )
        }
    }
}