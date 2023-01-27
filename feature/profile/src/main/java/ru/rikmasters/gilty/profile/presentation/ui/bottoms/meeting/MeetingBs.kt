package ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer.OrganizerProfileState
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants.ParticipantsListCallback
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel.MeetNavigation.*
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsContent
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsState
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ANONYMOUS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

@Composable
fun MeetingBs(
    vm: MeetingBsViewModel,
    meetId: String,
) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val screen by vm.screen.collectAsState()
    
    val meeting by vm.meet.collectAsState()
    val stack by vm.stack.collectAsState()
    val profileModel = DemoProfileModel
    
    val menu by vm.menu.collectAsState()
    val memberList by vm.memberList.collectAsState()
    val distance by vm.distance.collectAsState()
    
    val shared = meeting?.memberState == IS_ORGANIZER
    val userInMeet = memberList.contains(profileModel.mapToMemberModel())
    val buttonState = meeting?.memberState == IS_ORGANIZER
    
    LaunchedEffect(Unit) {
        vm.navigate(MEET, meetId)
        vm.clearStack()
    }
    
    meeting?.let { meet ->
        when(screen) {
            PROFILE -> Profile(vm, scope, meet.type)
            PARTICIPANTS -> Participants(vm, scope, meet)
            COMPLAINTS -> Complaints(vm, scope, meetId)
            MEET -> MeetingBsContent(
                MeetingBsState(
                    menu, meet, memberList, distance,
                    userInMeet, buttonState, shared,
                    backButton = stack.isNotEmpty()
                ),
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                object: MeetingBsCallback {
                    
                    override fun onBack() {
                        scope.launch { vm.navigateBack() }
                    }
                    
                    override fun onKebabClick(state: Boolean) {
                        scope.launch { vm.menuDismiss(state) }
                    }
                    
                    override fun onMeetPlaceClick(meetLocation: LocationModel?) {
                        scope.launch { vm.meetPlaceClick(meetLocation) }
                    }
                    
                    override fun onRespondsClick() {
                    }
                    
                    override fun onMenuItemClick(index: Int, meetId: String) {
                        scope.launch {
                            when(index) {
                                0 -> vm.sharedMeet(meetId)
                                1 -> vm.leaveMeet(meetId)
                                2 -> vm.canceledMeet(meetId)
                                3 -> vm.navigate(COMPLAINTS)
                                else -> {}
                            }
                            asm.bottomSheet.collapse()
                        }
                    }
                    
                    override fun onAvatarClick(organizerId: String) {
                        scope.launch {
                            if(meet.type != ANONYMOUS)
                                vm.getUserActualMeets(organizerId)
                            vm.navigate(PROFILE, organizerId)
                        }
                    }
                    
                    override fun onAllMembersClick(meetId: String) {
                        scope.launch { vm.navigate(PARTICIPANTS, meetId) }
                    }
                    
                    override fun onMemberClick(member: MemberModel) {
                        scope.launch {
                            if(meet.type != ANONYMOUS)
                                vm.getUserActualMeets(member.id)
                            vm.navigate(PROFILE, member.id)
                        }
                    }
                    
                    override fun onRespondClick(meetId: String) {
                        scope.launch { vm.respondForMeet(meetId) }
                    }
                }
            )
        }
    }
}

@Composable
private fun Complaints(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    meetId: String,
) {
    val asm = get<AppStateModel>()
    ComplainsContent(meetId) {
        scope.launch {
            asm.bottomSheet.collapse()
            vm.clearStack()
            vm.alertDismiss(true)
        }
    }
}

@Composable
private fun Participants(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    meet: FullMeetingModel,
) {
    val memberList by vm.memberList.collectAsState()
    ParticipantsList(
        meet, memberList,
        Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        object: ParticipantsListCallback {
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch { vm.navigate(PROFILE, member.id) }
            }
        }
    )
}

@Composable
private fun Profile(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    meetState: MeetType,
) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    
    val observeState by vm.observe.collectAsState()
    val meets by vm.userActualMeets.collectAsState()
    val profile by vm.profile.collectAsState()
    
    val profileState = ProfileState(
        profile = profile,
        profileType = if(meetState == ANONYMOUS)
            ANONYMOUS_ORGANIZER else ORGANIZER,
        observeState = observeState
    )
    
    OrganizerProfile(
        Modifier.fillMaxSize(),
        OrganizerProfileState(
            profileState, meets
        ), object: UserProfileCallback {
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch { vm.navigate(MEET, meet.id) }
            }
            
            override fun profileImage() {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigate("avatar?type=2&image=${profile.avatar?.url}")
                }
            }
            
            override fun hiddenImages() {
            }
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onObserveChange(state: Boolean) {
                scope.launch { vm.observeUser(state, profile.id) }
            }
        }
    )
}