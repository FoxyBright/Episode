package ru.rikmasters.gilty.addmeet.presentation.ui.requirements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun RequirementsScreen(nav: NavState = get()) {
    var hideMeetPlace by remember { mutableStateOf(false) }
    var memberCount by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var orientation by remember { mutableStateOf("") }
    val selectedTabs = remember { mutableStateListOf(true, false) }
    val selectedMember = remember { mutableStateListOf(true) }
    GiltyTheme {
        RequirementsContent(
            RequirementsState(
                hideMeetPlace, memberCount,
                gender, age, orientation, selectedTabs, selectedMember
            ),
            Modifier, object : RequirementsCallback {
                override fun onHideMeetPlaceClick() {
                    hideMeetPlace = !hideMeetPlace
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
                    gender = "Не важно"
                }

                override fun onAgeClick() {
                    age = "от 18 до 28"
                }

                override fun onOrientationClick() {
                    orientation = "Гетеро"
                }

                override fun onTabClick(it: Int) {
                    repeat(selectedTabs.size)
                    { index -> selectedTabs[index] = it == index }
                }

                override fun onCountChange(text: String) {
                    val count = if (text.isNotBlank()) text.toInt() else 1
                    when {
                        count > selectedMember.size ->
                            repeat(count) { selectedMember.add(false) }

                        count < selectedMember.size -> if (count > 1)
                            repeat(selectedMember.size - count)
                            { selectedMember.removeLast() }

                    }
                    memberCount = text
                }
            })
    }
}