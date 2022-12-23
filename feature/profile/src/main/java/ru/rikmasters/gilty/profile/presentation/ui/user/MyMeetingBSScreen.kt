package ru.rikmasters.gilty.profile.presentation.ui.user

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.presentation.ui.lists.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfileState
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSState
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBottomSheet
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

@Composable
fun MyMeetingScreen(
    user: ProfileModel,
    meet: MeetingModel,
    asm: AppStateModel = get(),
    scope: CoroutineScope
) {
    meet(user, meet, asm, scope)
}

@Composable
private fun meet(
    user: ProfileModel,
    meet: MeetingModel,
    asm: AppStateModel = get(),
    scope: CoroutineScope
) {
    val context = LocalContext.current
    var menuState by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    
    MeetingBottomSheet(
        MeetingBSState(
            menuState, meet,
            DemoMemberModelList,
            distanceCalculator(meet),
            (true), (true), (true)
        ), Modifier.padding(16.dp),
        object: MeetingBSCallback {
    
            override fun closeAlert() {
                alert = false
            }
    
            override fun onKebabClick(state: Boolean) {
                menuState = state
            }
            
            override fun onMenuItemClick(index: Int) {
                menuState = false
                scope.launch {
                    asm.bottomSheet.expand {
                        ComplainsContent(DemoMeetingModel) {
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }; alert = true
                        }
                    }
                }
            }
            
            override fun onAllMembersClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        ParticipantsList(
                            DemoMeetingModel, DemoMemberModelList,
                            Modifier, {
                                launch {
                                    asm.bottomSheet.expand {
                                        meet(user, meet, asm, scope)
                                    }
                                }
                            }, {
                                launch {
                                    asm.bottomSheet.expand {
                                        organizer(user, meet, asm, scope)
                                    }
                                }
                            }
                        )
                    }
                }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        organizer(user, meet, asm, scope)
                    }
                }
            }
            
            override fun onBottomButtonClick(point: Int) {
                when(point) {
                    1 -> Toast.makeText(
                        context,
                        "Вы покинули meet",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    2 -> Toast.makeText(
                        context,
                        "ВААААУ, Типа поделился) Съешь пирожок с полки",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    3 -> Toast.makeText(
                        context,
                        "Вы отменили встречу",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )
}

@Composable
fun organizer(
    user: ProfileModel,
    meet: MeetingModel,
    asm: AppStateModel = get(),
    scope: CoroutineScope
) {
    val profileModel = DemoProfileModel
    var observeState by remember { mutableStateOf(false) }
    val currentMeetings = DemoMeetingList
    val profileState = ProfileState(
        name = "${profileModel.username}, ${profileModel.age}",
        profilePhoto = profileModel.avatar.id,
        description = profileModel.aboutMe,
        rating = profileModel.rating.average,
        emoji = DemoEmojiModel,
        profileType = ORGANIZER,
        observeState = observeState,
        enabled = false
    )
    OrganizerProfile(
        Modifier, OrganizerProfileState(
            profileState, currentMeetings
        ), object: UserProfileCallback {
            override fun menu(state: Boolean) {}
            override fun closeAlert() {}
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        meet(user, meet, asm, scope)
                    }
                }
            }
            
            override fun onBack() {
                scope.launch {
                    asm.bottomSheet.expand {
                        meet(user, meet, asm, scope)
                    }
                }
            }
            
            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }
    )
}