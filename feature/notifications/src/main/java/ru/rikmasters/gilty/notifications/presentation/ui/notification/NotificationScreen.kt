package ru.rikmasters.gilty.notifications.presentation.ui.notification

import android.widget.Toast
import androidx.compose.runtime.*
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
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.notification.*
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
                DemoNotificationMeetingOverModel,
                DemoTodayNotificationRespondAccept,
                DemoTodayNotificationRespondAccept,
                DemoNotificationMeetingOverModel,
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
                    scope.launch { asm.bottomSheet.collapse() }
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
                    scope.launch { asm.bottomSheet.collapse() }
            }
        }

        repeat(memberList.size) { memberListWrap.add(false) }
        NotificationsContent(
            NotificationsState(
                notificationsList,
                DemoMeetingModel, respondsList.size, stateList,
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
                        when (point) {
                            0 -> nav.navigateAbsolute("main/meetings")
                            1 -> nav.navigateAbsolute("notification/list")
                            2 -> nav.navigateAbsolute("addmeet/category")
                            3 -> nav.navigateAbsolute("chats/main")
                            4 -> nav.navigateAbsolute("profile/main")
                        }
                    }
                }

                override fun onRespondsClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
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