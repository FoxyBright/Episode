package ru.rikmasters.gilty.profile.presentation.ui.bottoms.meeting

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
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
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel

@Composable
fun MeetingBs(
    vm: MeetingBsViewModel,
    meet: MeetingModel,
) {
    val scope = rememberCoroutineScope()
    val screen by vm.screen.collectAsState()
    
    val menu by vm.menu.collectAsState()
    val memberList by vm.memberList.collectAsState()
    val distance by vm.distance.collectAsState()
    val shared = true
    val userInMeet = true
    val buttonState = true
    
    LaunchedEffect(Unit) {
        vm.drawMeet(meet)
    }
    
    when(screen) {
        PROFILE -> Profile(vm, scope, meet.organizer)
        PARTICIPANTS -> Participants(vm, scope, meet)
        COMPLAINTS -> Complaints(vm, scope, meet)
        MEET -> MeetingBsContent(
            MeetingBsState(
                menu, meet, memberList, distance,
                userInMeet, buttonState, shared
            ), Modifier.padding(16.dp),
            object: MeetingBsCallback {
                
                override fun closeAlert() {
                    scope.launch { vm.alertDismiss(false) }
                }
                
                override fun onKebabClick(state: Boolean) {
                    scope.launch { vm.menuDismiss(state) }
                }
                
                override fun onMenuItemClick(index: Int) {
                    scope.launch { vm.navigate(COMPLAINTS) }
                }
                
                override fun onAllMembersClick() {
                    scope.launch { vm.navigate(PARTICIPANTS) }
                }
                
                override fun onMemberClick(member: MemberModel) {
                    scope.launch { vm.navigate(PROFILE) }
                }
                
                override fun onBottomButtonClick(point: Int) {
                    scope.launch {
                        when(point) {
                            1 -> vm.outOfMeet()
                            2 -> vm.sharedMeet()
                            3 -> vm.cancelOfMeet()
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun Complaints(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    meet: MeetingModel,
) {
    ComplainsContent(meet) {
        scope.launch {
            vm.navigate(MEET)
            vm.alertDismiss(true)
        }
    }
}

@Composable
private fun Participants(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    meet: MeetingModel,
) {
    val memberList by vm.memberList.collectAsState()
    ParticipantsList(
        meet, memberList, Modifier,
        object: ParticipantsListCallback {
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch { vm.navigate(PROFILE) }
            }
        }
    )
}

@Composable
private fun Profile(
    vm: MeetingBsViewModel,
    scope: CoroutineScope,
    user: OrganizerModel?,
) {
    
    val observeState by vm.observe.collectAsState()
    val meets by vm.meets.collectAsState()
    val profile by vm.profile.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.drawProfile()
    }
    
    val profileState = ProfileState(
        profile, profileType = ORGANIZER,
        observeState = observeState,
    )
    
    OrganizerProfile(
        Modifier, OrganizerProfileState(
            profileState, meets
        ), object: UserProfileCallback {
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch { vm.navigate(MEET) }
            }
            
            override fun onBack() {
                scope.launch { vm.navigateBack() }
            }
            
            override fun onObserveChange(state: Boolean) {
                scope.launch { vm.observeUser(state) }
            }
        }
    )
}