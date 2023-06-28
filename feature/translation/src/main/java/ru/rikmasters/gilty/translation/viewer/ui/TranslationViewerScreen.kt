package ru.rikmasters.gilty.translation.viewer.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
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
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.bottoms.BottomSheetStateManager
import ru.rikmasters.gilty.translation.bottoms.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.shared.components.NoConnection
import ru.rikmasters.gilty.translation.shared.components.Paused
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TranslationViewerScreen(
    vm: TranslationViewerViewModel,
    translationId: String
) {
    val asm = get<AppStateModel>()

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
    val nav = get<NavState>()

    /**
     * WebRTC
     */
    val webRtcClient = remember { WebRtcClient(context) }
    val remoteVideoTrackState by webRtcClient.remoteVideoSinkFlow.collectAsState(null)
    //val audioLevel by webRtcClient.audioLevel.collectAsState(initial = 0.0)

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
    val inactiveBg by vm.inactiveBg.collectAsState()

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
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    /**
     * Orientation
     */
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                asm.systemUiInsets.isSystemBarsVisible = it == Configuration.ORIENTATION_PORTRAIT
                asm.systemUi.setSystemBarsColor(newBackgroundColor, darkIcons = false)
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
        asm.systemUi.setSystemBarsColor(newBackgroundColor, darkIcons = false)
        onDispose {
            webRtcClient.disconnect()
            vm.onEvent(TranslationViewerEvent.Dismiss)
            if (isInDarkTheme) {
                asm.systemUi.setSystemBarsColor(backgroundColor, darkIcons = false)
            } else {
                asm.systemUi.setSystemBarsColor(backgroundColor, darkIcons = true)
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
                        context.errorToast(
                            event.message
                        )
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

    ModalBottomSheetLayout(
        modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Modifier
                .systemBarsPadding()
                .navigationBarsPadding()
                .background(color = Color.Transparent)
        } else {
            Modifier
                .background(color = Color.Transparent)
        },
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
                onComplainClicked = {
                    currentComplainUser = it
                    showComplainDialog = true
                },
                onDeleteClicked = {},
                onAppendDurationSave = {},
                isOrganizer = false,
                query = query,
                userId = translation?.userId
            )
        },
        sheetState = modalBottomSheetState,
        scrimColor = Color.Transparent,
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
        sheetContentColor = Color.Transparent,
        sheetElevation = 0.dp
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
                        bottomSheetState = TranslationBottomSheetState.CHAT
                        modalBottomSheetState.show()
                    }
                },
                onMembersCountClicked = {
                    scope.launch {
                        bottomSheetState = TranslationBottomSheetState.USERS
                        modalBottomSheetState.show()
                    }
                },
                onCloseClicked = { showExitDialog = true },
                isTimerHighlighted = highlightTimer,
                addTimerString = additionalTime,
                onToChatPressed = { nav.navigationBack() },
                isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                inactiveBg = inactiveBg
            )

            /**
             * Connection placeholders
             */
            if (customHUDState == null && !inactiveBg) {
                when (hudState) {
                    ViewerHUD.RECONNECTING -> {
                        Reconnecting(
                            modifier = if (modalBottomSheetState.isVisible && orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
                            modifier = if (modalBottomSheetState.isVisible && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                Modifier
                                    .fillMaxHeight()
                                    .width((configuration.screenWidthDp * 0.6).dp)
                            } else {
                                Modifier.fillMaxSize()
                            }
                        )
                    }
                    ViewerHUD.PAUSED -> {
                        Paused(
                            modifier = if (modalBottomSheetState.isVisible && orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
            }

            /**
             * Snackbars / camera/micro placeholders
             */
            OnTopSnackbarsPlacehodlers(
                orientation = orientation,
                bsOpened = modalBottomSheetState.isVisible,
                isShowPlacehodlers = isShowPlaceholder,
                isShowSnackbar = isShowSnackbar,
                cameraState = cameraState,
                microphoneState = microphoneState,
                meeting = meeting,
                snackbarState = snackbarState,
                configuration = configuration,
                hudState = hudState,
                context = context
            )

            /**
             * Dialogs
             */
            TranslationStreamerDialog(
                type = TranslationDialogType.COMPLAIN,
                show = showComplainDialog,
                onSuccess = {
                    showComplainDialog = false
                    scope.launch {
                        asm.bottomSheet.expand {
                            BottomSheet(
                                vm.scope,
                                BsType.REPORTS,
                                reportObject = currentComplainUser?.id,
                                reportType = ReportObjectType.PROFILE
                            )
                        }
                    }
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

