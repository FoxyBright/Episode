package ru.rikmasters.gilty.translation.streamer.ui

import android.content.res.Configuration
import android.util.Log
import android.view.SurfaceHolder
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
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.util.BitrateAdapter
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.components.NoConnection
import ru.rikmasters.gilty.translation.shared.components.Reconnecting
import ru.rikmasters.gilty.translation.shared.components.TranslationDialogType
import ru.rikmasters.gilty.translation.shared.components.TranslationStreamerDialog
import ru.rikmasters.gilty.translation.bottoms.BottomSheetStateManager
import ru.rikmasters.gilty.translation.bottoms.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.shared.utils.OnLifecycleEvent
import ru.rikmasters.gilty.translation.shared.utils.destroyRTMP
import ru.rikmasters.gilty.translation.shared.utils.map
import ru.rikmasters.gilty.translation.shared.utils.restartBroadCast
import ru.rikmasters.gilty.translation.shared.utils.restartPreview
import ru.rikmasters.gilty.translation.shared.utils.startBroadCast
import ru.rikmasters.gilty.translation.shared.utils.stopBroadcast
import ru.rikmasters.gilty.translation.shared.utils.stopPreview
import ru.rikmasters.gilty.translation.shared.utils.toggleCamera
import ru.rikmasters.gilty.translation.shared.utils.toggleMicrophone
import ru.rikmasters.gilty.translation.streamer.event.TranslationEvent
import ru.rikmasters.gilty.translation.streamer.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus.CONNECTED
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus.FAILED
import ru.rikmasters.gilty.translation.streamer.model.StreamerCustomHUD
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD
import ru.rikmasters.gilty.translation.streamer.model.SurfaceState
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationStreamerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationStreamerScreen(
    vm: TranslationStreamerViewModel,
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
    val scaffoldState = rememberBottomSheetScaffoldState()
    val nav = get<NavState>()

    /**
     * States from vm
     */
    val translation by vm.translation.collectAsState()
    val meeting by vm.meeting.collectAsState()
    val microphoneState by vm.microphone.collectAsState()
    val cameraState by vm.camera.collectAsState()
    val hudState by vm.hudState.collectAsState()
    val facing by vm.facing.collectAsState()
    val snackbarState by vm.streamerSnackbarState.collectAsState()
    val isShowPlaceholder by vm.placeHolderVisible.collectAsState()
    val membersCount by vm.membersCount.collectAsState()
    val retryCount by vm.retryCount.collectAsState()
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
    var showKickDialog by remember { mutableStateOf(false) }
    var currentDeleteUser by remember { mutableStateOf<FullUserModel?>(null) }
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
     * Camera
     */
    val bitrateAdapter by remember { mutableStateOf<BitrateAdapter?>(null) }
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }
    var currentOpenGlView by remember { mutableStateOf<OpenGlView?>(null) }
    val surfaceState by vm.surfaceState.collectAsState()

    /**
     * Connection
     */
    var bitrateCalc by remember { mutableStateOf(false) }

    /**
     * Orientation
     */
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                orientation = it
                if (surfaceState == SurfaceState.CHANGED) {
                    translation?.rtmp?.let {
                        restartBroadCast(it, camera, context, facing)
                    }
                }
            }
    }

    /**
     * Enter/leave screen
     */
    DisposableEffect(Unit) {
        Log.d("TEST","INIT FR")
        vm.onEvent(TranslationEvent.Initialize(translationId))
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            Log.d("TEST","DISP FR")
            destroyRTMP(camera)
            vm.onEvent(TranslationEvent.Dismiss)
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    /**
     * Enter background / enter foreground
     */
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Log.d("TEST","RESUME FR")
                vm.onEvent(TranslationEvent.EnterForeground(translationId))
            }

            Lifecycle.Event.ON_PAUSE -> {
                Log.d("TEST","PAUSE FR")
                vm.onEvent(TranslationEvent.EnterBackground)
            }

            else -> {}
        }
    }

    /**
     * Collect one-time events
     */
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.oneTimeEvent.collectLatest { event ->
                when (event) {
                    is TranslationOneTimeEvent.ChangeFacing -> {
                        camera?.let {
                            if (it.cameraFacing.map() != event.facing) {
                                it.switchCamera()
                            }
                        }
                    }

                    is TranslationOneTimeEvent.ToggleCamera -> {
                        toggleCamera(event.value, camera, context, currentOpenGlView)
                    }

                    is TranslationOneTimeEvent.ToggleMicrophone -> {
                        toggleMicrophone(event.value, camera)
                    }

                    is TranslationOneTimeEvent.OnError -> {
                        //TODO: Тост с ошибкой
                    }

                    TranslationOneTimeEvent.CompleteTranslation -> {
                        showExitDialog = false
                    }

                    TranslationOneTimeEvent.ShowSnackbar -> {
                        scope.launch {
                            isShowSnackbar = true
                            delay(2000)
                            isShowSnackbar = false
                        }
                    }

                    TranslationOneTimeEvent.Reconnect -> {
                        camera?.setReTries(retryCount)
                        camera?.reTry(1000, "")
                        vm.onEvent(TranslationEvent.DecreaseRetryCount)
                    }

                    TranslationOneTimeEvent.DestroyRTMP -> {
                        stopBroadcast(camera)
                    }

                    is TranslationOneTimeEvent.StartStreaming -> {
                        if (surfaceState == SurfaceState.CHANGED) {
                            restartPreview(camera, facing, context)
                            startBroadCast(
                                rtmpUrl = event.url,
                                camera = camera,
                                context = context
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Camera callback
     */
    val connectionChecker = remember {
        object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
            override fun onConnectionFailedRtmp(reason: String) {
                vm.onEvent(TranslationEvent.ProcessRTMPStatus(FAILED))
            }

            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {
                vm.onEvent(TranslationEvent.ProcessRTMPStatus(CONNECTED))
            }

            override fun onDisconnectRtmp() {}
            override fun onNewBitrateRtmp(bitrate: Long) {
                bitrateAdapter?.adaptBitrate(bitrate)
            }
        }
    }
    val surfaceHolderCallback = remember {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d("TEST", "SURFF CREATED")
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.CREATED))
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.d("TEST", "SURFF CHANGED")
                restartPreview(camera, facing, context)
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.CHANGED))
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.d("TEST", "SURFF DESTROYED")
                stopPreview(camera)
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.DESTROYED))
            }
        }
    }

    /**
     * Back pressed handler
     */
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
                onSendMessage = { text ->
                    vm.onEvent(
                        TranslationEvent.SendMessage(
                            text = text
                        )
                    )
                },
                membersCount = membersCount,
                searchValue = query,
                onSearchValueChange = { newQuery ->
                    vm.onEvent(
                        TranslationEvent.UserBottomSheetQueryChanged(
                            newQuery = newQuery
                        )
                    )
                },
                membersList = members,
                onComplainClicked = {
                    currentComplainUser = it
                    showComplainDialog = true
                },
                onDeleteClicked = {
                    currentDeleteUser = it
                    showKickDialog = true
                },
                onAppendDurationSave = { minutes ->
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                        vm.onEvent(
                            TranslationEvent.AppendTranslation(
                                appendMinutes = minutes
                            )
                        )
                    }
                },
                isOrganizer = true
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
            TranslationStreamerContent(
                initCamera = { view ->
                    currentOpenGlView = view
                    if (camera == null) {
                        camera = RtmpCamera2(view, connectionChecker)
                    }
                },
                surfaceHolderCallback = surfaceHolderCallback,
                isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                meetingModel = meeting,
                remainTime = remainTime,
                isHighlightTimer = highlightTimer,
                addTimerString = additionalTime,
                onChatClicked = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            vm.onEvent(
                                TranslationEvent.ChatBottomSheetOpened(
                                    isOpened = true
                                )
                            )
                            bottomSheetState = TranslationBottomSheetState.CHAT
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                onMembersClicked = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            vm.onEvent(
                                TranslationEvent.UserBottomSheetOpened(
                                    isOpened = true
                                )
                            )
                            bottomSheetState = TranslationBottomSheetState.USERS
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                membersCount = membersCount,
                onTimerClicked = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            vm.onEvent(
                                TranslationEvent.UserBottomSheetOpened(
                                    isOpened = true
                                )
                            )
                            bottomSheetState = TranslationBottomSheetState.DURATION
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                onCloseClicked = {
                    if (scaffoldState.bottomSheetState.isCollapsed) {
                        showExitDialog = true
                    } else {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                },
                bsOpened = scaffoldState.bottomSheetState.isExpanded,
                cameraEnabled = cameraState,
                microphoneEnabled = microphoneState,
                onCameraClicked = { vm.onEvent(TranslationEvent.ToggleCamera) },
                onMicrophoneClicked = { vm.onEvent(TranslationEvent.ToggleMicrophone) },
                changeFacing = { vm.onEvent(TranslationEvent.ChangeFacing) },
                onToChatPressed = {
                    camera?.stopStream()
                    camera?.stopPreview()
                    nav.navigationBack()
                },
                customHUDState = customHUDState
            )

            /**
             * Connection placeholders
             */
            if (customHUDState == null) {
                when (hudState) {
                    StreamerHUD.RECONNECTING -> {
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

                    StreamerHUD.RECONNECT_FAILED -> {
                        NoConnection(
                            onReconnectCLicked = {
                                vm.onEvent(TranslationEvent.Reconnect)
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
                configuration = configuration,
                hudState = hudState
            )


            /**
             * Dialogs
             */
            TranslationStreamerDialog(
                type = TranslationDialogType.KICK,
                show = showKickDialog,
                onSuccess = {
                    currentDeleteUser?.let {
                        vm.onEvent(
                            TranslationEvent.KickUser(
                                user = it
                            )
                        )
                    }
                    showKickDialog = false
                },
                dismiss = { showKickDialog = false }
            )
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
                    if (customHUDState == StreamerCustomHUD.EXPIRED) {
                        vm.onEvent(TranslationEvent.CompleteTranslation)
                        showExitDialog = false
                    } else {
                        nav.navigationBack()
                        showExitDialog = false
                    }
                },
                dismiss = { showExitDialog = false }
            )
        }
    }
}