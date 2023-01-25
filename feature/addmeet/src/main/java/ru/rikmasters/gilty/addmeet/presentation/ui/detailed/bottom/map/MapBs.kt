package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.MapBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel

@Composable
fun MapBs(vm: MapBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val last by vm.last.collectAsState()
    val placeSearch by vm.search.collectAsState()
    
    LaunchedEffect(Unit){
        vm.getLastPlaces()
    }
    
    MapBottomSheet(
        MapState(last, placeSearch, Online),
        Modifier, object: MapCallback {
            override fun onChange(text: String) {
                scope.launch { vm.changeSearch(text) }
            }
            
            override fun onMapClick() {
                scope.launch { vm.onMapClick() }
            }
            
            override fun onItemClick(place: Pair<String, String>) {
                scope.launch {
                    vm.selectPlace(place)
                    asm.bottomSheet.collapse()
                }
            }
            
            override fun onBack() {
                scope.launch { asm.bottomSheet.collapse() }
            }
        }
    )
}