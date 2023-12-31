package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.AgeBs
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.GenderBs
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.OrientationBs
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.AgeBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.GenderBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.OrientationBsViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.common.extentions.offset
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

@Composable
fun RequirementsScreen(vm: RequirementsViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    val context = LocalContext.current
    
    val requirements by vm.requirements.collectAsState()
    val orientation by vm.orientation.collectAsState()
    val withoutRespond by vm.withoutRespond.collectAsState()
    val memberLimited by vm.limited.collectAsState()
    val count by vm.memberCount.collectAsState()
    val member by vm.selectMember.collectAsState()
    val private by vm.private.collectAsState()
    val alert by vm.alert.collectAsState()
    val gender by vm.gender.collectAsState()
    val age by vm.age.collectAsState()
    val selectedTab by vm.selectedTab.collectAsState()
    
    val meetType by vm.meetType.collectAsState()
    val online by vm.online.collectAsState()
    
    fun reqControl(it: RequirementModel) = when {
        it.gender == null -> true
        it.ageMin == 0 -> true
        it.ageMax == 0 -> true
        it.orientation == null -> true
        else -> false
    }
    
    fun checkRequirements(): Boolean {
        requirements.ifEmpty { return true }
        if(selectedTab == 0) reqControl(requirements.first())
        else requirements.forEach {
            if(reqControl(it)) return false
        }
        return true
    }
    
    fun getGender(index: Int?) =
        index?.let { GenderType.get(it).value }
    
    fun getOrientation(orientation: OrientationModel?) =
        orientation?.name ?: ""
    
    val countCheck = (if(memberLimited) count.isNotBlank()
            && count.toInt() > 1 else true)
    
    val isActive = (private && countCheck)
            || countCheck
            && gender != null
            && age.isNotBlank()
            && orientation != null
            && checkRequirements()
    val badDate = stringResource(R.string.add_meet_bad_date)
    RequirementsContent(
        RequirementsState(
            private, count, getGender(gender), age,
            getOrientation(orientation), selectedTab,
            member, alert, meetType, online,
            isActive, withoutRespond, memberLimited
        ), Modifier, object: RequirementsCallback {
            
            override fun onGenderClick() {
                vm.scope.openBS<GenderBsViewModel>(scope) {
                    GenderBs(it)
                }
            }
            
            override fun onAgeClick() {
                vm.scope.openBS<AgeBsViewModel>(scope) {
                    AgeBs(it)
                }
            }
            
            override fun onOrientationClick() {
                vm.scope.openBS<OrientationBsViewModel>(scope) {
                    OrientationBs(it)
                }
            }
            
            override fun onClearCount() {
                scope.launch { vm.clearCount() }
            }
            
            override fun onWithoutRespondClick() {
                scope.launch { vm.withoutRespondChange() }
            }
            
            override fun onMemberLimit() {
                scope.launch {
                    vm.limitMembers()
                    vm.changeMemberCount("")
                    vm.changeTab(0)
                    vm.selectMember(0)
                }
            }
            
            override fun onTabClick(tab: Int) {
                scope.launch { vm.changeTab(tab) }
            }
            
            override fun onCountChange(text: String) {
                scope.launch { vm.changeMemberCount(text) }
            }
            
            override fun onHideMeetPlaceClick() {
                scope.launch { vm.changePrivate() }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onMemberClick(member: Int) {
                scope.launch { vm.selectMember(member) }
            }
            
            override fun onClose() {
                scope.launch {
                    vm.clearAddMeet()
                    nav.clearStackNavigation("main/meetings")
                }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    val toComplete = vm.date.value?.let {
                        it.ifBlank { return@let false }
                        LocalDateTime.of(it)
                            .minusMinute(offset / 60_000)
                            .isAfter(LocalDateTime.nowZ())
                    } ?: false
                    vm.setRequirements()
                    nav.navigate(
                        if(toComplete) "complete"
                        else {
                            Toast.makeText(
                                context, badDate,
                                Toast.LENGTH_SHORT
                            ).show()
                            "detailed"
                        }
                    )
                }
            }
        }
    )
}