package ru.rikmasters.gilty.profile.presentation.ui.mymeetings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.presentation.ui.lists.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.organizer.OrganizerProfileState
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.common.RespondsListContent
import ru.rikmasters.gilty.shared.common.extentions.MeetSeparate
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.shared.RowActionBar

@Composable
fun MyMeetingScreen(
    asm: AppStateModel = get(),
    scope: CoroutineScope
) {
    meet(asm, scope)
}

@Composable
private fun meet(asm: AppStateModel = get(), scope: CoroutineScope) {
    val context = LocalContext.current
    val meet = DemoFullMeetingModel
    var menuState by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    
    val respondsList = remember {
        mutableStateListOf(
            DemoReceivedRespondModelWithoutPhoto,
            DemoReceivedRespondsModel
        )
    }
    
    val respondListStates =
        remember { mutableStateListOf<Boolean>() }
    repeat(respondsList.size) {
        if(it == 0) respondListStates.add(true) else
            respondListStates.add(false)
    }
    
    MyMeeting(
        Modifier.padding(16.dp),
        MyMeetingState(
            meet, DemoMemberModelList,
            18, meet.duration, menuState,
            alert, respondsList.size, respondsList.last()
        ), object: MyMeetingCallback {
            override fun onConfirm() {
                Toast.makeText(
                    context,
                    "ВААААУ, Типа поделился) Съешь пирожок с полки",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onRespondsClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        responds(
                            asm, scope, context,
                            respondsList, respondListStates,
                            { respondListStates[it] = !respondListStates[it] },
                            { respondsList.remove(it) }
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
            
            override fun onCloseClick() {
                scope.launch {
                    asm.bottomSheetState.collapse()
                }
            }
            
            override fun closeAlert() {
                alert = false
            }
            
            override fun onAllWatchClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        ParticipantsList(
                            DemoFullMeetingModel, DemoMemberModelList,
                            Modifier, {
                                launch { asm.bottomSheetState.expand { meet(asm, scope) } }
                            }, { launch { asm.bottomSheetState.expand { organizer(asm, scope) } } }
                        )
                    }
                }
            }
            
            override fun onMemberClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        organizer(asm, scope)
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
    )
}

@Composable
private fun responds(
    asm: AppStateModel = get(),
    scope: CoroutineScope,
    context: Context,
    respondsList: List<RespondModel>,
    respondListStates: List<Boolean>,
    onArrowClick: (Int) -> Unit,
    remove: (RespondModel) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        RowActionBar(
            stringResource(R.string.profile_responds_label),
            modifier = Modifier.padding(start = 16.dp, top = 28.dp)
        )
        RespondsListContent(
            MeetSeparate(respondsList),
            respondListStates,
            Modifier.padding(start = 16.dp),
            object: RespondCallback {
                override fun onCancelClick(respond: RespondModel) {
                    remove(respond)
                    Toast.makeText(
                        context, "Втреча отклонена",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(respondsList.isEmpty())
                        scope.launch { asm.bottomSheetState.collapse() }
                }
                
                override fun onRespondClick(meet: MeetingModel) {
                    Toast.makeText(
                        context,
                        "Отклик на встречу ${meet.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                override fun onAcceptClick(respond: RespondModel) {
                    remove(respond)
                    Toast.makeText(
                        context, "Втреча принята",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(respondsList.isEmpty())
                        scope.launch { asm.bottomSheetState.collapse() }
                }
                
                override fun onArrowClick(index: Int) {
                    onArrowClick(index)
                }
            }
        )
    }
}

@Composable
private fun organizer(asm: AppStateModel = get(), scope: CoroutineScope) {
    val profileModel = DemoProfileModel
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
    OrganizerProfile(
        Modifier, OrganizerProfileState(
            profileState, currentMeetings
        ), object: UserProfileCallback {
            override fun menu(state: Boolean) {}
            override fun closeAlert() {}
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch { asm.bottomSheetState.expand { meet(asm, scope) } }
            }
            
            override fun onBack() {
                scope.launch { asm.bottomSheetState.expand { meet(asm, scope) } }
            }
            
            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }
    )
}