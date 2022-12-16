package ru.rikmasters.gilty.profile.presentation.ui.user.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.profile.DemoGenderModel
import ru.rikmasters.gilty.shared.model.profile.DemoOrientationModel
import ru.rikmasters.gilty.shared.model.profile.DemoRatingModel
import ru.rikmasters.gilty.shared.model.profile.GenderModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

@Composable
fun SettingsScreen(nav: NavState = get()) {
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var age by remember { mutableStateOf(18) }
    var orientation by remember { mutableStateOf(DemoOrientationModel) }
    var gender by remember { mutableStateOf(DemoGenderModel) }
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
        stringResource(R.string.female_sex),
        stringResource(R.string.male_sex),
        stringResource(R.string.condition_no_matter)
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
        Modifier, object : SettingsCallback {
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
                    asm.bottomSheetState.expand {
                        AboutAppBottom {
                            Toast.makeText(
                                context,
                                "Ссылок пока нет",
                                Toast.LENGTH_SHORT
                            ).show()
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onIconAppClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        IconsBottom {
                            Toast.makeText(
                                context,
                                "Иконку пока менять нельзя!",
                                Toast.LENGTH_SHORT
                            ).show()
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onGenderClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        SelectBottom(
                            stringResource(R.string.sex),
                            genderList, genderState
                        ) {
                            repeat(genderState.size) { index ->
                                genderState[index] = it == index
                            }
                            gender = GenderModel(
                                "0", genderList[it]
                            )
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onAgeClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        AgeBottom(Modifier, age, { age = it })
                        {
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onOrientationClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
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
                                asm.bottomSheetState.collapse()
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