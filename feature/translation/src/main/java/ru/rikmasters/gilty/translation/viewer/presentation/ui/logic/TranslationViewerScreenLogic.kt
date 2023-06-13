package ru.rikmasters.gilty.translation.viewer.presentation.ui.logic

import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.TextureView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.blur
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.logic.BottomSheetStateManager
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus
import ru.rikmasters.gilty.translation.shared.model.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.ProfileAvatar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.MicroWave
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.LowConnectionMicroOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.LowConnectionMicroOffVideoOff
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.MicroInactiveViewerSnackbar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.NoConnection
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.Reconnecting
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.TranslationResumedSnackbar
import ru.rikmasters.gilty.translation.streamer.presentation.ui.components.WeakConnectionSnackbar
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerEvent
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerOneTimeEvents
import ru.rikmasters.gilty.translation.viewer.model.TranslationViewerStatus
import ru.rikmasters.gilty.translation.viewer.presentation.ui.components.VideoTextureViewRenderer
import ru.rikmasters.gilty.translation.viewer.presentation.ui.content.TranslationViewerScreenContent
import ru.rikmasters.gilty.translation.viewer.viewmodel.TranslationViewerViewModel
import ru.rikmasters.gilty.translations.webrtc.WebRtcClient
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcConfig
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationViewerScreenLogic(
    vm: TranslationViewerViewModel,
    translationId: String
) {
    val nav = get<NavState>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val scaffoldState = rememberBottomSheetScaffoldState()
    val density = LocalDensity.current
    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.roundToPx()
    }

    // Смена систем баров
    val systemUiController = rememberSystemUiController()
    val isInDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val newBackgroundColor = ThemeExtra.colors.preDarkColor


    val webRtcClient = remember { WebRtcClient(context) }
    val screenState by vm.translationViewerUiState.collectAsState()
    val remoteVideoTrackState by webRtcClient.remoteVideoSinkFlow.collectAsState(null)
    val messages = vm.messages.collectAsLazyPagingItems()
    val members = vm.members.collectAsLazyPagingItems()
    val query by vm.query.collectAsState()

    var showComplainDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var isLowConnection by remember { mutableStateOf(false) }
    var isMicDisabled by remember { mutableStateOf(false) }
    var isTimerHighlighted by remember { mutableStateOf(false) }
    var timerAdditionalTime by remember { mutableStateOf<String?>(null) }
    var translationResumed by remember { mutableStateOf(false) }
    var bottomSheetState by remember { mutableStateOf(TranslationBottomSheetState.CHAT) }
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    /**
     * Blur
     */
    var currentBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentBgOffset by remember { mutableStateOf<Float?>(null) }
    var currentView by remember { mutableStateOf<VideoTextureViewRenderer?>(null) }

    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                orientation = it
            }
    }

    DisposableEffect(Unit) {
        vm.onEvent(TranslationViewerEvent.Initialize(translationId))
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            vm.onEvent(TranslationViewerEvent.Dismiss)
            webRtcClient.disconnect()
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.translationViewerOneTimeEvents.collectLatest { event ->
                when (event) {
                    is TranslationViewerOneTimeEvents.OnError -> {

                    }
                    is TranslationViewerOneTimeEvents.TranslationExtended -> {
                        event.duration?.let {
                            timerAdditionalTime = it
                            delay(2000)
                            timerAdditionalTime = null
                        } ?: kotlin.run {
                            isTimerHighlighted = true
                            delay(2000)
                            isTimerHighlighted = false
                        }
                    }

                    is TranslationViewerOneTimeEvents.ConnectToStream -> {
                        webRtcClient.connecting(WebRtcConfig(event.wsUrl))
                    }

                    TranslationViewerOneTimeEvents.LowConnection -> {
                        scope.launch {
                            isLowConnection = true
                            delay(2000)
                            isLowConnection = false
                        }
                    }

                    TranslationViewerOneTimeEvents.DisconnectWebRtc -> {
                        webRtcClient.disconnect()
                    }

                    TranslationViewerOneTimeEvents.MicroOff -> {
                        scope.launch {
                            isMicDisabled = true
                            delay(2000)
                            isMicDisabled = false
                        }
                    }

                    TranslationViewerOneTimeEvents.TranslationResumed -> {
                        scope.launch {
                            translationResumed = true
                            delay(2000)
                            translationResumed = false
                        }
                    }
                }
            }
            webRtcClient.webRtcStatus.collectLatest { status ->
                vm.onEvent(
                    TranslationViewerEvent.HandleWebRtcStatus(
                        status = status
                    )
                )
            }
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

    /**
     * Blur
     */
    LaunchedEffect(scaffoldState.bottomSheetState) {
        while (true) {
            (currentView as? TextureView)?.bitmap?.let {
                val blurred = it.blur(context)
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
                onSendMessage = { vm.onEvent(TranslationViewerEvent.MessageSent(it)) },
                membersCount = screenState.membersCount,
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
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            TranslationViewerScreenContent(
                translationStatus = screenState.translationStatus
                    ?: TranslationViewerStatus.INACTIVE,
                videoTrack = remoteVideoTrackState,
                eglBaseContext = webRtcClient.eglBaseContext,
                membersCount = screenState.membersCount,
                meetingModel = screenState.meetingModel,
                remainTime = screenState.remainTime,
                onChatClicked = {
                    scope.launch {
                        when {
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == TranslationBottomSheetState.CHAT -> {
                                vm.onEvent(TranslationViewerEvent.DisconnectFromChat)
                                scaffoldState.bottomSheetState.collapse()
                            }

                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != TranslationBottomSheetState.CHAT -> {
                                vm.onEvent(TranslationViewerEvent.ConnectToChat)
                                scaffoldState.bottomSheetState.collapse()
                                bottomSheetState = TranslationBottomSheetState.CHAT
                                scaffoldState.bottomSheetState.expand()
                            }

                            !scaffoldState.bottomSheetState.isExpanded -> {
                                vm.onEvent(TranslationViewerEvent.ConnectToChat)
                                bottomSheetState = TranslationBottomSheetState.CHAT
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                },
                onMembersCountClicked = {
                    scope.launch {
                        when {
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == TranslationBottomSheetState.USERS -> {
                                scaffoldState.bottomSheetState.collapse()
                            }

                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != TranslationBottomSheetState.USERS -> {
                                vm.onEvent(TranslationViewerEvent.ReloadMembers)
                                scaffoldState.bottomSheetState.collapse()
                                bottomSheetState = TranslationBottomSheetState.USERS
                                scaffoldState.bottomSheetState.expand()
                            }

                            !scaffoldState.bottomSheetState.isExpanded -> {
                                vm.onEvent(TranslationViewerEvent.ReloadMembers)
                                bottomSheetState = TranslationBottomSheetState.USERS
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                },
                onCloseClicked = { showExitDialog = true },
                isTimerHighlighted = isTimerHighlighted,
                addTimerString = timerAdditionalTime ?: "",
                onToChatPressed = { nav.navigationBack() },
                isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT,
                initialize = {
                    currentView = it
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
            AnimatedVisibility(
                visible = isTimerHighlighted || !timerAdditionalTime.isNullOrEmpty() && screenState.connectionStatus == ConnectionStatus.SUCCESS && screenState.translationStatus == TranslationViewerStatus.STREAM,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = ThemeExtra.colors.thirdOpaqueGray,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            vertical = 6.dp,
                            horizontal = 16.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Microphone off",
                            tint = ThemeExtra.colors.white
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.translation_extended),
                            style = MaterialTheme.typography.bodyMedium,
                            color = ThemeExtra.colors.white
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = screenState.translationStatus == TranslationViewerStatus.PAUSED,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.translation_paused),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.translation_paused_text),
                        color = ThemeExtra.colors.white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            AnimatedVisibility(
                visible = isLowConnection &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.translationInfo?.camera == false &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
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
                visible = isLowConnection &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
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
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ProfileAvatar(
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
                visible = !isLowConnection &&
                        screenState.translationInfo?.camera == false &&
                        screenState.translationInfo?.microphone == false &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MicroWave(
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
                visible = screenState.connectionStatus == ConnectionStatus.RECONNECTING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Reconnecting(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && orientation == Configuration.ORIENTATION_LANDSCAPE  && screenState.translationStatus == TranslationViewerStatus.STREAM) {
                        Modifier
                            .fillMaxHeight()
                            .width((configuration.screenWidthDp * 0.6).dp)
                    } else {
                        Modifier.fillMaxSize()
                    }
                )
            }
            AnimatedVisibility(
                visible = screenState.connectionStatus == ConnectionStatus.NO_CONNECTION  && screenState.translationStatus == TranslationViewerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NoConnection(
                    onReconnectCLicked = {
                        screenState.translationInfo?.webrtc?.let {
                            webRtcClient.disconnect()
                            webRtcClient.retry = 0
                            webRtcClient.connecting(WebRtcConfig(it))
                        }
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
                visible = isMicDisabled &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MicroInactiveViewerSnackbar(
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
                visible = translationResumed &&
                        screenState.translationInfo?.microphone == true &&
                        screenState.translationInfo?.camera == true &&
                        !isLowConnection &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
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
                visible = isLowConnection &&
                        screenState.translationInfo?.microphone == true &&
                        screenState.translationInfo?.camera == true &&
                        screenState.connectionStatus == ConnectionStatus.SUCCESS  && screenState.translationStatus == TranslationViewerStatus.STREAM,
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
            /*
            GAlertDarkTheme(
                show = showComplainDialog,
                success = Pair(stringResource(id = R.string.translations_viewer_complain_posiitive_nex)) {

                },
                label = stringResource(id = R.string.translations_viewer_complain_text),
                title = stringResource(id = R.string.translations_members_complain),
                onDismissRequest = { showComplainDialog = false },
                cancel = Pair(stringResource(id = R.string.translations_members_cancel)) {
                    showComplainDialog = false
                }
            )
            GAlertDarkTheme(
                show = showExitDialog,
                success = Pair(stringResource(id = R.string.translations_viewer_complain_exit_positive)) {
                    vm.onEvent(TranslationViewerEvent.Dismiss)
                    webRtcClient.disconnect()
                    showExitDialog = false
                    nav.navigationBack()
                },
                label = stringResource(id = R.string.translations_viewer_complain_exit_text),
                title = stringResource(id = R.string.translations_viewer_complain_exit),
                onDismissRequest = { showExitDialog = false },
                cancel = Pair(stringResource(id = R.string.translations_complete_negative)) {
                    showExitDialog = false
                }
            )
             */
        }
    }
}

