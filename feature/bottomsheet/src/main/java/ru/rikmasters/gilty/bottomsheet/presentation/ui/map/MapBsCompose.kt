package ru.rikmasters.gilty.bottomsheet.presentation.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
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
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.meeting.DemoLocationModel
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
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
                    listOf(DemoLocationModel),
                    "", false
                )
            )
        }
    }
}

data class MapState(
    val lastPlaces: List<LocationModel>,
    val text: String,
    val online: Boolean,
)

interface MapCallback {
    
    fun onChange(text: String) {}
    fun onMapClick() {}
    fun onItemClick(place: LocationModel) {}
    fun onBack() {}
}

@Composable
fun MapBottomSheet(
    state: MapState,
    modifier: Modifier = Modifier,
    callback: MapCallback? = null,
) {
    Element(
        item = FilterModel(
            stringResource(
                R.string.add_meet_detailed_meet_place
            )
        ) {
            Content(
                state = state,
                modifier = modifier,
                callback = callback
            )
        },
        modifier = Modifier
            .padding(vertical = 28.dp)
    )
}

@Composable
private fun Content(
    state: MapState,
    modifier: Modifier = Modifier,
    callback: MapCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        Search(
            text = state.text,
            modifier = Modifier
                .padding(bottom = 20.dp),
            online = state.online,
            onChange = {
                callback?.onChange(it)
            },
            onMapClick = {
                callback?.onMapClick()
            }
        ) { callback?.onBack() }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .background(
                    color = colorScheme
                        .primaryContainer,
                    shape = shapes.medium
                )
        ) {
            itemsIndexed(
                items = state.lastPlaces
            ) { index, it ->
                Item(
                    location = it,
                    modifier = Modifier,
                    shape = lazyItemsShapes(
                        index = index,
                        size = state.lastPlaces.size
                    )
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
    location: LocationModel,
    modifier: Modifier,
    shape: Shape,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        enabled = true,
        shape = shape,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            SpaceBetween, CenterVertically
        ) {
            Column {
                if((location.address ?: "") != "")
                    Text(
                        text = location.address ?: "",
                        color = colorScheme.onTertiary,
                        style = typography.headlineSmall
                    )
                if((location.place ?: "") != "")
                    Text(
                        text = location.place ?: "",
                        color = colorScheme.tertiary,
                        style = typography.bodyMedium
                    )
            }
            Icon(
                imageVector = Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = colorScheme.scrim
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
        modifier = modifier,
        shape = shapes.medium,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            Start, CenterVertically
        ) {
            val style =
                typography.bodyMedium.copy(
                    fontSize = 17.dp.toSp(),
                    fontWeight = Bold
                )
            Icon(
                painter = painterResource(
                    if(text.isNotBlank())
                        R.drawable.ic_back
                    else R.drawable.magnifier
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        if(text.isNotBlank())
                            onBack?.let { it() }
                    },
                tint = colorScheme.onTertiary
            )
            GTextField(
                value = text,
                onValueChange = {
                    onChange?.let { text ->
                        text(onNull(it))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                shape = shapes.large,
                colors = placeSearchColors(online),
                keyboardOptions = Default.copy(
                    imeAction = Done,
                    keyboardType = Text,
                    capitalization = Sentences
                ),
                singleLine = true,
                placeholder = textFieldLabel(
                    label = false,
                    text = stringResource(R.string.search_placeholder),
                    holderFont = style.copy(
                        colorScheme.onTertiary
                    )
                ),
                textStyle = style,
            )
            if(text.isBlank()) Icon(
                painter = painterResource(
                    R.drawable.ic_map
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onMapClick?.let { it() }
                    },
                tint = colorScheme.onTertiary
            )
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
        cursorColor = if(online)
            colorScheme.secondary
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