package ru.rikmasters.gilty.notifications.presentation.ui.notification

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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.notifications.presentation.ui.responds.NotificationRespondsContent
import ru.rikmasters.gilty.notifications.presentation.ui.responds.NotificationRespondsState
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.common.extentions.MeetSeparate
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoTodayNotificationMeetingOver
import ru.rikmasters.gilty.shared.model.notification.DemoTodayNotificationRespondAccept
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun NotificationsScreen(nav: NavState = get()) {
    GiltyTheme {
        val asm = get<AppStateModel>()
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var activeNotification by
        remember { mutableStateOf<NotificationModel?>(null) }
        var blur by remember { mutableStateOf(true) }
        val notificationsList = remember {
            mutableStateListOf(
                DemoNotificationLeaveEmotionModel,
                DemoNotificationMeetingOverModel,
                DemoTodayNotificationRespondAccept,
                DemoTodayNotificationMeetingOver,
            )
        }
        val stateList = remember {
            mutableStateListOf(INACTIVE, ACTIVE, INACTIVE, INACTIVE, INACTIVE)
        }
        val memberList by
        remember { mutableStateOf(listOf(DemoMemberModel, DemoMemberModel)) }
        val memberListWrap =
            remember { mutableStateListOf<Boolean>() }
        val respondsList =
            remember {
                mutableStateListOf(
                    DemoReceivedRespondModelWithoutPhoto,
                    DemoReceivedRespondsModel
                )
            }
        val respondListStates =
            remember { mutableStateListOf<Boolean>() }
        repeat(respondsList.size) {
            if (it == 0) respondListStates.add(true) else
                respondListStates.add(false)
        }
        val notificationCallback = object : RespondCallback {
            override fun onCancelClick(respond: RespondModel) {
                respondsList.remove(respond)
                Toast.makeText(
                    context, "Втреча отклонена",
                    Toast.LENGTH_SHORT
                ).show()
                if (respondsList.size == 0)
                    scope.launch { asm.bottomSheetState.collapse() }
            }

            override fun onArrowClick(index: Int) {
                respondListStates[index] = !respondListStates[index]
            }

            override fun onAcceptClick(respond: RespondModel) {
                respondsList.remove(respond)
                Toast.makeText(
                    context, "Втреча принята",
                    Toast.LENGTH_SHORT
                ).show()
                if (respondsList.size == 0)
                    scope.launch { asm.bottomSheetState.collapse() }
            }
        }

        repeat(memberList.size) { memberListWrap.add(false) }
        NotificationsContent(
            NotificationsState(
                notificationsList,
                DemoFullMeetingModel, respondsList.size, stateList,
                blur, activeNotification, memberList, memberListWrap
            ), Modifier, object : NotificationsCallback {
                override fun onClick(notification: NotificationModel) {
                    activeNotification = notification; blur = true
                }

                override fun onParticipantClick(index: Int) {
                    memberListWrap[index] = !memberListWrap[index]
                }

                override fun onBlurClick() {
                    blur = false
                }

                override fun onEmojiClick(emoji: EmojiModel) {
                    Toast.makeText(context, "Emoji!", Toast.LENGTH_SHORT).show()
                }

                override fun onNavBarSelect(point: Int) {
                    repeat(stateList.size) {
                        if (it == point) stateList[it] = ACTIVE
                        else if (stateList[it] != NEW)
                            stateList[it] = INACTIVE
                        if (point == 0) nav.navigateAbsolute("main/meetings")
                    }
                }

                override fun onRespondsClick() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            NotificationRespondsContent(
                                NotificationRespondsState(
                                    MeetSeparate(respondsList),
                                    respondListStates
                                ), Modifier, notificationCallback
                            )
                        }
                    }
                }

                override fun onSwiped(notification: NotificationModel) {
                    if (notificationsList.contains(notification))
                        notificationsList.remove(notification)
                }
            })
    }
}