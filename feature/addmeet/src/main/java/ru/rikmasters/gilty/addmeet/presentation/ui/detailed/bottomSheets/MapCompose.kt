package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.shared.Element
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.onNull
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private val placeList =
    listOf(
        Pair("ул. Большая Дмитровская, д 13, Москва", "Kaif Provance"),
        Pair("ул. Ленина, д 117, Москва", "Eifel Burbershop"),
        Pair("ул. Льва Толстого, д 18, Калуга", "Дом Мудрых")
    )

@Preview
@Composable
private fun SearchPreview() {
    GiltyTheme {
        MapBottomSheet(MapState(""))
    }
}

data class MapState(
    val text: String
)

interface MapCallback {
    fun onChange(text: String) {}
    fun onMapClick() {}
    fun onItemClick(place: Pair<String, String>) {}
    fun onBack() {}
}

@Composable
fun MapBottomSheet(
    state: MapState,
    modifier: Modifier = Modifier,
    callback: MapCallback? = null
) {
    Element(
        FilterModel(stringResource(R.string.add_meet_detailed_meet_place)) {
            Content(state, modifier, callback)
        }, Modifier.padding(vertical = 28.dp)
    )
}

@Composable
private fun Content(
    state: MapState,
    modifier: Modifier = Modifier,
    callback: MapCallback? = null
) {
    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Search(state.text, Modifier.padding(bottom = 20.dp),
            { callback?.onChange(it) },
            { callback?.onMapClick() })
        { callback?.onBack() }
        LazyColumn(Modifier.fillMaxWidth()) {
            itemsIndexed(placeList) { index, it ->
                Item(
                    it, Modifier,
                    LazyItemsShapes(index, placeList.size)
                ) { callback?.onItemClick(it) }
                if (index < placeList.size.minus(1))
                    Divider(Modifier.padding(start = 16.dp))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Item(
    text: Pair<String, String>,
    modifier: Modifier,
    shape: Shape,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier.fillMaxWidth(), (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            SpaceBetween, CenterVertically
        ) {
            Column {
                Text(
                    text.first, Modifier,
                    colorScheme.onTertiary,
                    style = typography.headlineSmall
                )
                Text(
                    text.second, Modifier,
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
            }
            Icon(
                Icons.Filled.KeyboardArrowRight,
                (null), Modifier.size(20.dp),
                colorScheme.onTertiary
            )
        }
    }
}

@Composable
private fun Search(
    text: String,
    modifier: Modifier = Modifier,
    onChange: ((String) -> Unit)? = null,
    onMapClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    Card(
        modifier, shapes.large,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(Modifier.fillMaxWidth(), Start, CenterVertically) {
            IconButton({ if (text.isNotBlank()) onBack?.let { it() } }) {
                Icon(
                    painterResource(
                        if (text.isNotBlank()) R.drawable.ic_back
                        else R.drawable.magnifier
                    ), (null), Modifier, colorScheme.onTertiary
                )
            }
            GTextField(
                text, { onChange?.let { text -> text(onNull(it)) } },
                Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .weight(1f),
                shape = shapes.large,
                colors = TextFieldColors(),
                placeholder = TextFieldLabel(
                    (false), stringResource(R.string.login_search_placeholder)
                ),
                textStyle = typography.bodyMedium.copy(fontWeight = Bold),
            )
            if (text.isBlank()) IconButton(
                { onMapClick?.let { it() } }) {
                Icon(
                    painterResource(R.drawable.ic_map),
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}