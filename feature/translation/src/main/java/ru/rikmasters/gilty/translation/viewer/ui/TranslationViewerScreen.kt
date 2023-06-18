package ru.rikmasters.gilty.translation.viewer.ui

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.bottoms.BottomSheetStateManager
import ru.rikmasters.gilty.translation.bottoms.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.shared.components.NoConnection
import ru.rikmasters.gilty.translation.shared.components.Reconnecting
import ru.rikmasters.gilty.translation.shared.components.TranslationDialogType
import ru.rikmasters.gilty.translation.shared.components.TranslationStreamerDialog
import ru.rikmasters.gilty.translation.shared.utils.OnLifecycleEvent
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerEvent
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerOneTimeEvents
import ru.rikmasters.gilty.translation.viewer.model.ViewerHUD
import ru.rikmasters.gilty.translation.viewer.viewmodel.TranslationViewerViewModel
import ru.rikmasters.gilty.translations.webrtc.WebRtcClient
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationViewerScreen(
    vm: TranslationViewerViewModel,
    translationId: String
) {

    /**
     * Configurations, system, design
     */
    val systemUiController = rememberSystemUiController()
    val isInDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val newBackgroundColor = ThemeExtra.colors.preDarkColor
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val nav = get<NavState>()

    /**
     * WebRTC
     */
    val webRtcClient = remember { WebRtcClient(context) }
    val remoteVideoTrackState by webRtcClient.remoteVideoSinkFlow.collectAsState(null)

    /**
     * States from vm
     */
    val translation by vm.translation.collectAsState()
    val isStreaming by vm.isStreaming.collectAsState()
    val meeting by vm.meeting.collectAsState()
    val microphoneState by vm.microphoneState.collectAsState()
    val cameraState by vm.cameraState.collectAsState()
    val hudState by vm.hudState.collectAsState()
    val snackbarState by vm.viewerSnackbarState.collectAsState()
    val isShowPlaceholder by vm.placeHolderVisible.collectAsState()
    val membersCount by vm.membersCount.collectAsState()
    val customHUDState by vm.customHUDState.collectAsState()
    val messages = vm.messages.collectAsLazyPagingItems()
    val members = vm.members.collectAsLazyPagingItems()
    val query by vm.query.collectAsState()

    /**
     * Time
     */
    val additionalTime by vm.additionalTime.collectAsState()
    val highlightTimer by vm.timerHighlighted.collectAsState()
    val remainTime by vm.remainTime.collectAsState()

    /**
     * Dialogs
     */
    var showComplainDialog by remember { mutableStateOf(false) }
    var currentComplainUser by remember { mutableStateOf<FullUserModel?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    /**
     * Snackbars
     */
    var isShowSnackbar by remember { mutableStateOf(false) }

    /**
     * BottomSheetState
     */
    var bottomSheetState by remember { mutableStateOf(TranslationBottomSheetState.CHAT) }

    /**
     * Orientation
     */
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                orientation = it
            }
    }

    /**
     * Enter background / enter foreground
     */
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                vm.onEvent(TranslationViewerEvent.EnterForeground(translationId))
            }

            Lifecycle.Event.ON_PAUSE -> {
                vm.onEvent(TranslationViewerEvent.EnterBackground)
            }

            else -> {}
        }
    }

    /**
     * Enter/leave screen
     */
    DisposableEffect(Unit) {
        vm.onEvent(TranslationViewerEvent.Initialize(translationId))
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            webRtcClient.disconnect()
            vm.onEvent(TranslationViewerEvent.Dismiss)
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    /**
     * Collect one-time events
     */
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.translationViewerOneTimeEvents.collectLatest { event ->
                when (event) {
                    is TranslationViewerOneTimeEvents.OnError -> {
                        //TODO: onError
                    }

                    is TranslationViewerOneTimeEvents.ConnectToStream -> {
                        webRtcClient.connecting(WebRtcConfig(event.wsUrl))
                    }

                    TranslationViewerOneTimeEvents.DisconnectWebRtc -> {
                        webRtcClient.disconnect()
                    }

                    TranslationViewerOneTimeEvents.ShowSnackbar -> {
                        scope.launch {
                            isShowSnackbar = true
                            delay(2000)
                            isShowSnackbar = false
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            webRtcClient.status.collectLatest { status ->
                Log.d("TEST","NEw STatus $status")
                vm.onEvent(
                    TranslationViewerEvent.HandleWebRtcStatus(
                        status = status
                    )
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            webRtcClient.webRtcAnswer.collectLatest { answer ->
                Log.d("TEST","NEw Answer $answer")
                vm.onEvent(
                    TranslationViewerEvent.HandleWebRtcAnswer(
                        answer = answer
                    )
                )
            }
        }
    }

    BackHandler {
        showExitDialog = true
    }

    BottomSheetScaffold(
        sheetContent = {
            BottomSheetStateManager(
                modifier = Modifier.align(Alignment.End),
                state = bottomSheetState,
                configuration = configuration,
                messagesList = messages,
                onSendMessage = { vm.onEvent(TranslationViewerEvent.MessageSent(it)) },
                membersCount = membersCount,
                searchValue = query,
                onSearchValueChange = { vm.onEvent(TranslationViewerEvent.QueryChanged(it)) },
                membersList = members,
                onComplainClicked = { showComplainDialog = true },
                onDeleteClicked = {},
                onAppendDurationSave = {}
            )
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetBackgroundColor = Color.Transparent,
        sheetShape = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RoundedCornerShape(
                topStart = 24.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    scope.launch {
                        if (!scaffoldState.bottomSheetState.isCollapsed) {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            TranslationViewerContent(
                customHUDState = customHUDState,
                videoTrack = remoteVideoTrackState,
                eglBaseContext = webRtcClient.eglBaseContext,
                membersCount = membersCount,
                meetingModel = meeting,
                remainTime = remainTime,
                onChatClicked = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            vm.onEvent(TranslationViewerEvent.ConnectToChat)
                            bottomSheetState = TranslationBottomSheetState.CHAT
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            vm.onEvent(TranslationViewerEvent.DisconnectFromChat)
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                onMembersCountClicked = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            vm.onEvent(TranslationViewerEvent.ReloadMembers)
                            bottomSheetState = TranslationBottomSheetState.USERS
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                onCloseClicked = { showExitDialog = true },
                isTimerHighlighted = highlightTimer,
                addTimerString = additionalTime,
                onToChatPressed = { nav.navigationBack() },
                isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                initialize = {
                    //TODO: WHY
                }
            )

            /**
             * Connection placeholders
             */
            when (hudState) {
                ViewerHUD.RECONNECTING -> {
                    Reconnecting(
                        modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Modifier
                                .fillMaxHeight()
                                .width((configuration.screenWidthDp * 0.6).dp)
                        } else {
                            Modifier.fillMaxSize()
                        }
                    )
                }

                ViewerHUD.RECONNECT_FAILED -> {
                    NoConnection(
                        onReconnectCLicked = {
                            webRtcClient.retry = 0
                            vm.onEvent(TranslationViewerEvent.Reconnect)
                        },
                        modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Modifier
                                .fillMaxHeight()
                                .width((configuration.screenWidthDp * 0.6).dp)
                        } else {
                            Modifier.fillMaxSize()
                        }
                    )
                }

                else -> {}
            }

            /**
             * Snackbars / camera/micro placeholders
             */
            OnTopSnackbarsPlacehodlers(
                orientation = orientation,
                bsOpened = !scaffoldState.bottomSheetState.isCollapsed,
                isShowPlacehodlers = isShowPlaceholder,
                isShowSnackbar = isShowSnackbar,
                cameraState = cameraState,
                microphoneState = microphoneState,
                meeting = meeting,
                snackbarState = snackbarState,
                configuration = configuration
            )

            /**
             * Dialogs
             */
            TranslationStreamerDialog(
                type = TranslationDialogType.COMPLAIN,
                show = showComplainDialog,
                onSuccess = {
                    //TODO: complain
                },
                dismiss = { showComplainDialog = false }
            )
            TranslationStreamerDialog(
                type = TranslationDialogType.EXIT,
                show = showExitDialog,
                onSuccess = {
                    nav.navigationBack()
                    showExitDialog = false
                },
                dismiss = { showExitDialog = false }
            )
        }
    }
}

