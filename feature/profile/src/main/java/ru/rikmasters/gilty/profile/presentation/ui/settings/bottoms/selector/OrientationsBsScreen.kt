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
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

@Composable
fun OrientationsBs(
    vm: OrientationBsViewModel,
    orientation: OrientationModel?,
    orientationList: List<OrientationModel>?,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selected by vm.selected.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.setSelected(orientation)
    }
    
    Use<OrientationBsViewModel>(LoadingTrait) {
        SelectorBsContent(
            SelectorBsState(
                stringResource(R.string.orientation_title),
                (orientationList?.map { it.name }
                    ?: emptyList()),
                orientationList?.indexOf(selected)
            ), Modifier, object: SelectorBsCallback {
                override fun onItemSelect(item: Int) {
                    scope.launch {
                        asm.bottomSheet.collapse()
                        vm.selectOrientation(
                            orientationList?.get(item)
                        )
                    }
                }
            }
        )
    }
}