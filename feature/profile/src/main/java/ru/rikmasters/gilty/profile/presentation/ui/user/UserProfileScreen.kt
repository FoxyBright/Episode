package ru.rikmasters.gilty.profile.presentation.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.lists.ObserversListCallback
import ru.rikmasters.gilty.profile.presentation.ui.lists.ObserversListContent
import ru.rikmasters.gilty.profile.presentation.ui.lists.ObserversListState
import ru.rikmasters.gilty.profile.presentation.ui.lists.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.lists.RespondsList
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeeting
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeetingCallback
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeetingState
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfileState
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
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
        val historyState =
            remember { mutableStateOf(false) }
        val menuExpanded =
            remember { mutableStateOf(false) }
        val meets = DemoMeetingList
        val profileModel = DemoProfileModel
        val state = ProfileState(
            name = "${profileModel.username}, ${profileModel.age}",
            profilePhoto = profileModel.avatar.id,
            description = profileModel.aboutMe,
            rating = profileModel.rating.average,
            observers = 13500,
            observed = 128,
            emoji = EmojiList.first(),
            profileType = ProfileType.USERPROFILE,
            enabled = false,
        )

        var alert by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val meeting = DemoFullMeetingModel
        var observeState by remember { mutableStateOf(false) }
        val currentMeetings = DemoMeetingList
        val profileState = ProfileState(
            name = "${profileModel.username}, ${profileModel.age}",
            profilePhoto = profileModel.avatar.id,
            description = profileModel.aboutMe,
            rating = profileModel.rating.average,
            emoji = EmojiList.first(),
            profileType = ProfileType.ORGANIZER,
            observeState = observeState,
            enabled = false
        )
        val organizerCallback = object : UserProfileCallback {
            override fun menu(state: Boolean) {}
            override fun closeAlert() {
                alert = false
            }

            override fun onMeetingClick(meet: MeetingModel) {}

            override fun onBack() {}

            override fun onWatchPhotoClick() {}

            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }

        var menuState by remember { mutableStateOf(false) }
        val meetingCallback = object : MyMeetingCallback {
            override fun onConfirm() {
                Toast.makeText(
                    context,
                    "ВААААУ, Типа поделился) Съешь пирожок с полки",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun closeAlert() {
                alert = false
            }

            override fun menuCollapse(it: Boolean) {
                menuState = it
            }

            override fun menuItemClick(point: Int) {
                menuState = false
                scope.launch {
                    asm.bottomSheetState.expand {
                        ComplainsContent(DemoFullMeetingModel) {
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }; alert = true
                        }
                    }
                }
            }

            override fun onCloseClick() {
                scope.launch {
                    asm.bottomSheetState.expand()
                }
            }

            override fun onAllWatchClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        ParticipantsList(
                            DemoFullMeetingModel,
                            DemoMemberModelList,
                            Modifier, {
                                scope.launch {
                                    asm.bottomSheetState.collapse()
                                }
                            }, {
                                scope.launch {
                                    asm.bottomSheetState.collapse()
                                }; onMemberClick()
                            }
                        )
                    }
                }
            }

            override fun onMemberClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        OrganizerProfile(
                            Modifier, OrganizerProfileState(
                                profileState, currentMeetings
                            ), organizerCallback
                        )
                    }
                }
            }

            override fun openMap() {
                Toast.makeText(
                    context,
                    "К сожалению карты пока не доступны",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
            remember { mutableStateOf(Pair(DemoFullMeetingModel, respondsList)) }

        val tabState = remember { mutableStateListOf(true, false) }

        UserProfile(
            UserProfileState(
                state, meets, meets,
                meets.first(), (respondsList.size),
                historyState.value, menuExpanded.value,
                stateList, alert
            ), Modifier, object : UserProfileCallback {
                override fun menu(state: Boolean) {
                    menuExpanded.value = !menuExpanded.value
                }

                override fun onObserveClick() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            ObserversListContent(
                                ObserversListState(
                                    profileModel, observers,
                                    observed, tabState
                                ), Modifier,
                                object : ObserversListCallback {
                                    override fun onTabChange(point: Int) {
                                        repeat(tabsState.size)
                                        { tabState[it] = point == it }
                                    }

                                    override fun onDelete() {
                                        super.onDelete()
                                    }

                                    override fun onClick() {
                                        super.onClick()
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
                        if (it == point) stateList[it] = ACTIVE
                        else if (stateList[it] != NEW)
                            stateList[it] = INACTIVE
                        when (point) {
                            0 -> nav.navigateAbsolute("main/meetings")
                            1 -> nav.navigateAbsolute("notification/list")
                            2 -> nav.navigateAbsolute("addmeet/category")
                            4 -> nav.navigateAbsolute("profile/main")
                        }
                    }
                }

                override fun onHistoryClick(meet: MeetingModel) {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            MyMeeting(
                                Modifier,
                                MyMeetingState(
                                    meeting, DemoMemberModelList,
                                    18, meeting.duration,
                                    menuState, alert
                                ), meetingCallback
                            )
                        }
                    }
                }

                override fun onMeetingClick(meet: MeetingModel) {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            MyMeeting(
                                Modifier.padding(16.dp),
                                MyMeetingState(
                                    meeting, DemoMemberModelList,
                                    18, meeting.duration,
                                    menuState, alert
                                ), meetingCallback
                            )
                        }
                    }
                }

                override fun profileImage() {
                    nav.navigate("photo")
                }

                override fun hiddenImages() {
                    Toast.makeText(
                        context,
                        "Ваши скрытые фото",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSettingsClick() {
                    nav.navigate("settings")
                }

                override fun onWatchPhotoClick() {
                    Toast.makeText(
                        context,
                        "Фото пока что нет",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onRespondsClick() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            RespondsList(
                                listOf(pairRespondsList.value),
                                respondsState, tabsState,
                                Modifier, object : RespondCallback {
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
                                })
                            {
                                scope.launch {
                                    asm.bottomSheetState.collapse()
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