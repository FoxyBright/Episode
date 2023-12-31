package ru.rikmasters.gilty.yandexmap.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.web.openInWeb
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.bottomsheet.*
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.yandexmap.model.MeetPlaceModel
import ru.rikmasters.gilty.yandexmap.ymap.MapContent


data class YandexMapState(
    val mapKit: MapKit,
    val location: LocationModel? = null,
    val userVisible: Boolean = false,
    val address: String? = null,
    val place: String? = null,
    val categoryName: String? = null,
    val isSearching: Boolean = false,
    val currentPoint: Point? = null,
    val subBsState: BottomSheetScaffoldState,
    val appBsState: BottomSheetScaffoldState,
)

interface YandexMapCallback {
    
    fun onBack()
    fun onMarkerClick(meetPlaceModel: MeetPlaceModel)
    fun onIsSearchingChange(value: Boolean)
    fun onCameraChange(point: Point)
    fun subBsExpandState(state: Boolean)
    fun appBsExpandState(state: Boolean)
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun YandexMapContent(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    callback: YandexMapCallback? = null,
) {
    BottomSheetScaffold(
        scaffoldState = state.appBsState,
        sheetContent = {
            val loc = state.location
            loc?.lat?.let { lat ->
                loc.lng?.let { lng ->
                    AppBs(lat to lng)
                }
            }
        },
        sheetBackgroundColor = Transparent,
        sheetPeekHeight = 0.dp
    ) {
        BottomSheetScaffold(
            scaffoldState = state.subBsState,
            sheetContent = {
                Bottom(state) {
                    callback?.appBsExpandState(
                        state = false
                    )
                }
            },
            sheetBackgroundColor = Transparent,
            sheetPeekHeight = 80.dp
        ) {
            Column(modifier.fillMaxSize()) {
                TopBar(Modifier) { callback?.onBack() }
                MapContent(
                    state = state,
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(y = 24.dp),
                    callback = callback
                )
            }
        }
    }
}

@Composable
private fun AppBs(loc: Pair<Double, Double>) {
    var alert by remember {
        mutableStateOf(false)
    }
    var appIndex by remember {
        mutableStateOf(0)
    }
    val scope =
        rememberCoroutineScope()
    
    val googleMap =
        "https://maps.google.com/?daddr=" +
                "${loc.first}%2C$${loc.second}&zoom=18"
    val yandexMap =
        "https://maps.yandex.ru/?pt=" +
                "${loc.second}%2C${loc.first}&z=18"
    
    val context = LocalContext.current
    val asm = get<AppStateModel>()
    MapAppsBs(
        alert = alert,
        appName = appIndex,
        modifier = Modifier.background(
            colorScheme.background
        ),
        alertDismiss = { state ->
            if(state) {
                openInWeb(
                    context = context,
                    uri = if(appIndex == 0)
                        googleMap else yandexMap
                )
                scope.launch {
                    asm.bottomSheet.collapse()
                }
            } else alert = false
        }
    ) { app -> appIndex = app; alert = true }
}

@Composable
private fun Bottom(
    state: YandexMapState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .background(
                colorScheme.background,
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 19.dp
                ), Center
        ) { Grip() }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = if(state.location?.hide == true)
                        35.dp else 0.dp
                ),
            shape = shapes.medium,
            colors = cardColors(
                colorScheme.primaryContainer
            )
        ) {
            if(state.location?.hide == true)
                Text(
                    text = stringResource(
                        R.string.add_meet_detailed_meet_place_place_holder
                    ),
                    modifier = Modifier.padding(16.dp, 12.dp),
                    color = colorScheme.tertiary,
                    style = typography.bodyMedium,
                )
            else Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    state.address ?: "", Modifier.padding(top = 8.dp),
                    colorScheme.onTertiary,
                    style = typography.headlineSmall,
                    maxLines = 1,
                    overflow = Ellipsis
                )
                Text(
                    state.place ?: "", Modifier.padding(
                        top = 2.dp, bottom = 10.dp
                    ), colorScheme.tertiary,
                    style = typography.bodyMedium,
                    maxLines = 1,
                    overflow = Ellipsis
                )
            }
        }
        if(state.location?.hide != true) GradientButton(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            stringResource(
                if(state.categoryName.isNullOrBlank())
                    R.string.map_get_route
                else R.string.select_button
            ),
        ) { onClick() }
    }
}

@Composable
private fun Grip(
    modifier: Modifier = Modifier,
    color: Color = colors.gripColor,
) {
    Box(
        modifier
            .size(40.dp, 5.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun TopBar(
    modifier: Modifier,
    onBack: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(16.dp, top = 18.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Arrangement.Start, CenterVertically
        ) {
            IconButton(
                { onBack() },
                Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    (null), Modifier, colorScheme.tertiary
                )
            }
            Text(
                stringResource(R.string.map),
                Modifier, colorScheme.tertiary,
                style = typography.labelLarge,
            )
        }
    }
}