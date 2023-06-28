package ru.rikmasters.gilty.translation.streamer.ui

import android.content.res.Configuration
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.input.pointer.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
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
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
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
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState
import ru.rikmasters.gilty.translation.streamer.model.SurfaceState
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationStreamerViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TranslationStreamerScreen(
    vm: TranslationStreamerViewModel,
    translationId: String,
    selectedFacing: StreamerFacing
) {

    val asm = get<AppStateModel>()

    /**
     * Configurations, system, design
     */
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
     * States from vm
     */
    val translation by vm.translation.collectAsState()
    val status by vm.translationStatus.collectAsState()
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
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    /**
     * Camera
     */
    val bitrateAdapter by remember { mutableStateOf<BitrateAdapter?>(null) }
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }
    var currentOpenGlView by remember { mutableStateOf<OpenGlView?>(null) }
    val surfaceState by vm.surfaceState.collectAsState()


    var orientation by remember { mutableStateOf(configuration.orientation) }
    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                asm.systemUiInsets.isSystemBarsVisible = it == Configuration.ORIENTATION_PORTRAIT
                asm.systemUi.setSystemBarsColor(newBackgroundColor, darkIcons = false)
                if (orientation != it) {
                    translation?.rtmp?.let { rtmp ->
                        Log.d("TEST","Broadcast started ORIENT $rtmp")
                        restartBroadCast(
                            rtmp,
                            camera,
                            context,
                            cameraState,
                            currentOpenGlView,
                            translation?.thumbnail,
                            scope,
                            microphoneState,
                            facing
                        )
                    }
                }
                orientation = it
            }
    }

    /**
     * Enter/leave screen
     */
    DisposableEffect(Unit) {
        vm.onEvent(TranslationEvent.Initialize(translationId, selectedFacing))
        asm.systemUi.setSystemBarsColor(newBackgroundColor, darkIcons = false)
        onDispose {
            if (isInDarkTheme) {
                asm.systemUi.setSystemBarsColor(backgroundColor, darkIcons = false)
            } else {
                asm.systemUi.setSystemBarsColor(backgroundColor, darkIcons = true)
            }
            destroyRTMP(camera)
            vm.onEvent(TranslationEvent.Dismiss)
        }
    }

    /**
     * Enter background / enter foreground
     */
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                vm.onEvent(TranslationEvent.EnterForeground(translationId))
            }

            Lifecycle.Event.ON_PAUSE -> {
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
                        toggleCamera(
                            event.value,
                            camera,
                            context,
                            currentOpenGlView,
                            translation?.thumbnail,
                            scope
                        )
                    }
                    is TranslationOneTimeEvent.ToggleMicrophone -> {
                        toggleMicrophone(event.value, camera)
                    }
                    is TranslationOneTimeEvent.OnError -> {
                        context.errorToast(
                            event.message
                        )
                    }
                    TranslationOneTimeEvent.CompleteTranslation -> {
                        showExitDialog = false
                    }
                    TranslationOneTimeEvent.ShowSnackbar -> {
                        scope.launch {
                            isShowSnackbar = true
                            delay(2000)
                            isShowSnackbar = false
                            if (snackbarState == StreamerSnackbarState.WEAK_CONNECTION) {
                                vm.onEvent(TranslationEvent.ClearSnackbarState)
                            }
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
                            Log.d("TEST","Start streaming ${event.url}")
                            startBroadCast(
                                event.url,
                                camera, context, facing
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit, cameraState, translation, snackbarState) {
        if (snackbarState == StreamerSnackbarState.WEAK_CONNECTION) {
            toggleCamera(false, camera, context, currentOpenGlView, translation?.thumbnail, scope)
        } else {
            toggleCamera(
                cameraState,
                camera,
                context,
                currentOpenGlView,
                translation?.thumbnail,
                scope
            )
        }
    }
    LaunchedEffect(Unit, microphoneState, translation, snackbarState) {
        toggleMicrophone(microphoneState, camera)
    }

    var lowBitrateCount by remember { mutableStateOf(0) }

    LaunchedEffect(lowBitrateCount) {
        if (lowBitrateCount > 3) {
            vm.onEvent(TranslationEvent.LowBitrate)
            lowBitrateCount = 0
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
                if (cameraState) {
                    if (bitrate < 400_000L) {
                        lowBitrateCount += 1
                    } else {
                        lowBitrateCount = 0
                    }
                } else {
                    lowBitrateCount = 0
                }
                bitrateAdapter?.adaptBitrate(bitrate)
            }
        }
    }

    val surfaceHolderCallback = remember {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.CREATED))
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.CHANGED))
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopPreview(camera)
                vm.onEvent(TranslationEvent.ChangeSurfaceState(SurfaceState.DESTROYED))
            }
        }
    }

    var currentZoom by remember { mutableStateOf(1f) }

    /**
     * Back pressed handler
     */
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
                modifier = Modifier
                    .align(Alignment.End)
                    .background(color = Color.Transparent),
                state = bottomSheetState,
                configuration = configuration,
                messagesList = messages,
                onSendMessage = { text ->
                    if (hudState == null) {
                        vm.onEvent(
                            TranslationEvent.SendMessage(
                                text = text
                            )
                        )
                    }
                },
                membersCount = membersCount,
                searchValue = query,
                onSearchValueChange = { newQuery ->
                    if (hudState == null) {
                        vm.onEvent(
                            TranslationEvent.UserBottomSheetQueryChanged(
                                newQuery = newQuery
                            )
                        )
                    }
                },
                membersList = members,
                onComplainClicked = {
                    if (hudState == null) {
                        currentComplainUser = it
                        showComplainDialog = true
                    }
                },
                onDeleteClicked = {
                    if (hudState == null) {
                        currentDeleteUser = it
                        showKickDialog = true
                    }
                },
                onAppendDurationSave = { minutes ->
                    if (hudState == null) {
                        scope.launch {
                            modalBottomSheetState.hide()
                            vm.onEvent(
                                TranslationEvent.AppendTranslation(
                                    appendMinutes = minutes
                                )
                            )
                        }
                    }
                },
                isOrganizer = true,
                query = query,
                userId = meeting?.organizer?.id
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
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        // Обработка жестов масштабирования, вращения и перемещения
                        if (cameraState) {
                            val newZoom = currentZoom * zoom
                            if (camera?.zoomRange?.contains(newZoom) == true) {
                                currentZoom *= zoom
                                camera?.zoom = currentZoom
                            }
                        }
                    }
                }
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
                        bottomSheetState = TranslationBottomSheetState.CHAT
                        modalBottomSheetState.show()
                    }
                },
                onMembersClicked = {
                    scope.launch {
                        bottomSheetState = TranslationBottomSheetState.USERS
                        modalBottomSheetState.show()
                    }
                },
                membersCount = membersCount,
                onTimerClicked = {
                    scope.launch {
                        bottomSheetState = TranslationBottomSheetState.DURATION
                        modalBottomSheetState.show()
                    }
                },
                onCloseClicked = {
                    showExitDialog = true
                },
                bsOpened = modalBottomSheetState.isVisible,
                cameraEnabled = cameraState,
                microphoneEnabled = microphoneState,
                onCameraClicked = {
                    if (hudState == null) {
                        vm.onEvent(TranslationEvent.ToggleCamera)
                    }
                },
                onMicrophoneClicked = {
                    if (hudState == null) {
                        vm.onEvent(TranslationEvent.ToggleMicrophone)
                    }
                },
                changeFacing = {
                    if (cameraState && hudState == null) {
                        vm.onEvent(TranslationEvent.ChangeFacing)
                    }
                },
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
                AnimatedVisibility(
                    visible = hudState == StreamerHUD.RECONNECTING,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
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
                AnimatedVisibility(
                    visible = hudState == StreamerHUD.RECONNECT_FAILED,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NoConnection(
                        onReconnectCLicked = {
                            vm.onEvent(TranslationEvent.Reconnect)
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