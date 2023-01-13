package ru.rikmasters.gilty.profile.presentation.ui.user.settings

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.SettingsViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.SexType
import ru.rikmasters.gilty.shared.model.enumeration.SexType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.SexType.MALE
import ru.rikmasters.gilty.shared.model.enumeration.SexType.OTHER
import ru.rikmasters.gilty.shared.model.profile.*

@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var age by remember { mutableStateOf(18) }
    var orientation by remember { mutableStateOf(DemoOrientationModel) }
    var gender by remember { mutableStateOf(FEMALE) }
    val orientationList = listOf(
        stringResource(R.string.orientation_hetero),
        stringResource(R.string.orientation_gay),
        stringResource(R.string.orientation_lesbian),
        stringResource(R.string.orientation_bisexual),
        stringResource(R.string.orientation_asexual),
        stringResource(R.string.orientation_demisexual),
        stringResource(R.string.orientation_pansexual),
        stringResource(R.string.orientation_queer),
        stringResource(R.string.condition_no_matter)
    )
    val orientationState = remember {
        mutableStateListOf(
            true, false, false, false,
            false, false, false, false
        )
    }
    val genderList = listOf(
        FEMALE.display,
        MALE.display,
        OTHER.display
    )
    val genderState = remember {
        mutableStateListOf(false, true, false)
    }
    var notification by remember { mutableStateOf(false) }
    val profile = ProfileModel(
        "0", "+7 910 524-12-12",
        "alina.loon", DemoEmojiModel,
        gender, orientation, age, ("Instagram @cristi"),
        DemoAvatarModel, DemoRatingModel, (true)
    )
    SettingsContent(
        SettingsState(profile, notification),
        Modifier, object: SettingsCallback {
            override fun onBack() {
                nav.navigate("main")
            }
            
            override fun editCategories() {
                nav.navigate("categories")
            }
            
            override fun onNotificationChange(it: Boolean) {
                notification = it
            }
            
            override fun onAboutAppClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        AboutAppBottom {
                            Toast.makeText(
                                context,
                                "Ссылок пока нет",
                                Toast.LENGTH_SHORT
                            ).show()
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                    }
                }
            }
            
            override fun onIconAppClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        IconsBottom {
                            Toast.makeText(
                                context,
                                "Иконку пока менять нельзя!",
                                Toast.LENGTH_SHORT
                            ).show()
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                    }
                }
            }
            
            override fun onGenderClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        SelectBottom(
                            stringResource(R.string.sex),
                            genderList, genderState
                        ) {
                            repeat(genderState.size) { index ->
                                genderState[index] = it == index
                            }
                            gender = SexType.get(it)
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                    }
                }
            }
            
            override fun onAgeClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        AgeBottom(Modifier, age, { age = it })
                        {
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                    }
                }
            }
            
            override fun onOrientationClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        SelectBottom(
                            stringResource(R.string.orientation_title),
                            orientationList, orientationState
                        ) {
                            repeat(orientationState.size) { index ->
                                orientationState[index] = it == index
                            }
                            orientation = OrientationModel(
                                "0", orientationList[it]
                            )
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                    }
                }
            }
            
            override fun onPhoneClick() {
                Toast.makeText(
                    context,
                    "Телефон пока менять нельзя!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onExit() {
                Toast.makeText(
                    context,
                    "Алерт будет позже",
                    Toast.LENGTH_SHORT
                ).show()
                scope.launch { vm.logout() }
                nav.navigateAbsolute("login")
            }
            
            override fun onDelete() {
                Toast.makeText(
                    context,
                    "Удалять аккаунт пока нельзя!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
}