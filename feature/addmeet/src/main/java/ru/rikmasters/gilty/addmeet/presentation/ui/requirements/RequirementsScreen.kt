package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.AgeBs
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.GenderBs
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms.OrientationBs
import ru.rikmasters.gilty.addmeet.viewmodel.MeetingType
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.AgeBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.GenderBsViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.OrientationBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

@Composable
fun RequirementsScreen(vm: RequirementsViewModel) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val gender by vm.gender.collectAsState()
    val orientation by vm.orientation.collectAsState()
    val age by vm.age.collectAsState()
    val count by vm.memberCount.collectAsState()
    val hideMeetPlace by vm.hideMeetPlace.collectAsState()
    val alert by vm.alert.collectAsState()
    val tabs by vm.tabs.collectAsState()
    val member by vm.selectMember.collectAsState()
    val withoutRespond by vm.withoutRespond.collectAsState()
    val memberLimited by vm.memberLimited.collectAsState()
    
    val isActive = /*memberCount.isNotEmpty()
                    && memberCount.toInt() != 0
                    && gender.isNotEmpty()
                    && age.isNotEmpty()
                    && orientation.isNotEmpty()
                    || hideMeetPlace*/ true
    
    fun getGender(index: Int?) =
        index?.let { GenderType.get(it).value }
    
    RequirementsContent(
        RequirementsState(
            hideMeetPlace, count,
            getGender(gender), age,
            orientation, tabs, member,
            alert, MeetingType, Online,
            isActive, withoutRespond, memberLimited
        ), Modifier, object: RequirementsCallback {
            
            override fun onGenderClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<GenderBsViewModel>(vm.scope) {
                            GenderBs(it)
                        }
                    }
                }
            }
            
            override fun onAgeClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<AgeBsViewModel>(vm.scope) {
                            AgeBs(it)
                        }
                    }
                }
            }
            
            override fun onOrientationClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<OrientationBsViewModel>(vm.scope) {
                            OrientationBs(it)
                        }
                    }
                }
            }
            
            override fun onWithoutRespondClick() {
                scope.launch { vm.withoutRespondChange() }
            }
            
            override fun onMemberLimit() {
                scope.launch { vm.limitMembers() }
            }
            
            override fun onTabClick(tab: Int) {
                scope.launch { vm.changeTab(tab) }
            }
            
            override fun onCountChange(text: String) {
                scope.launch { vm.changeMemberCount(text) }
            }
            
            override fun onHideMeetPlaceClick() {
                scope.launch { vm.hideMeetPlace() }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onMemberClick(member: Int) {
                scope.launch { vm.selectMember(member) }
            }
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                nav.navigate("complete")
            }
        })
}