package ru.rikmasters.gilty.bottomsheet.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.*
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.MeetingBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.organizer.OrganizerBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.participants.ParticipantsBs
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportsBs
import ru.rikmasters.gilty.bottomsheet.viewmodel.BsViewModel
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.*
import ru.rikmasters.gilty.core.viewmodel.connector.Connector

@Composable
fun BottomSheet(
    vm: BsViewModel,
    type: BsType,
    meetId: String? = null,
    userId: String? = null,
    reportType: String? = null,
    reportObject: String? = null,
) {
    val nav = rememberNavController()
    
    NavHost(
        nav, when(type) {
            REPORTS -> "REPORTS?id={id}&type={type}"
            PARTICIPANTS -> "PARTICIPANTS?meet={meet}"
            USER -> "USER?user={user}&meet={meet}"
            RESPONDS -> "RESPONDS?meet={meet}"
            MEET -> "MEET?meet={meet}"
            OBSERVERS -> "OBSERVERS"
        }
    ) {
        
        composable("OBSERVERS") {
            //TODO экран наблюдателей
            nav.popBackStack()
        }
        
        composable(
            route = "RESPONDS?meet={meet}",
            listOf(
                setArg("meet", "$meetId")
            )
        ) {
            it.GetArg("meet") {
                //TODO экран откликов
                nav.popBackStack()
            }
        }
        
        composable(
            route = "MEET?meet={meet}",
            arguments = listOf(
                setArg("meet", "$meetId")
            )
        ) { stack ->
            stack.GetArg("meet") { meet ->
                Connector<MeetingViewModel>(vm.scope) {
                    MeetingBs(it, meet, nav)
                }
            }
        }
        
        composable(
            route = "USER?user={user}&meet={meet}",
            arguments = listOf(
                setArg("user", "$userId"),
                setArg("meet", "$meetId")
            )
        ) { stack ->
            stack.GetArg("user") { user ->
                stack.GetArg("meet") { meet ->
                    Connector<OrganizerViewModel>(vm.scope) {
                        OrganizerBs(it, user, meet, nav)
                    }
                }
            }
        }
        
        composable(
            route = "PARTICIPANTS?meet={meet}",
            arguments = listOf(
                setArg("meet", "$meetId")
            )
        ) { stack ->
            stack.GetArg("meet") { meet ->
                Connector<ParticipantsViewModel>(vm.scope) {
                    ParticipantsBs(it, meet, nav)
                }
            }
        }
        
        composable(
            route = "REPORTS?id={id}&type={type}",
            arguments = listOf(
                setArg("id", "$reportObject"),
                setArg("type", "$reportType")
            )
        ) { stack ->
            stack.GetArg("id") { id ->
                stack.GetArg("type") { type ->
                    Connector<ReportsViewModel>(vm.scope) {
                        ReportsBs(it, type, id, nav)
                    }
                }
            }
        }
        
    }
}

@Composable
private fun NavBackStackEntry.GetArg(
    arg: String,
    block: @Composable (String) -> Unit,
) = arguments?.getString(arg)?.let { block(it) }

private fun setArg(
    arg: String,
    default: String,
) = navArgument(arg) {
    type = StringType
    defaultValue = default
}