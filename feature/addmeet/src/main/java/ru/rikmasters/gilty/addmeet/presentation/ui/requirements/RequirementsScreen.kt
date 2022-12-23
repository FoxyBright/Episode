package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun RequirementsScreen(nav: NavState = get()) {
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    var hideMeetPlace by remember { mutableStateOf(false) }
    var memberCount by remember { mutableStateOf("1") }
    var alert by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("18") }
    var to by remember { mutableStateOf("18") }
    var orientation by remember { mutableStateOf("") }
    val selectedTabs = remember { mutableStateListOf(true, false) }
    val selectedMember = remember { mutableStateListOf(true) }
    val genderList = listOf(
        stringResource(R.string.female_sex),
        stringResource(R.string.male_sex),
        stringResource(R.string.condition_no_matter)
    )
    val genderControl =
        remember { mutableStateListOf(false, false, true) }
    val orientationList = listOf(
        stringResource(R.string.orientation_hetero),
        stringResource(R.string.orientation_gay),
        stringResource(R.string.orientation_lesbian),
        stringResource(R.string.orientation_bisexual),
        stringResource(R.string.orientation_asexual),
        stringResource(R.string.orientation_demisexual),
        stringResource(R.string.orientation_pansexual),
        stringResource(R.string.orientation_queer),
        stringResource(R.string.condition_no_matter),
    )
    val orientationControl =
        remember {
            mutableStateListOf(
                false, false, false, false,
                false, false, false, false, true
            )
        }
    GiltyTheme {
        RequirementsContent(
            RequirementsState(
                hideMeetPlace, memberCount,
                gender, age, orientation,
                selectedTabs, selectedMember,
                alert, MEETING.isOnline
            ),
            Modifier, object: RequirementsCallback {
                override fun onHideMeetPlaceClick() {
                    hideMeetPlace = !hideMeetPlace
                    MEETING.isPrivate = hideMeetPlace
                }
                
                override fun onCloseAlert(it: Boolean) {
                    alert = it
                }
                
                override fun onMemberClick(it: Int) {
                    repeat(selectedMember.size) { index ->
                        selectedMember[index] = it == index
                    }
                }
                
                override fun onClose() {
                    nav.navigateAbsolute("main/meetings")
                }
                
                override fun onBack() {
                    nav.navigate("detailed")
                }
                
                override fun onNext() {
                    nav.navigate("complete")
                }
                
                override fun onGenderClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            SelectBottom(
                                stringResource(R.string.sex),
                                genderList, genderControl, MEETING.isOnline
                            ) {
                                repeat(genderControl.size) { index ->
                                    genderControl[index] = index == it
                                    if(index == it) gender = genderList[index]
                                }; scope.launch { asm.bottomSheet.collapse() }
                            }
                        }
                    }
                }
                
                override fun onAgeClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            AgeBottom(from, to, Modifier,
                                { from = it }, { to = it }, MEETING.isOnline
                            )
                            {
                                age = "от $from до $to"
                                scope.launch { asm.bottomSheet.collapse() }
                            }
                        }
                    }
                }
                
                override fun onOrientationClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            SelectBottom(
                                stringResource(R.string.orientation_title),
                                orientationList,
                                orientationControl, MEETING.isOnline
                            ) {
                                repeat(orientationControl.size) { index ->
                                    orientationControl[index] = index == it
                                    if(index == it) orientation = orientationList[index]
                                }; scope.launch { asm.bottomSheet.collapse() }
                            }
                        }
                    }
                }
                
                override fun onTabClick(it: Int) {
                    repeat(selectedTabs.size)
                    { index -> selectedTabs[index] = it == index }
                }
                
                override fun onCountChange(text: String) {
                    val count = if(text.isNotBlank()) text.toInt() else 1
                    when {
                        count > selectedMember.size ->
                            repeat(count) { selectedMember.add(false) }
                        
                        count < selectedMember.size -> if(count > 1)
                            repeat(selectedMember.size - count)
                            { selectedMember.removeLast() }
                        
                    }
                    memberCount = text
                    MEETING.memberCount = count
                }
            })
    }
}