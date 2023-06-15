package ru.rikmasters.gilty.translation.streamer

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pedro.encoder.input.gl.render.filters.`object`.ImageObjectFilterRender
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.util.BitrateAdapter
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.blur
import ru.rikmasters.gilty.shared.common.extentions.getBitmap
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.logic.BottomSheetStateManager
import ru.rikmasters.gilty.translation.shared.model.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus.CONNECTED
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus.FAILED
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState
import ru.rikmasters.gilty.translation.streamer.model.SurfaceState
import ru.rikmasters.gilty.translation.shared.components.ProfileAvatar
import ru.rikmasters.gilty.translation.shared.components.MicroWave
import ru.rikmasters.gilty.translation.shared.components.MicroInactiveSnackbar
import ru.rikmasters.gilty.translation.shared.components.NoConnection
import ru.rikmasters.gilty.translation.shared.components.Reconnecting
import ru.rikmasters.gilty.translation.shared.components.TranslationDialogType
import ru.rikmasters.gilty.translation.shared.components.TranslationResumedSnackbar
import ru.rikmasters.gilty.translation.shared.components.TranslationStreamerDialog
import ru.rikmasters.gilty.translation.shared.components.WeakConnectionSnackbar
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationViewModel
import ru.rikmasters.gilty.translation.shared.utils.destroyRTMP
import ru.rikmasters.gilty.translation.shared.utils.map
import ru.rikmasters.gilty.translation.shared.utils.restartPreview
import ru.rikmasters.gilty.translation.shared.utils.startBroadCast
import ru.rikmasters.gilty.translation.viewer.presentation.ui.components.OnLifecycleEvent
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationStreamerScreen(
    vm: TranslationViewModel,
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
    val density = LocalDensity.current
    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.roundToPx()
    }
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
    val remainTime by vm.remainTime.collectAsState()
    val additionalTime by vm.additionalTime.collectAsState()
    val snackbarState by vm.streamerSnackbarState.collectAsState()
    val placeholderView by vm.placeHolderVisible.collectAsState()
    val membersCount by vm.membersCount.collectAsState()
    val retryCount by vm.retryCount.collectAsState()
    val customHUDState by vm.customHUDState.collectAsState()
    val messages = vm.messages.collectAsLazyPagingItems()
    val members = vm.members.collectAsLazyPagingItems()
    val query by vm.query.collectAsState()

    /**
     * Dialogs
     */
    var showComplainDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showKickDialog by remember { mutableStateOf(false) }
    var showCompleteEarlierDialog by remember { mutableStateOf(false) }
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
    var glViewChanged by remember { mutableStateOf(false) }

    /**
     * Blur
     */
    var currentBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentBgOffset by remember { mutableStateOf<Float?>(null) }

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
        // do stuff on event
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
     * Enter/leave screen
     */
    DisposableEffect(Unit) {
        vm.onEvent(TranslationEvent.Initialize(translationId))
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            destroyRTMP(camera)
            vm.onEvent(TranslationEvent.Dismiss)
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    val surfaceState by vm.surfaceState.collectAsState()


    /**
     * Camera
     */

    fun toggleCamera(value: Boolean) {
        if (value) {
            camera?.glInterface?.clearFilters()
        } else {
            val imageFilter = ImageObjectFilterRender()
            currentOpenGlView?.getBitmap {
                it?.let { bitmap ->
                    imageFilter.setImage(bitmap.blur(context))
                    camera?.glInterface?.addFilter(imageFilter)
                } ?: run {
                    camera?.glInterface?.muteVideo()
                }
            } ?: run {
                camera?.glInterface?.muteVideo()
            }
        }
    }

    fun toggleMicrophone(value: Boolean) {
        if (value) {
            if (camera?.isAudioMuted == true) {
                camera?.enableAudio()
            }
        } else {
            if (camera?.isAudioMuted == false) {
                camera?.disableAudio()
            }
        }
    }

    /**
     * Status
     */

    fun completeTranslation() {
       // stopBroadcast()
        showCompleteDialog = false
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
                        toggleCamera(event.value)
                    }

                    is TranslationOneTimeEvent.ToggleMicrophone -> {
                        toggleMicrophone(event.value)
                    }

                    is TranslationOneTimeEvent.OnError -> {}
                    TranslationOneTimeEvent.CompleteTranslation -> {
                        completeTranslation()
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
                        Log.d("TEST","DRSTROYING RTMP")
                        destroyRTMP(camera)
                    }
                    is TranslationOneTimeEvent.StartStreaming -> {
                        if (surfaceState == SurfaceState.CHANGED) {
                            Log.d("TEST","STARTING BROADCAST")
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
                /*
                if (customHUDState == StreamerViewState.STREAM) {
                    if (cameraState) {
                        if (bitrate < 300_000) {
                            //vm.onEvent(TranslationEvent.LowBitrate)
                        } else {
                            //vm.onEvent(TranslationEvent.BitrateStabilized)
                        }
                    } else {
                        if (bitrate < 30_000) {
                            //vm.onEvent(TranslationEvent.LowBitrate)
                        } else {
                            //vm.onEvent(TranslationEvent.BitrateStabilized)
                        }
                    }
                }
                 */
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
                restartPreview(camera, facing, context)
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
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

    /**
     * Blur
     */
    LaunchedEffect(Unit) {
        while (true) {
            currentOpenGlView?.getBitmap {
                val blurred = it?.blur(context)
                blurred?.let { blur ->
                    currentBgBitmap = blur
                }
            }
            currentBgOffset = scaffoldState.bottomSheetState.offset.value
            delay(10)
        }
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
                }
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
        modifier = Modifier.fillMaxSize()
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
                // TODO
                isHighlightTimer = false,
                addTimerString = additionalTime,
                onChatClicked = {
                    scope.launch {
                        when {
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == TranslationBottomSheetState.CHAT -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                            }

                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != TranslationBottomSheetState.CHAT -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                                bottomSheetState = TranslationBottomSheetState.CHAT
                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }

                            !scaffoldState.bottomSheetState.isExpanded -> {
                                bottomSheetState = TranslationBottomSheetState.CHAT
                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }
                        }
                    }
                },
                onMembersClicked = {
                    scope.launch {
                        when {
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == TranslationBottomSheetState.USERS -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                            }

                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != TranslationBottomSheetState.USERS -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                                bottomSheetState = TranslationBottomSheetState.USERS
                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }

                            !scaffoldState.bottomSheetState.isExpanded -> {
                                bottomSheetState = TranslationBottomSheetState.USERS
                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }
                        }
                    }
                },
                membersCount = membersCount,
                onTimerClicked = {
                    scope.launch {
                        bottomSheetState = TranslationBottomSheetState.DURATION
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                onCloseClicked = {
                    /*
                    if (viewState == StreamerViewState.PREVIEW_REOPENED || viewState == StreamerViewState.EXPIRED) {
                        showCompleteDialog = true
                    } else {
                        showCompleteEarlierDialog = true
                    }

                     */
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
             * BlurBg
             */
            currentBgBitmap?.let {
                val pxHeight =
                    screenHeightPx - (scaffoldState.bottomSheetState.offset.value.roundToInt())
                val screenPixelDensity = context.resources.displayMetrics.density
                val dpHeight = pxHeight.toFloat() / screenPixelDensity
                val width = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    configuration.screenWidthDp.dp
                } else {
                    (configuration.screenWidthDp * 0.4).dp
                }
                val shape = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                } else {
                    RoundedCornerShape(
                        topStart = 24.dp
                    )
                }
                if (scaffoldState.bottomSheetState.isExpanded) {
                    Image(
                        modifier = Modifier
                            .width(width)
                            .clip(shape)
                            .height(dpHeight.dp)
                            .align(Alignment.BottomEnd),
                        bitmap = it.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.BottomEnd
                    )
                }
            }

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

            Column(
                modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Modifier
                        .fillMaxHeight()
                        .width((configuration.screenWidthDp * 0.6).dp)
                } else {
                    Modifier.fillMaxSize()
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (placeholderView) {
                    if (!cameraState) {
                        ProfileAvatar(meetingModel = meeting, modifier = Modifier)
                        if (!microphoneState) {
                            Spacer(modifier = Modifier.height(8.dp))
                            MicroWave(meetingModel = meeting, modifier = Modifier)
                        }
                    }
                }
                if (isShowSnackbar) {
                    Spacer(modifier = Modifier.height(12.dp))
                    when (snackbarState) {
                        StreamerSnackbarState.MICRO_OFF -> {
                            MicroInactiveSnackbar()
                        }

                        StreamerSnackbarState.WEAK_CONNECTION -> {
                            WeakConnectionSnackbar()
                        }

                        StreamerSnackbarState.BROADCAST_EXTENDED -> {
                            TranslationResumedSnackbar()
                        }

                        else -> {}
                    }
                }
            }


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
                },
                dismiss = { showKickDialog = false }
            )
            TranslationStreamerDialog(
                type = TranslationDialogType.COMPLAIN,
                show = showComplainDialog,
                onSuccess = {},
                dismiss = { showComplainDialog = false }
            )
            TranslationStreamerDialog(
                type = TranslationDialogType.COMPLETE,
                show = showCompleteDialog,
                onSuccess = {
                    vm.onEvent(TranslationEvent.CompleteTranslation)
                },
                dismiss = { showCompleteDialog = false }
            )
            TranslationStreamerDialog(
                type = TranslationDialogType.COMPLETE_EARLIER,
                show = showCompleteEarlierDialog,
                onSuccess = {
                    vm.onEvent(TranslationEvent.CompleteTranslation)
                },
                dismiss = { showCompleteEarlierDialog = false }
            )
            TranslationStreamerDialog(
                type = TranslationDialogType.EXIT,
                show = showExitDialog,
                onSuccess = {
                    showExitDialog = false
                    nav.navigationBack()
                },
                dismiss = { showExitDialog = false }
            )
        }
    }
}