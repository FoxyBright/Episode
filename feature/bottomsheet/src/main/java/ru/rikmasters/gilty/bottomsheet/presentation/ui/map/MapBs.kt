package ru.rikmasters.gilty.bottomsheet.presentation.ui.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.viewmodel.MapBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

@Composable
fun MapBs(
    vm: MapBsViewModel,
    nav: NavHostController,
    category: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val last by vm.last.collectAsState()
    val placeSearch by vm.search.collectAsState()
    
    LaunchedEffect(Unit) { vm.getLastPlaces() }
    
    MapBottomSheet(
        MapState(last, placeSearch, (false)),
        
        Modifier, object: MapCallback {
            override fun onChange(text: String) {
                scope.launch { vm.changeSearch(text) }
            }
            
            override fun onMapClick() {
                scope.launch {
                    val location = mapper
                        .writeValueAsString(
                            LocationModel(
                                (false), (55.112020),
                                (36.586459), (""), ("")
                            )
                        )
                    nav.navigate("MAP?location=$location&category=$category")
                }
            }
            
            override fun onItemClick(place: Pair<String, String>) {
                scope.launch {
                    vm.selectPlace(place)
                    asm.bottomSheet.collapse()
                }
            }
            
            override fun onBack() {
                nav.popBackStack()
            }
        }
    )
}