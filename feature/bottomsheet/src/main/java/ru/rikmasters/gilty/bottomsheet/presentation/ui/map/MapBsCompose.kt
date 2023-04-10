package ru.rikmasters.gilty.bottomsheet.presentation.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun SearchPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MapBottomSheet(
                MapState(
                    listOf(
                        Pair("ул. Большая Дмитровская, д 13, Москва", "Kaif Provance"),
                        Pair("ул. Ленина, д 117, Москва", "Eifel Burbershop"),
                        Pair("ул. Льва Толстого, д 18, Калуга", "Дом Мудрых")
                    ), "", false
                )
            )
        }
    }
}

data class MapState(
    val lastPlaces: List<Pair<String, String>>,
    val text: String,
    val online: Boolean,
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
    callback: MapCallback? = null,
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
    callback: MapCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        Search(state.text, Modifier.padding(bottom = 20.dp),
            state.online, { callback?.onChange(it) },
            { callback?.onMapClick() })
        { callback?.onBack() }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
        ) {
            itemsIndexed(state.lastPlaces) { index, it ->
                Item(
                    it, Modifier,
                    lazyItemsShapes(index, state.lastPlaces.size)
                ) { callback?.onItemClick(it) }
                if(index < state.lastPlaces.size.minus(1))
                    GDivider(Modifier.padding(start = 16.dp))
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
    onClick: () -> Unit,
) {
    Card(
        onClick, modifier.fillMaxWidth(),
        (true), shape,
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
    online: Boolean,
    onChange: ((String) -> Unit)? = null,
    onMapClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    Card(
        modifier, shapes.large,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(Modifier.fillMaxWidth(), Start, CenterVertically) {
            IconButton({ if(text.isNotBlank()) onBack?.let { it() } }) {
                Icon(
                    painterResource(
                        if(text.isNotBlank()) R.drawable.ic_back
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
                colors = placeSearchColors(online),
                keyboardOptions = Default.copy(
                    imeAction = Done, keyboardType = Text,
                    capitalization = Sentences
                ),
                singleLine = true,
                placeholder = textFieldLabel(
                    (false), stringResource(R.string.search_placeholder)
                ),
                textStyle = typography.bodyMedium.copy(fontWeight = Bold),
            )
            if(text.isBlank()) IconButton(
                { onMapClick?.let { it() } }) {
                Icon(
                    painterResource(R.drawable.ic_map),
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}

fun onNull(text: String): String =
    if(text.isNotEmpty()
        && text.first() == '0'
    ) text.substring(1, text.length)
    else text

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun placeSearchColors(online: Boolean) =
    TextFieldDefaults.textFieldColors(
        textColor = colorScheme.tertiary,
        cursorColor = if(online) colorScheme.secondary
        else colorScheme.primary,
        containerColor = colorScheme.primaryContainer,
        unfocusedLabelColor = colorScheme.onTertiary,
        disabledLabelColor = colorScheme.onTertiary,
        focusedLabelColor = colorScheme.tertiary,
        disabledTrailingIconColor = Color.Transparent,
        focusedTrailingIconColor = Color.Transparent,
        unfocusedTrailingIconColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        placeholderColor = colorScheme.onTertiary,
        disabledPlaceholderColor = Color.Transparent,
    )