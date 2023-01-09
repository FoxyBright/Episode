package ru.rikmasters.gilty.profile.presentation.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.lists.*
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun UserProfileScreen(nav: NavState = get()) {
    GiltyTheme {
        val asm = get<AppStateModel>()
        val scope = rememberCoroutineScope()
        var menuState by remember { mutableStateOf(false) }
        val historyState =
            remember { mutableStateOf(false) }
        val meets = DemoMeetingList
        val profileModel = DemoProfileModel
        var description by
        remember { mutableStateOf(profileModel.aboutMe) }
        val state = ProfileState(
            name = "${profileModel.username}, ${profileModel.age}",
            profilePhoto = profileModel.avatar.id,
            hiddenPhoto = profileModel.avatar.id,
            description = description,
            rating = profileModel.rating.average,
            observers = 13500,
            observed = 128,
            emoji = EmojiList.first(),
            profileType = ProfileType.USERPROFILE,
            enabled = false,
        )
        
        var alert by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val stateList = remember {
            mutableStateListOf(INACTIVE, NEW, INACTIVE, NEW, ACTIVE)
        }
        val tabsState = remember {
            mutableStateListOf(true, false)
        }
        val respondsState = remember {
            mutableStateListOf(true, false)
        }
        val respondsList =
            remember {
                mutableStateListOf(
                    DemoReceivedRespondsModel,
                    DemoReceivedRespondsModel,
                    DemoSendRespondsModel,
                    DemoSendRespondsModel,
                    DemoSendRespondsModel
                )
            }
        val observers =
            remember {
                mutableStateListOf(
                    DemoMemberModel,
                    DemoMemberModel,
                    DemoMemberModel,
                    DemoMemberModel
                )
            }
        val observed =
            remember {
                mutableStateListOf(
                    DemoMemberModel,
                    DemoMemberModel,
                    DemoMemberModel,
                    DemoMemberModel
                )
            }
        val pairRespondsList =
            remember { mutableStateOf(Pair(DemoMeetingModel, respondsList)) }
        val tabState = remember { mutableStateListOf(true, false) }
        UserProfile(
            UserProfileState(
                state, meets, meets,
                meets.first(), (respondsList.size),
                historyState.value,
                stateList, alert, menuState
            ), Modifier, object: UserProfileCallback {
                override fun menu(state: Boolean) {
                    nav.navigate("settings")
                }
                
                override fun onNameChange(text: String) {
                }
                
                override fun onDescriptionChange(text: String) {
                    description = text
                }
                
                override fun onObserveClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            ObserversListContent(
                                ObserversListState(
                                    profileModel, observers,
                                    observed, tabState
                                ), Modifier.padding(top = 28.dp),
                                object: ObserversListCallback {
                                    override fun onTabChange(point: Int) {
                                        repeat(tabsState.size)
                                        { tabState[it] = point == it }
                                    }
                                }
                            )
                        }
                    }
                }
                
                override fun closeAlert() {
                    alert = false
                }
                
                override fun onNavBarSelect(point: Int) {
                    repeat(stateList.size) {
                        if(it == point) stateList[it] = ACTIVE
                        else if(stateList[it] != NEW)
                            stateList[it] = INACTIVE
                        when(point) {
                            0 -> nav.navigateAbsolute("main/meetings")
                            1 -> nav.navigateAbsolute("notification/list")
                            2 -> nav.navigateAbsolute("addmeet/category")
                            3 -> nav.navigateAbsolute("chats/main")
                            4 -> nav.navigateAbsolute("profile/main")
                        }
                    }
                }
                
                override fun onMenuClick(it: Boolean) {
                    menuState = it
                }
                
                override fun onMenuItemClick(point: Int) {
                    when(point) {
                        0 -> nav.navigate("avatar")
                        
                        else -> nav.navigateAbsolute(
                            "registration/gallery?multi=false"
                        )
                    }
                }
                
                override fun onHistoryClick(meet: MeetingModel) {
                    scope.launch {
                        asm.bottomSheet.expand {
                            MyMeetingScreen(
                                profileModel, meet,
                                asm, scope
                            )
                        }
                    }
                }
                
                override fun onMeetingClick(meet: MeetingModel) {
                    scope.launch {
                        asm.bottomSheet.expand {
                            MyMeetingScreen(
                                profileModel, meet,
                                asm, scope
                            )
                        }
                    }
                }
                
                override fun profileImage() {
                    menuState = true
                }
                
                override fun hiddenImages() {
                    nav.navigate("hidden")
                }
                
                override fun onRespondsClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            RespondsList(
                                listOf(pairRespondsList.value),
                                respondsState, tabsState,
                                Modifier, object: RespondCallback {
                                    override fun onCancelClick(respond: RespondModel) {
                                        respondsList.remove(respond)
                                        Toast.makeText(
                                            context,
                                            "Вы отказались от встречи",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    
                                    override fun onRespondClick(meet: MeetingModel) {
                                        Toast.makeText(
                                            context,
                                            "Вы нажали на встречу",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    
                                    override fun onAcceptClick(respond: RespondModel) {
                                        respondsList.remove(respond)
                                        Toast.makeText(
                                            context,
                                            "Вы приняли встречу",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    
                                    override fun onImageClick(image: HiddenPhotoModel) {
                                        Toast.makeText(
                                            context,
                                            "Фото смотреть нельзя",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    
                                    override fun onArrowClick(index: Int) {
                                        respondsState[index] = !respondsState[index]
                                    }
                                }, {
                                    repeat(tabsState.size) { index ->
                                        tabsState[index] = it == index
                                    }
                                }) {
                                scope.launch {
                                    asm.bottomSheet.collapse()
                                }
                            }
                        }
                    }
                }
                
                override fun openHistory(state: Boolean) {
                    historyState.value = !state
                }
            }
        )
    }
}