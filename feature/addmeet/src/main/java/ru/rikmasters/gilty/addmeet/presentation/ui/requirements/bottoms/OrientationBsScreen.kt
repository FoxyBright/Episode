package ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.SelectBottom
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.OrientationBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R

@Composable
fun OrientationBs(vm: OrientationBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val orientations by vm.orientations.collectAsState()
    val select by vm.select.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getOrientations()
    }
    
    fun getSelect() = orientations.indexOf(select)
    
    SelectBottom(
        stringResource(R.string.orientation_title),
        orientations.map { it.name },
        getSelect(), Online
    ) {
        scope.launch {
            vm.selectOrientation(it)
            asm.bottomSheet.collapse()
        }
    }
}