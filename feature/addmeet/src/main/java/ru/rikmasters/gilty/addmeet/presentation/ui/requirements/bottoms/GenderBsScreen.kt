package ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.SelectBottom
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.GenderBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R

@Composable
fun GenderBs(vm: GenderBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val genders by vm.genders.collectAsState()
    val select by vm.select.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getGenders()
    }
    
    SelectBottom(
        stringResource(R.string.sex),
        genders, select, Online
    ) {
        scope.launch {
            vm.selectGender(it)
            asm.bottomSheet.collapse()
        }
    }
}