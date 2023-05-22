package ru.rikmasters.gilty.translation.presentation.ui.logic

import android.content.res.Configuration
import android.view.SurfaceHolder
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.util.BitrateAdapter
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetState
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.viewmodel.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel,
    translationId: String
) {
    val scope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()
    val isInDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val newBackgroundColor = ThemeExtra.colors.preDarkColor

    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    var bitrateAdapter by remember { mutableStateOf<BitrateAdapter?>(null) }

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(color = newBackgroundColor, darkIcons = false)
        onDispose {
            if (isInDarkTheme) {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = false)
            } else {
                systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        // Начальная инициализация при заходе в экран
        vm.onEvent(
            TranslationEvent.EnterScreen(
                meetingId = translationId
            )
        )
    }

    // Коллект одноразовых ивентов
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.oneTimeEvent.collectLatest { event ->
                when (event) {
                    is TranslationOneTimeEvent.ErrorHappened -> TODO()
                }
            }
        }
    }

    // Состояние экрана
    val translationScreenState by vm.translationUiState.collectAsState()
    // Список подключенных пользователей с пагинацией
    val connectedMembers = vm.connectedUsers.collectAsLazyPagingItems()
    // Список сообщений в чате с пагинацикй
    val messages = vm.messages.collectAsLazyPagingItems()
    // Состояние поиска ботом шита участников
    val query by vm.usersQuery.collectAsState()
    // Оставшееся время трансляции
    val remainTime by vm.remainTime.collectAsState()


    // Камера РТМП клиента
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }
    // Смена ориентации камеры
    camera?.let {
        if (translationScreenState.selectedCamera == Facing.FRONT && it.cameraFacing != CameraHelper.Facing.FRONT || translationScreenState.selectedCamera == Facing.BACK && it.cameraFacing != CameraHelper.Facing.BACK) {
            it.switchCamera()
        }
    }
    // Логика включения выключения аудио при стриминге
    LaunchedEffect(translationScreenState.translationInfo?.microphone) {
        camera?.let { camera ->
            translationScreenState.translationInfo?.microphone?.let { enabled ->
                if (enabled) {
                    camera.enableAudio()
                } else {
                    camera.disableAudio()
                }
            }
        }
    }
    // Логика включения выключения видео и блюра
    // TODO: использовать thumbnail фото при выключении камеры
    LaunchedEffect(translationScreenState.translationInfo?.camera) {
        camera?.let { camera ->
            translationScreenState.translationInfo?.camera?.let { enabled ->
                if (enabled) {
                    camera.glInterface.unMuteVideo()
                    camera.glInterface.clearFilters()
                    camera.resumeRecord()
                } else {
                    camera.glInterface.muteVideo()
                    camera.glInterface.addFilter(BlurFilterRender())
                    camera.pauseRecord()
                }
            }
        }
    }
    var currentOpenGlView by remember { mutableStateOf<OpenGlView?>(null) }
    var glViewChanged by remember { mutableStateOf(false) }


    // Состояние боттом шита
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )

    // Конфигурация экрана (для определения максимальной/минимальной высоты ботом шита)
    val configuration = LocalConfiguration.current

    LaunchedEffect(key1 = configuration) {
        snapshotFlow { configuration.orientation }
            .collect {
                orientation = it
            }
    }

    // Остановка стрима и превью - release GL, оповещение вью модели, конект
    fun stopBroadcast() {
        camera?.let {
            if (it.isStreaming) {
                it.stopStream()
                it.stopPreview()
                vm.onEvent(TranslationEvent.StopStreaming)
            }
        }
    }

    // Остановка превью
    fun stopPreview() {
        camera?.let {
            if (it.isOnPreview) {
                it.stopPreview()
            }
        }
    }


    // Запуск стрима, оповещение вью модели
    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                if (it.prepareAudio() && it.prepareVideo()) {
                    it.startStream(rtmpUrl)
                    vm.onEvent(TranslationEvent.StartStreaming)
                } else {
                    // TODO: обработать случаи когда не удалось подготовить видео или аудио
                    //vm.onEvent(TranslationEvent.StopStreaming)
                }
            }
        }
    }

    // Запуск превью
    fun startPreview() {
        camera?.let {
            if (!it.isOnPreview) {
                it.startPreview()
            }
        }
    }


    val connectionChecker = remember {
        object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
            override fun onConnectionFailedRtmp(reason: String) {}
            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {
                camera?.let {
                    bitrateAdapter = BitrateAdapter { bitrate -> it.setVideoBitrateOnFly(bitrate) }
                    bitrateAdapter?.setMaxBitrate(it.bitrate)
                }
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
                // Change View of Surface
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
                camera?.let {
                    when (translationScreenState.translationStatus) {
                        TranslationStatus.PREVIEW -> {
                            it.startPreview()
                        }
                        TranslationStatus.STREAM -> {
                            translationScreenState.translationInfo?.let { translation ->
                                startBroadCast(
                                    rtmpUrl = translation.rtmp
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
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

    var isShowDeleteAlert by remember { mutableStateOf(false) }
    var currentDeleteUser by remember { mutableStateOf<FullUserModel?>(null) }

    var bottomSheetState by remember {
        mutableStateOf(
            ru.rikmasters.gilty.translation.model.BottomSheetState.CHAT
        )
    }

    BottomSheetScaffold(
        sheetContent = {
            BottomSheetStateManager(
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
                membersCount = translationScreenState.membersCount ?: 0,
                searchValue = query,
                onSearchValueChange = { newQuery ->
                    vm.onEvent(
                        TranslationEvent.UserBottomSheetQueryChanged(
                            newQuery = newQuery
                        )
                    )
                },
                membersList = connectedMembers,
                onComplainClicked = {
                    //TODO: На экран жалоб
                },
                onDeleteClicked = {
                    currentDeleteUser = it
                    isShowDeleteAlert = true
                }
            )
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
        sheetBackgroundColor = ThemeExtra.colors.blackSeventy,
        sheetShape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        )
    ) {
        Box {
            TranslationStateManager(
                translationStatus = translationScreenState.translationStatus
                    ?: TranslationStatus.PREVIEW,
                initializeCamera = { view ->
                    if (camera == null) {
                        // Первый заход в экран
                        camera = RtmpCamera2(view, connectionChecker)
                    } else {
                        // Экран обновился
                        currentOpenGlView = view
                        glViewChanged = true
                    }
                },
                onCloseClicked = {
                    vm.onEvent(TranslationEvent.ChangeUiToPreview)
                },
                changeFacing = {
                    vm.onEvent(TranslationEvent.ChangeFacing)
                },
                selectedFacing = translationScreenState.selectedCamera ?: Facing.FRONT,
                startBroadcast = {
                    vm.onEvent(TranslationEvent.ChangeUiToStream)
                },
                meetingModel = translationScreenState.meetingModel,
                cameraEnabled = translationScreenState.translationInfo?.camera ?: true,
                microphoneEnabled = translationScreenState.translationInfo?.microphone ?: true,
                onCameraClicked = {
                    vm.onEvent(TranslationEvent.ChangeVideoState)
                },
                onMicrophoneClicked = {
                    vm.onEvent(TranslationEvent.ChangeMicrophoneState)
                },
                remainTime = remainTime,
                membersCount = translationScreenState.membersCount ?: 0,
                onChatClicked = {
                    scope.launch {
                        when {
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == ru.rikmasters.gilty.translation.model.BottomSheetState.CHAT -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                            }
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != ru.rikmasters.gilty.translation.model.BottomSheetState.CHAT -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = false
                                    )
                                )

                                bottomSheetState =
                                    ru.rikmasters.gilty.translation.model.BottomSheetState.CHAT

                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.ChatBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }
                            !scaffoldState.bottomSheetState.isExpanded -> {
                                bottomSheetState =
                                    ru.rikmasters.gilty.translation.model.BottomSheetState.CHAT
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
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState == ru.rikmasters.gilty.translation.model.BottomSheetState.USERS -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = false
                                    )
                                )
                            }
                            scaffoldState.bottomSheetState.isExpanded && bottomSheetState != ru.rikmasters.gilty.translation.model.BottomSheetState.USERS -> {
                                scaffoldState.bottomSheetState.collapse()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = false
                                    )
                                )

                                bottomSheetState =
                                    ru.rikmasters.gilty.translation.model.BottomSheetState.USERS

                                scaffoldState.bottomSheetState.expand()
                                vm.onEvent(
                                    TranslationEvent.UserBottomSheetOpened(
                                        isOpened = true
                                    )
                                )
                            }
                            !scaffoldState.bottomSheetState.isExpanded -> {
                                bottomSheetState =
                                    ru.rikmasters.gilty.translation.model.BottomSheetState.USERS
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
                surfaceHolderCallback = surfaceHolderCallback,
                orientation = orientation
            )
            GAlert(
                show = isShowDeleteAlert,
                success = Pair(stringResource(id = R.string.translations_members_delete)) {
                    currentDeleteUser?.let {
                        vm.onEvent(
                            TranslationEvent.KickUser(
                                user = it
                            )
                        )
                    }
                },
                label = stringResource(id = R.string.translations_members_delete_label),
                title = stringResource(id = R.string.translations_members_delete_title),
                onDismissRequest = { isShowDeleteAlert = false },
                cancel = Pair(stringResource(id = R.string.translations_members_cancel)) {
                    isShowDeleteAlert = false
                }
            )
        }
    }
}

private enum class TestStreamState {
    PREVIEW, STREAM
}