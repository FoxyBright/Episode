package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.selector

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.GenderBsViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

@Composable
fun GenderBs(vm: GenderBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val genders = GenderType.fullList.map { it.value }
    val selected by vm.selected.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getGenders()
    }
    
    SelectorBsContent(
        SelectorBsState(
            stringResource(R.string.sex),
            genders, selected
        ), Modifier,
        object: SelectorBsCallback {
            override fun onItemSelect(item: Int) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    vm.selectGender(item)
                }
            }
        }
    )
}