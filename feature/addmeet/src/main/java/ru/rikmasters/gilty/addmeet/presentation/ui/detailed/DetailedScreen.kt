package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration.DurationBs
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.map.MapBs
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time.TimeBs
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.DurationBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.MapBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.shared.model.meeting.TagModel

@Composable
fun DetailedScreen(vm: DetailedViewModel) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val description by vm.description.collectAsState()
    val alert by vm.alert.collectAsState()
    val hideAddress by vm.hideAddress.collectAsState()
    val date by vm.date.collectAsState()
    val duration by vm.duration.collectAsState()
    val tags by vm.tags.collectAsState()
    val place by vm.place.collectAsState()
    
    val isActive = date.isNotBlank()
            && duration.isNotBlank()
            && tags.isNotEmpty()
            && if(!Online)
        place != null
    else true
    
    DetailedContent(
        DetailedState(
            duration, date, description,
            tags, place, hideAddress,
            alert, Online, isActive
        ), Modifier, object: DetailedCallback {
            
            override fun onDateClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<TimeBsViewModel>(vm.scope) {
                            TimeBs(it)
                        }
                    }
                }
            }
            
            override fun onTimeClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<DurationBsViewModel>(vm.scope) {
                            DurationBs(it)
                        }
                    }
                }
            }
            
            override fun onMeetPlaceClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<MapBsViewModel>(vm.scope) {
                            MapBs(it)
                        }
                    }
                }
            }
            
            override fun onTagDelete(tag: TagModel) {
                scope.launch { vm.deleteTag(tag) }
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.changeDescription(text) }
            }
            
            override fun onHideMeetPlaceClick() {
                scope.launch { vm.hideMeetPlace() }
            }
            
            override fun onDescriptionClear() {
                scope.launch { vm.clearDescription() }
            }
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onNext() {
                nav.navigate("requirements")
            }
            
            override fun onTagsClick() {
                nav.navigate("tags")
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
        }
    )
}