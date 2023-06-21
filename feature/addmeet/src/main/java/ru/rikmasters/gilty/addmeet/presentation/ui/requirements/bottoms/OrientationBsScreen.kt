package ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.SelectBottom
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.OrientationBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R

@Composable
fun OrientationBs(vm: OrientationBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val orientations by vm.orientations.collectAsState()
    val select by vm.select.collectAsState()
    val online by vm.online.collectAsState()
    
    LaunchedEffect(Unit) { vm.getOrientations() }

    BackHandler {
        scope.launch {
            asm.bottomSheet.collapse()
        }
    }

    SelectBottom(
        stringResource(R.string.orientation_title),
        orientations.map { it.name },
        orientations.indexOf(select), online
    ) {
        scope.launch {
            vm.selectOrientation(it)
            asm.bottomSheet.collapse()
        }
    }
}