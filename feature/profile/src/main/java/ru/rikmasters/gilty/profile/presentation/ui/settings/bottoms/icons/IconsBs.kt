package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.icons

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.IconsBsViewModel
import ru.rikmasters.gilty.shared.R

@Composable
fun IconsBs(vm: IconsBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selected by vm.selected.collectAsState()
    
    val list = listOf(
        Pair(
            R.drawable.ic_logo_dark,
            stringResource(R.string.settings_dark_icon_label)
        ),
        Pair(
            R.drawable.ic_logo_white,
            stringResource(R.string.settings_white_icon_label)
        ),
    )
    
    IconsBsContent(
        IconsBsState(list, selected),
        Modifier, object: IconsBsCallback {
            override fun onItemClick(icon: Int) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    vm.selectIcon(icon)
                }
            }
        }
    )
}