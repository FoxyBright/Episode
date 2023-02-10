package ru.rikmasters.gilty.profile.presentation.ui.bottoms.responds

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import ru.rikmasters.gilty.profile.viewmodel.bottoms.RespondsBsViewModel

@Composable
fun RespondsBs(vm: RespondsBsViewModel) {
    
    val tabs by vm.tabs.collectAsState()
    val groupStates by vm.groupStates.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
//    val respondsList = list
//
//    val pairRespondsList =
//        remember { mutableStateOf(Pair(DemoMeetingModel, respondsList)) }
//
//    RespondsList(
//        listOf(pairRespondsList.value),
//        groupStates, tabs,
//        Modifier, object: RespondsListCallback {
//            override fun onCancelClick(respond: ShortRespondModel) {
//                respondsList.remove(respond)
//                Toast.makeText(
//                    context,
//                    "Вы отказались от встречи",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onRespondClick(meet: MeetingModel) {
//                Toast.makeText(
//                    context,
//                    "Вы нажали на встречу",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onAcceptClick(respond: ShortRespondModel) {
//                respondsList.remove(respond)
//                Toast.makeText(
//                    context,
//                    "Вы приняли встречу",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onImageClick(image: AvatarModel) {
//                Toast.makeText(
//                    context,
//                    "Фото смотреть нельзя",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onTabChange(tab: Int) {
//                scope.launch { vm.selectTab(tab) }
//            }
//
//            override fun onArrowClick(index: Int) {
//                scope.launch { vm.selectRespondGroup(index) }
//            }
//        })
}