package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun PersonalScreen(nav: NavState = get()) {
    val a = remember { mutableStateOf(18) }
    var age by remember { mutableStateOf(18) }
    var ageText by remember { mutableStateOf("") }
    var list by remember { mutableStateOf(arrayListOf(false, false, false)) }
    PersonalInfoContent(
        PersonalInfoContentState(rememberCoroutineScope(), a.value, ageText, list),
        object : PersonalInfoContentCallback {
            override fun onBack() {
                nav.navigate("profile")
            }

            override fun onAgeChange(it: Int) {
                a.value = it
                age = it
                ageText = "$it"
            }

            override fun onGenderChange(index: Int) {
                val array = arrayListOf<Boolean>()
                list.forEachIndexed { it, _ -> array.add(index == it) }
                list = array
            }

            override fun onNext() {
                nav.navigate("categories")
            }
        })
}