package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun PersonalScreen(nav: NavState = get()) {
    var age by remember { mutableStateOf<Int?>(null) }
    val asm = get<AppStateModel>()
    var list by remember { mutableStateOf(arrayListOf(false, false, false)) }
    val scope = rememberCoroutineScope()
    PersonalInfoContent(
        PersonalInfoContentState(age, list),
        object : PersonalInfoContentCallback {
            override fun onBack() {
                nav.navigate("profile")
            }

            override fun onAgeClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        BottomSheetContent(Modifier, age, { age = it })
                        {
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onAgeChange(it: Int) {
                age = it
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