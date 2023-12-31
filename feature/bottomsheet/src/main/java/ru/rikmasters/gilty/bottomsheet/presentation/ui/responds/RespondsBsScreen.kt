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
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModelWithPhotos
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
    // Список откликов с пагинацией
    val sentResponds = vm.sentResponds.collectAsLazyPagingItems()
    val receivedResponds: LazyPagingItems<MeetWithRespondsModelWithPhotos> = vm.receivedResponds.collectAsLazyPagingItems()
    
    val groupsStates by vm.groupsStates.collectAsState()
    val tabs by vm.tabs.collectAsState()
    
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.updateMeetId(meetId)
        if(!full) vm.selectTab(1)
    }
    
    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsList(
            type = meetId?.let { MEET }
                ?: if(full) FULL else SHORT,
            responds = respondsList,
            sentResponds = sentResponds,
            receivedResponds = receivedResponds,
            respondsStates = groupsStates,
            photoViewState = photoViewState,
            viewerImages = viewerImages,
            viewerSelectImage = viewerSelectImage,
            scope = scope,
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
                        if(tabs == 0) vm.cancelRespond(respondId)
                        else vm.deleteRespond(respondId)
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
