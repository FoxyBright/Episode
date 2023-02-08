package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.information

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.InformationBsViewModel
import ru.rikmasters.gilty.shared.R

@Composable
fun InformationBs(vm: InformationBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val list = listOf(
        stringResource(R.string.settings_about_app_privacy_policy),
        stringResource(R.string.settings_about_app_agreement),
        stringResource(R.string.settings_about_app_rules),
        stringResource(R.string.settings_about_app_help)
    )
    
    InformationBsContent(
        InformationBsState(list), Modifier,
        object: InformationBsCallback {
            override fun onItemClick(item: Int) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    vm.getInformation(item)
                }
            }
        }
    )
}