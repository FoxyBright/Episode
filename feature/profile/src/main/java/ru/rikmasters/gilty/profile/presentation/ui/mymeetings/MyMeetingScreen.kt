package ru.rikmasters.gilty.profile.presentation.ui.mymeetings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.lists.ParticipantsList
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun MyMeetingScreen(nav: NavState = get()) {
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val meet = DemoFullMeetingModel
    var menuState by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    MyMeeting(
        Modifier,
        MyMeetingState(
            meet, DemoMemberModelList,
            18, meet.duration, menuState, alert
        ), object : MyMeetingCallback {
            override fun onConfirm() {
                Toast.makeText(
                    context,
                    "ВААААУ, Типа поделился) Съешь пирожок с полки",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun menuCollapse(it: Boolean) {
                menuState = it
            }

            override fun menuItemClick(point: Int) {
                menuState = false
                scope.launch {
                    asm.bottomSheetState.expand {
                        ComplainsContent(DemoFullMeetingModel) {
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }; alert = true
                        }
                    }
                }
            }

            override fun closeAlert() {
                alert = false
            }

            override fun onCloseClick() {
                nav.navigate("main")
            }

            override fun onAllWatchClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        ParticipantsList(
                            DemoFullMeetingModel,
                            DemoMemberModelList,
                            Modifier, {
                                scope.launch {
                                    asm.bottomSheetState.collapse()
                                }
                            }, { nav.navigate("organizer") }
                        )
                    }
                }
            }

            override fun onMemberClick() {
                scope.launch {
                    asm.bottomSheetState.collapse()
                }
            }

            override fun openMap() {
                Toast.makeText(
                    context,
                    "К сожалению карты пока не доступны",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
}