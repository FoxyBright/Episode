package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.selector

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.OrientationBsViewModel
import ru.rikmasters.gilty.shared.R

@Composable
fun OrientationsBs(vm: OrientationBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val orientations by vm.orientations.collectAsState()
    val selected by vm.selected.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getOrientations()
    }
    
    Use<OrientationBsViewModel>(LoadingTrait) {
        SelectorBsContent(
            SelectorBsState(
                stringResource(R.string.orientation_title),
                (orientations?.map { it.name }
                    ?: emptyList()), selected
            ), Modifier,
            object: SelectorBsCallback {
                override fun onItemSelect(item: Int) {
                    scope.launch {
                        asm.bottomSheet.collapse()
                        vm.selectOrientation(item)
                    }
                }
            }
        )
    }
}