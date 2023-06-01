package ru.rikmasters.gilty.bottomsheet.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType.Companion.BoolType
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.core.scope.Scope
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.*
import ru.rikmasters.gilty.bottomsheet.presentation.ui.map.MapBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.MeetingBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.observers.ObserversBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer.OrganizerBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.participants.ParticipantsBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportsBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBs
import ru.rikmasters.gilty.bottomsheet.viewmodel.*
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.web.openInWeb
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.yandexmap.presentation.MapAppsBs
import ru.rikmasters.gilty.yandexmap.presentation.YandexMapScreen
import ru.rikmasters.gilty.yandexmap.viewmodel.YandexMapViewModel

@Composable
fun BottomSheet(
    scope: Scope,
    type: BsType,
    meetId: String? = null,
    userId: String? = null,
    reportType: ReportObjectType? = null,
    reportObject: String? = null,
    location: LocationModel? = null,
    category: CategoryModel? = null,
    fullResponds: Boolean = false,
    username: String? = "",
) {
    
    val nav = rememberNavController()
    val asm = get<AppStateModel>()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        if(asm.bottomSheet.current.value != BottomSheetSwipeState.COLLAPSED){
            coroutineScope.launch { asm.bottomSheet.collapse() }
        }else {
            nav.popBackStack()
        }
    }

    NavHost(
        nav, when(type) {
            MEET, SHORT_MEET -> "MEET?meet={meet}&detailed={detailed}"
            MAP -> "MAP?location={location}&category={category}"
            RESPONDS -> "RESPONDS?meet={meet}&full={full}"
            PARTICIPANTS -> "PARTICIPANTS?meet={meet}"
            REPORTS -> "REPORTS?id={id}&type={type}"
            USER -> "USER?user={user}&meet={meet}"
            OBSERVERS -> "OBSERVERS?user={user}"
            APPS -> "APPS?lat={lat}&lng={lng}"
            LOCATION -> "LOCATION"
        }
    ) {
        
        composable(
            route = "OBSERVERS?user={user}",
            arguments = listOf(
                setStringArg("user", (username ?: ""))
            )
        ) { stack ->
            stack.GetStringArg("user") { user ->
                Connector<ObserverBsViewModel>(scope) {
                    ObserversBs(it, user, nav)
                }
            }
        }
        
        composable(
            route = "RESPONDS?meet={meet}&full={full}",
            arguments = listOf(
                setStringArg("meet", "$meetId"),
                setBooleanArg("full", fullResponds)
            )
        ) { stack ->
            stack.GetStringArg("meet") { meet ->
                stack.GetBooleanArg("full") { full ->
                    Connector<RespondsBsViewModel>(scope) {
                        RespondsBs(
                            full, if(meet != "null")
                                meet else null, it, nav
                        )
                    }
                }
            }
        }
        
        composable(
            route = "APPS?lat={lat}&lng={lng}",
            arguments = listOf(
                setStringArg("lat", "${location?.lat}"),
                setStringArg("lng", "${location?.lng}"),
            )
        ) {
            
            var alert by remember { mutableStateOf(false) }
            var appIndex by remember { mutableStateOf(0) }
            
            val corScope = rememberCoroutineScope()
            
            it.GetStringArg("lat") { lat ->
                it.GetStringArg("lng") { lng ->
                    val googleMap =
                        "https://maps.google.com/?daddr=$lat%2C$lng&zoom=18"
                    val yandexMap =
                        "https://maps.yandex.ru/?pt=$lng%2C$lat&z=18"
                    MapAppsBs(
                        alert, appIndex, Modifier, { state ->
                            if(state) {
                                openInWeb(
                                    context, if(appIndex == 0) googleMap
                                    else yandexMap
                                )
                                corScope.launch {
                                    asm.bottomSheet.collapse()
                                }
                            } else alert = false
                        }
                    ) { app -> appIndex = app; alert = true }
                }
            }
        }
        
        composable(
            route = "MAP?location={location}&category={category}",
            arguments = listOf(
                setStringArg("location", "$location"),
                setStringArg("category", ""),
            )
        ) { stack ->

            LaunchedEffect(key1 = Unit, block = {
                asm.bottomSheet.swipeableState.currentScreenName.value = "Map"
            })

            DisposableEffect(key1 = Unit, effect = {
                onDispose {
                    asm.bottomSheet.swipeableState.currentScreenName.value = ""
                }
            })

            stack.GetStringArg("location") { location ->
                stack.GetStringArg("category") { category ->
                    Connector<YandexMapViewModel>(scope) {
                        YandexMapScreen(it, location, category, nav)
                    }
                }
            }
        }
        
        composable(
            route = "MEET?meet={meet}&detailed={detailed}",
            arguments = listOf(
                setStringArg("meet", "$meetId"),
                setBooleanArg("detailed", type == SHORT_MEET)
            )
        ) { stack ->
            stack.GetStringArg("meet") { meet ->
                stack.GetBooleanArg("detailed") { detailed ->
                    Connector<MeetingBsViewModel>(scope) {
                        MeetingBs(it, meet, detailed, nav)
                    }
                }
            }
        }
        
        composable(
            route = "USER?user={user}&meet={meet}",
            arguments = listOf(
                setStringArg("user", "$userId"),
                setStringArg("meet", "$meetId")
            )
        ) { stack ->
            stack.GetStringArg("user") { user ->
                stack.GetStringArg("meet") { meet ->
                    Connector<UserBsViewModel>(scope) {
                        OrganizerBs(it, user, meet, nav)
                    }
                }
            }
        }
        
        composable(
            route = "PARTICIPANTS?meet={meet}",
            arguments = listOf(
                setStringArg("meet", "$meetId")
            )
        ) { stack ->
            stack.GetStringArg("meet") { meet ->
                Connector<ParticipantsBsViewModel>(scope) {
                    ParticipantsBs(it, meet, nav)
                }
            }
        }
        
        composable(
            route = "REPORTS?id={id}&type={type}",
            arguments = listOf(
                setStringArg("id", "$reportObject"),
                setStringArg("type", "${reportType?.name}")
            )
        ) { stack ->
            stack.GetStringArg("id") { id ->
                stack.GetStringArg("type") { type ->
                    Connector<ReportsBsViewModel>(scope) {
                        ReportsBs(
                            it,
                            ReportObjectType.valueOf(type),
                            id,
                            nav
                        )
                    }
                }
            }
        }
        
        composable("LOCATION") {
            category?.name?.let { name ->
                Connector<MapBsViewModel>(scope) {
                    MapBs(it, nav, name)
                }
            }
        }
    }
}

@Composable
private fun NavBackStackEntry.GetStringArg(
    arg: String,
    block: @Composable (String) -> Unit,
) = arguments?.getString(arg)?.let { block(it) }

@Composable
private fun NavBackStackEntry.GetBooleanArg(
    arg: String,
    block: @Composable (Boolean) -> Unit,
) = arguments?.getBoolean(arg)?.let { block(it) }

private fun setStringArg(
    arg: String,
    default: String,
) = navArgument(arg) {
    this.type = StringType
    defaultValue = default
}

@Suppress("SameParameterValue")
private fun setBooleanArg(
    arg: String,
    default: Boolean,
) = navArgument(arg) {
    this.type = BoolType
    defaultValue = default
}