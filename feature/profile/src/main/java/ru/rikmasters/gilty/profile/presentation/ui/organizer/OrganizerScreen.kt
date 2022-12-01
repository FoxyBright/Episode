package ru.rikmasters.gilty.profile.presentation.ui.organizer

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.lists.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeeting
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeetingCallback
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeetingState
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
@Suppress("unused")
fun OrganizerProfileScreen(nav: NavState = get()) {
    GiltyTheme {
        val asm = get<AppStateModel>()
        val scope = rememberCoroutineScope()
        var observeState by remember { mutableStateOf(false) }
        val currentMeetings = DemoMeetingList
        val profileModel = DemoProfileModel
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

        val context = LocalContext.current
        val organizerCB = object : UserProfileCallback { /* -    временная заглушка*/
            override fun menu(state: Boolean) {}

            override fun onMeetingClick(meet: MeetingModel) {}
            override fun closeAlert() {}

            override fun onBack() {
                scope.launch {
                    asm.bottomSheetState.collapse()
                }
            }

            override fun onWatchPhotoClick() {}

            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }

        var alert by remember { mutableStateOf(false) }
        var menuState by remember { mutableStateOf(false) }
        val meeting = DemoFullMeetingModel
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

            override fun onCloseClick() {
                scope.launch {
                    asm.bottomSheetState.collapse()
                }
            }

            override fun onAllWatchClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        ParticipantsList(
                            DemoFullMeetingModel,
                            DemoMemberModelList,
                            Modifier, {}, {}
                        )
                    }
                }
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

            override fun onMemberClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        OrganizerProfile(
                            Modifier, OrganizerProfileState(
                                profileState, currentMeetings
                            ), organizerCB
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

        val organizerCallback = object : UserProfileCallback {

            override fun menu(state: Boolean) {}

            override fun onMeetingClick(meet: MeetingModel) {
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

            override fun onBack() {
                Toast.makeText(
                    context,
                    "Зачем тыкать - если можно дернуть?",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onWatchPhotoClick() {
                Toast.makeText(
                    context,
                    "Посмотреть фото",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun closeAlert() {}

            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }
        OrganizerProfile(
            Modifier, OrganizerProfileState(
                profileState, currentMeetings
            ), organizerCallback
        )
    }
}