package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration.DurationBs
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time.TimeBs
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.DurationBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.LOCATION
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.common.extentions.offset
import ru.rikmasters.gilty.shared.model.meeting.TagModel

@Composable
fun DetailedScreen(vm: DetailedViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val category by vm.category.collectAsState()
    val description by vm.description.collectAsState()
    val place by vm.place.collectAsState()
    val duration by vm.duration.collectAsState()
    val tags by vm.tags.collectAsState()
    val online by vm.online.collectAsState()
    val alert by vm.alert.collectAsState()
    val hide by vm.hide.collectAsState()
    val date by vm.date.collectAsState()
    
    val isActive = date.isNotBlank()
            && duration.isNotBlank()
            && tags.isNotEmpty()
            && if(!online)
        place != null
    else true
    
    DetailedContent(
        DetailedState(
            time = duration,
            date = date.let {
                it.ifEmpty { return@let "" }
                LocalDateTime.ofZ(it)
                    .minusMinute(offset / 6000)
                    .plusDay(1)
                    .format("d MMMM, HH:mm")
            },
            description = description,
            tagList = tags,
            meetPlace = place,
            hideMeetPlace = hide,
            alert = alert,
            online = online,
            isActive = isActive
        ), modifier = Modifier.systemBarsPadding(),callback = object: DetailedCallback {
            
            override fun onDateClick() {
                vm.scope.openBS<TimeBsViewModel>(scope) {
                    TimeBs(it)
                }
            }
            
            override fun onTimeClick() {
                vm.scope.openBS<DurationBsViewModel>(scope) {
                    DurationBs(it)
                }
            }
            
            override fun onMeetPlaceClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(
                            vm.scope, LOCATION,
                            category = category
                        )
                    }
                }
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.changeDescription(text) }
            }
            
            override fun onDescriptionClear() {
                scope.launch { vm.clearDescription() }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onClose() {
                scope.launch {
                    vm.clearAddMeet()
                    nav.clearStackNavigation("main/meetings")
                }
            }
            
            override fun onTagDelete(tag: TagModel) {
                scope.launch { vm.deleteTag(tag) }
            }
            
            override fun onHideMeetPlaceClick() {
                scope.launch { vm.hideMeetPlace() }
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
        }
    )
}