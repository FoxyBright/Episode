package ru.rikmasters.gilty.bottomsheet.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType.Companion.BoolType
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
            MEET, SHORT_MEET -> "MEET?meet={meet}&detailed={detailed}"
            PARTICIPANTS -> "PARTICIPANTS?meet={meet}"
            REPORTS -> "REPORTS?id={id}&type={type}"
            USER -> "USER?user={user}&meet={meet}"
            RESPONDS -> "RESPONDS?meet={meet}"
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
                setStringArg("meet", "$meetId")
            )
        ) {
            it.GetStringArg("meet") {
                //TODO экран откликов
                nav.popBackStack()
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
                    Connector<MeetingViewModel>(vm.scope) {
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
                    Connector<OrganizerViewModel>(vm.scope) {
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
                Connector<ParticipantsViewModel>(vm.scope) {
                    ParticipantsBs(it, meet, nav)
                }
            }
        }
        
        composable(
            route = "REPORTS?id={id}&type={type}",
            arguments = listOf(
                setStringArg("id", "$reportObject"),
                setStringArg("type", "$reportType")
            )
        ) { stack ->
            stack.GetStringArg("id") { id ->
                stack.GetStringArg("type") { type ->
                    Connector<ReportsViewModel>(vm.scope) {
                        ReportsBs(it, type, id, nav)
                    }
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

private fun setBooleanArg(
    arg: String,
    default: Boolean,
) = navArgument(arg) {
    this.type = BoolType
    defaultValue = default
}