package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.FULL
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.SHORT
import ru.rikmasters.gilty.bottomsheet.viewmodel.RespondsBsViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.RespondsListCallback
import ru.rikmasters.gilty.shared.model.notification.RespondWithPhotos
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@Composable
fun RespondsBs(
    full: Boolean,
    meetId: String? = null,
    vm: RespondsBsViewModel,
    nav: NavHostController,
) {
    val scope = rememberCoroutineScope()
    
    // Список встреч с пагинацией
    val respondsList: LazyPagingItems<RespondWithPhotos> = vm.responds.collectAsLazyPagingItems()
    val localResponds = vm.localResponds.collectAsState()

    // Список отправленных откликов с пагинацией
    val sentResponds = vm.sentResponds.collectAsLazyPagingItems()
    val localSentResponds = vm.localSentResponds.collectAsState()

    // Список полученных откликов с пагинацией
    val receivedResponds = vm.receivedResponds.collectAsLazyPagingItems()
    val localReceivedResponds = vm.localReceivedResponds.collectAsState()


    val groupsStates by vm.groupsStates.collectAsState()
    val tabs by vm.tabs.collectAsState()
    
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.updateMeetId(meetId)
        if(!full) vm.selectTab(1)
    }

    LaunchedEffect(key1 = respondsList.itemSnapshotList.items, block = {
        vm.setLocalResponds(respondsList.itemSnapshotList.items)
    })

    LaunchedEffect(key1 = sentResponds.itemSnapshotList.items, block = {
        vm.setLocalSentResponds(sentResponds.itemSnapshotList.items)
    })

    LaunchedEffect(key1 = receivedResponds.itemSnapshotList.items, block = {
        vm.setLocalReceivedResponds(sentResponds.itemSnapshotList.items)
    })

    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsList(
            state = RespondsListState(type = meetId?.let { MEET }
                ?: if(full) FULL else SHORT,
                responds = respondsList,
                localResponds = localResponds.value,
                sentResponds = sentResponds,
                localSentResponds = localSentResponds.value,
                receivedResponds = receivedResponds,
                localReceivedResponds = localReceivedResponds.value,
                respondsStates = groupsStates,
                photoViewState = photoViewState,
                viewerImages = viewerImages,
                viewerSelectImage = viewerSelectImage,
                scope = scope,
            ),
            callback = object: RespondsListCallback {
                
                override fun onPhotoViewDismiss(state: Boolean) {
                    scope.launch { vm.changePhotoViewState(state) }
                }
                
                override fun onRespondClick(
                    authorId: String,
                    meetId: String,
                ) {
                    if(tabs == 0) nav.navigate(
                        "MEET?meet=$meetId"
                    )
                    else nav.navigate(
                        "USER?user=$authorId&meet=$meetId"
                    )
                }
                
                override fun onImageClick(image: AvatarModel) {
                    scope.launch {
                        vm.setPhotoViewSelected(image)
                        vm.setPhotoViewImages(listOf(image))
                        vm.changePhotoViewState(true)
                    }
                }
                
                override fun onCancelClick(respondId: String) {
                    scope.launch {
                        if(tabs == 0) {
                            vm.cancelRespond(respondId)
                            sentResponds.refresh()
                        }
                        else {
                            vm.deleteRespond(respondId)
                            receivedResponds.refresh()
                        }
                        vm.updateMeetId(meetId)
                    }
                }
                
                override fun onAcceptClick(respondId: String) {
                    scope.launch {
                        vm.acceptRespond(respondId)
                        receivedResponds.refresh()
                        vm.updateMeetId(meetId)
                        vm.selectTab(1)
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
