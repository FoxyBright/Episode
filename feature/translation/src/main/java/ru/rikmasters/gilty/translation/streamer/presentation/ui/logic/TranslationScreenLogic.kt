package ru.rikmasters.gilty.translation.streamer.presentation.ui.logic

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pedro.encoder.input.gl.render.filters.`object`.ImageObjectFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.util.BitrateAdapter
import com.pedro.rtplibrary.util.SensorRotationManager
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.blur
import ru.rikmasters.gilty.shared.common.extentions.getBitmap
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.shared.GAlertDarkTheme
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.logic.BottomSheetStateManager
import ru.rikmasters.gilty.translation.streamer.event.TranslationEvent
import ru.rikmasters.gilty.translation.streamer.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus
import ru.rikmasters.gilty.translation.shared.model.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.streamer.model.Facing
import ru.rikmasters.gilty.translation.streamer.model.TranslationStreamerStatus
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.CameraOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.CameraOffMicOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.LowConnectionMicroOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.LowConnectionMicroOffVideoOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.MicroInactiveSnackbar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.NoConnection
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.Reconnecting
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.TranslationDialogType
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.TranslationResumedSnackbar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.TranslationStreamerDialog
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.WeakConnectionSnackbar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.content.TranslationStreamerContent
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTranslationScreen(
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
    val screenState by vm.translationStreamerUiState.collectAsState()
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

    /**
     * Snackbar
     */
    var showMicroInactiveSnackbar by remember { mutableStateOf(false) }
    var showTranslationResumedSnackbar by remember { mutableStateOf(false) }
    var showWeakConnectionSnackbar by remember { mutableStateOf(false) }

    /**
     * Timer
     */
    var isTimerHighlighted by remember { mutableStateOf(false) }
    var timerAdditionalTime by remember { mutableStateOf<String?>(null) }

    /**
     * BottomSheetState
     */
    var bottomSheetState by remember { mutableStateOf(TranslationBottomSheetState.CHAT) }

    /**
     * Camera
     */
    var bitrateAdapter by remember { mutableStateOf<BitrateAdapter?>(null) }
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }
    var currentOpenGlView by remember { mutableStateOf<OpenGlView?>(null) }
    var glViewChanged by remember { mutableStateOf(false) }
    var reTryRemained by remember { mutableStateOf(7) }

    /**
     * Blur
     */
    var currentBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentBgOffset by remember { mutableStateOf<Float?>(null) }

    /**
     * Orientation
     */
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    var sensorOrientation by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                if (camera?.isStreaming == true || camera?.isOnPreview == true) {
                    camera?.glInterface?.setRotation(sensorOrientation)
                }
                orientation = it
            }
    }

    /**
     * Enter/leave screen
     */
    DisposableEffect(Unit) {
        vm.onEvent(TranslationEvent.Initialize(translationId))
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            vm.onEvent(TranslationEvent.Dismiss)
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    /**
     * Camera
     */
    fun switchCamera(camera: RtmpCamera2) {
        if (screenState.facing == Facing.FRONT && camera.cameraFacing != CameraHelper.Facing.FRONT
            || screenState.facing == Facing.BACK && camera.cameraFacing != CameraHelper.Facing.BACK
        ) {
            camera.switchCamera()
        }
    }
    camera?.let {
        switchCamera(it)
    }
    fun startPreview() {
        camera?.let {
            if (!it.isOnPreview) {
                val resolutionFront = it.resolutionsFront
                val resolutionBack = it.resolutionsBack
                val isUHd = (resolutionBack.contains(
                    Size(
                        1920,
                        1080
                    )
                ) && resolutionFront.contains(Size(1920, 1080)))
                val isHd = (resolutionBack.contains(Size(1280, 720)) && resolutionFront.contains(
                    Size(
                        1280,
                        720
                    )
                ))
                val width = if (isUHd) 1920 else if (isHd) 1280 else 640
                val height = if (isUHd) 1080 else if (isHd) 720 else 480
                it.startPreview(
                    CameraHelper.Facing.FRONT,
                    width,
                    height
                )
            }
        }
    }

    fun stopPreview() {
        camera?.let {
            if (it.isOnPreview) {
                it.stopPreview()
            }
        }
    }

    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                val resolutionFront = it.resolutionsFront
                val resolutionBack = it.resolutionsBack
                val isUHd = (resolutionBack.contains(
                    Size(
                        1920,
                        1080
                    )
                ) && resolutionFront.contains(Size(1920, 1080)))
                val isHd = (resolutionBack.contains(Size(1280, 720)) && resolutionFront.contains(
                    Size(
                        1280,
                        720
                    )
                ))
                val width = if (isUHd) 1920 else if (isHd) 1280 else 640
                val height = if (isUHd) 1080 else if (isHd) 720 else 480
                val bitrate = if (isUHd) 4_500_000 else if (isHd) 3_000_000 else 1_500_000
                if (it.prepareAudio() && it.prepareVideo(width, height, bitrate)) {
                    it.startStream(rtmpUrl)
                    vm.onEvent(TranslationEvent.StartStreaming)
                }
            }
        }
    }

    fun stopBroadcast() {
        camera?.let {
            if (it.isStreaming) {
                it.stopStream()
                it.stopPreview()
                vm.onEvent(TranslationEvent.StopStreaming)
            }
        }
    }

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

    val sensorRotationManager = remember {
        object : SensorRotationManager(
            context,
            object : RotationChangedListener {
                override fun onRotationChanged(rotation: Int) {
                    sensorOrientation = rotation
                }
            }) {}
    }

    /**
     * Snackbars
     */
    fun showMicroOffSnackbar() {
        scope.launch {
            showMicroInactiveSnackbar = true
            delay(2000)
            showMicroInactiveSnackbar = false
        }
    }

    fun showWeakConnectionSnackbar() {
        scope.launch {
            showWeakConnectionSnackbar = true
            delay(2000)
            showWeakConnectionSnackbar = false
        }
    }

    suspend fun showTranslationResumedSnackbar() {
        scope.launch {
            showTranslationResumedSnackbar = true
            delay(2000)
            showTranslationResumedSnackbar = false
        }
    }

    /**
     * Timer
     */
    suspend fun handleExtendTranslation(duration: String?) {
        duration?.let {
            timerAdditionalTime = it
            delay(2000)
            timerAdditionalTime = null
        } ?: run {
            showTranslationResumedSnackbar()
            isTimerHighlighted = true
            delay(2000)
            isTimerHighlighted = false
        }
    }

    /**
     * Connection
     */
    fun reconnect() {
        camera?.reTry(1000, "")
        reTryRemained -= 1
    }

    fun reconnectAfterOver() {
        camera?.setReTries(8)
        reTryRemained = 7
        camera?.reTry(1000, "")
    }

    /**
     * Status
     */
    fun processExpiredState() {
        if (screenState.translationInfo?.camera != false) {
            toggleCamera(false)
        }
        if (screenState.translationInfo?.microphone != false) {
            toggleMicrophone(false)
        }
    }

    fun processFromExpiredToPreview() {
        if (screenState.translationInfo?.camera != true) {
            toggleCamera(true)
        }
        if (screenState.translationInfo?.microphone != true) {
            toggleMicrophone(true)
        }
    }

    fun completeTranslation() {
        Log.d("TEST","Complete translation")
        if (screenState.translationInfo?.camera != false) {
            toggleCamera(false)
        }
        if (screenState.translationInfo?.microphone != false) {
            toggleMicrophone(false)
        }

    }

    /**
     * Collect one-time events
     */
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.oneTimeEvent.collectLatest { event ->
                when (event) {
                    is TranslationOneTimeEvent.OnError -> {}
                    TranslationOneTimeEvent.Reconnect -> {
                        reconnect()
                    }

                    TranslationOneTimeEvent.ShowMicroDisabledSnackbar -> {
                        showMicroOffSnackbar()
                    }

                    is TranslationOneTimeEvent.TranslationExtended -> {
                        handleExtendTranslation(event.duration)
                    }

                    is TranslationOneTimeEvent.ToggleCamera -> {
                        toggleCamera(event.value)
                    }

                    is TranslationOneTimeEvent.ToggleMicrophone -> {
                        toggleMicrophone(event.value)
                    }

                    TranslationOneTimeEvent.ShowWeakConnectionSnackbar -> {
                        showWeakConnectionSnackbar()
                    }

                    TranslationOneTimeEvent.ReconnectAfterOver -> {
                        reconnectAfterOver()
                    }

                    TranslationOneTimeEvent.TranslationExpired -> {
                        processExpiredState()
                    }

                    TranslationOneTimeEvent.FromExpiredToPreview -> {
                        processFromExpiredToPreview()
                    }

                    TranslationOneTimeEvent.CompleteTranslation -> {
                        completeTranslation()
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
                camera?.let {
                    if (reTryRemained > 0) {
                        vm.onEvent(TranslationEvent.Reconnect)
                    } else {
                        vm.onEvent(TranslationEvent.ReconnectAttemptsOver)
                    }
                }
            }

            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {
                camera?.setReTries(7)
                reTryRemained = 7
                camera?.let {
                    bitrateAdapter = BitrateAdapter { bitrate ->
                        it.setVideoBitrateOnFly(bitrate)
                    }
                    bitrateAdapter?.setMaxBitrate(it.bitrate)
                }
                vm.onEvent(TranslationEvent.ConnectionSucceed)
            }

            override fun onDisconnectRtmp() {}
            override fun onNewBitrateRtmp(bitrate: Long) {
                if (screenState.translationStatus == TranslationStreamerStatus.STREAM) {
                    if (screenState.translationInfo?.camera == true) {
                        if (bitrate < 300_000) {
                            vm.onEvent(TranslationEvent.LowBitrate)
                        }
                    } else {
                        if (bitrate < 30_000) {
                            vm.onEvent(TranslationEvent.LowBitrate)
                        }
                    }
                }
                bitrateAdapter?.adaptBitrate(bitrate)
            }
        }
    }
    val surfaceHolderCallback = remember {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (glViewChanged && currentOpenGlView != null) {
                    camera?.replaceView(currentOpenGlView)
                    glViewChanged = false
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                sensorRotationManager.start()
                camera?.let {
                    when (screenState.translationStatus) {
                        TranslationStreamerStatus.PREVIEW -> {
                            startPreview()
                        }

                        TranslationStreamerStatus.STREAM -> {
                            screenState.translationInfo?.let { translation ->
                                startBroadCast(
                                    rtmpUrl = translation.rtmp ?: ""
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                sensorRotationManager.stop()
                camera?.let {
                    if (it.isStreaming) {
                        stopBroadcast()
                    } else {
                        stopPreview()
                    }
                }
            }
        }
    }

    /**
     * Back pressed handler
     */
    BackHandler {
        when (screenState.translationStatus) {
            TranslationStreamerStatus.STREAM -> {
                showCompleteDialog = true
            }

            TranslationStreamerStatus.EXPIRED -> {
                showCompleteEarlierDialog = true
            }

            TranslationStreamerStatus.COMPLETED -> {
                camera?.stopStream()
                camera?.stopPreview()
                nav.navigationBack()
            }

            TranslationStreamerStatus.PREVIEW -> {
                if (screenState.onPreviewFromExpired) {
                    showCompleteDialog = true
                } else {
                    camera?.stopStream()
                }
            }

            else -> {}
        }
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
                membersCount = screenState.membersCount,
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
                translationStatus = screenState.translationStatus
                    ?: TranslationStreamerStatus.PREVIEW,
                initCamera = { view ->
                    currentOpenGlView = view
                    if (camera == null) {
                        camera = RtmpCamera2(view, connectionChecker)
                    } else {
                        glViewChanged = true
                    }
                },
                surfaceHolderCallback = surfaceHolderCallback,
                isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                meetingModel = screenState.meetingModel,
                remainTime = screenState.remainTime,
                isHighlightTimer = isTimerHighlighted,
                addTimerString = timerAdditionalTime ?: "",
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
                membersCount = screenState.membersCount,
                onTimerClicked = {
                    scope.launch {
                        bottomSheetState = TranslationBottomSheetState.DURATION
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                onCloseClicked = {
                    if (screenState.onPreviewFromExpired || screenState.translationStatus == TranslationStreamerStatus.EXPIRED) {
                        showCompleteDialog = true
                    } else {
                        showCompleteEarlierDialog = true
                    }
                },
                bsOpened = scaffoldState.bottomSheetState.isExpanded,
                cameraEnabled = screenState.translationInfo?.camera ?: true,
                microphoneEnabled = screenState.translationInfo?.microphone ?: true,
                onCameraClicked = { vm.onEvent(TranslationEvent.ToggleCamera) },
                onMicrophoneClicked = { vm.onEvent(TranslationEvent.ToggleMicrophone) },
                changeFacing = { vm.onEvent(TranslationEvent.ChangeFacing) },
                onToChatPressed = {
                    camera?.stopStream()
                    camera?.stopPreview()
                    nav.navigationBack()
                },
                startBroadCast = {
                    vm.onEvent(TranslationEvent.ChangeUiToStream)
                },
                selectedFacing = screenState.facing,
                closeFromPreview = {
                    if (screenState.onPreviewFromExpired || screenState.translationStatus == TranslationStreamerStatus.EXPIRED) {
                        showCompleteDialog = true
                    } else {
                        showCompleteEarlierDialog = true
                    }
                }
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

            /**
             * Snackbar
             */
            AnimatedVisibility(
                visible = showWeakConnectionSnackbar &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.translationInfo?.camera == false &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LowConnectionMicroOffVideoOff(
                    meetingModel = screenState.meetingModel,
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = showWeakConnectionSnackbar &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LowConnectionMicroOff(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = screenState.translationInfo?.camera == false &&
                        screenState.translationInfo?.microphone == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CameraOff(
                    meetingModel = screenState.meetingModel,
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = !showWeakConnectionSnackbar &&
                        screenState.translationInfo?.camera == false &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CameraOffMicOff(
                    meetingModel = screenState.meetingModel,
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = screenState.connectionStatus == ConnectionStatus.RECONNECTING && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
            AnimatedVisibility(
                visible = screenState.connectionStatus == ConnectionStatus.NO_CONNECTION && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NoConnection(
                    onReconnectCLicked = {
                        vm.onEvent(TranslationEvent.ReconnectAfterAttemptsOver)
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
            AnimatedVisibility(
                visible = showMicroInactiveSnackbar &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MicroInactiveSnackbar(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = showTranslationResumedSnackbar &&
                        screenState.translationInfo?.microphone == true &&
                        screenState.translationInfo?.camera == true &&
                        !showWeakConnectionSnackbar &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TranslationResumedSnackbar(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = showWeakConnectionSnackbar &&
                        screenState.translationInfo?.microphone == true &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS && screenState.translationStatus == TranslationStreamerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                WeakConnectionSnackbar(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
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
                    Log.d("TEST","COMPLETE EARLIER")
                    vm.onEvent(TranslationEvent.CompleteTranslation)
                },
                dismiss = { showCompleteEarlierDialog = false }
            )
        }
    }
}