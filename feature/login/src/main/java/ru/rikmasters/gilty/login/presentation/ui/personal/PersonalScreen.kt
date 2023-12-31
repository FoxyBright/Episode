package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.login.viewmodel.PersonalViewModel
import ru.rikmasters.gilty.login.viewmodel.bottoms.AgeBsViewModel

@Composable
fun PersonalScreen(vm: PersonalViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val gender by vm.gender.collectAsState()
    val age by vm.age.collectAsState()
    
    PersonalContent(
        PersonalState(age, gender),
        Modifier, object: PersonalCallback {
            
            override fun onAgeClick() {
                vm.scope.openBS<AgeBsViewModel>(scope){
                    AgeBs(it)
                }
            }
        
            override fun onGenderChange(index: Int) {
                scope.launch { vm.setGender(index) }
            }
        
            override fun onNext() {
                nav.navigate("categories")
            }
        
            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}