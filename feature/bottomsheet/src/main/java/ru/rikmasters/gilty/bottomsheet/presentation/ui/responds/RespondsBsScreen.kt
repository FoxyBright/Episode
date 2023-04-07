package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.FULL
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.SHORT
import ru.rikmasters.gilty.bottomsheet.viewmodel.RespondsBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.RespondsListCallback

@Composable
fun RespondsBs(
    full: Boolean,
    meetId: String? = null,
    vm: RespondsBsViewModel,
    nav: NavHostController
) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val globalNav = get<NavState>()

    // Список встреч с пагинацией
    val respondsList = vm.responds.collectAsLazyPagingItems()
    // Список откликов с пагинацией
    val meetRespondsList = vm.meetResponds.collectAsLazyPagingItems()

    val groupsStates by vm.groupsStates.collectAsState()
    val tabs by vm.tabs.collectAsState()

    LaunchedEffect(Unit) { vm.updateMeetId(meetId) }

    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsList(
            type = meetId?.let { MEET }
                ?: if (full) FULL else SHORT,
            responds = respondsList,
            meetResponds = meetRespondsList,
            respondsStates = groupsStates,
            selectTab = tabs,
            Modifier,
            object : RespondsListCallback {

                override fun onRespondClick(authorId: String) {
                    nav.navigate("USER?user=$authorId&meet=$meetId")
                }

                override fun onImageClick(image: String) {
                    scope.launch {
                        globalNav.navigateAbsolute(
                            ("profile/avatar?type=2&image=$image")
                        )
                        asm.bottomSheet.collapse()
                    }
                }

                override fun onCancelClick(respondId: String) {
                    scope.launch {
                        if (tabs == 0) {
                            vm.cancelRespond(respondId)
                        } else {
                            vm.deleteRespond(respondId)
                        }
                        vm.updateMeetId(meetId)
                    }
                }

                override fun onAcceptClick(respondId: String) {
                    scope.launch {
                        vm.acceptRespond(respondId)
                        vm.updateMeetId(meetId)
                    }
                }

                override fun onArrowClick(index: Int) {
                    scope.launch { vm.selectRespondsGroup(index) }
                }

                override fun onTabChange(tab: Int) {
                    scope.launch { vm.selectTab(tab) }
                }

                override fun onBack() {
                    nav.popBackStack()
                }
            }
        )
    }
}
