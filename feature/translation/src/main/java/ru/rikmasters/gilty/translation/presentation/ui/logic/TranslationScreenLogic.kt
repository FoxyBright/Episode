package ru.rikmasters.gilty.translation.presentation.ui.logic

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetScaffold
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.presentation.ui.content.TranslationScreen
import ru.rikmasters.gilty.translation.presentation.ui.content.UsersBottomSheetContent
import ru.rikmasters.gilty.translation.viewmodel.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel,
    translationId: String
) {
    val scope = rememberCoroutineScope()

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

    // TODO: Пока непонятно для чего нужно
    var streamState by remember { mutableStateOf(StreamState.STOP) }

    // Состояние боттом шита
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Конфигурация экрана (для определения максимальной/минимальной высоты ботом шита)
    val configuration = LocalConfiguration.current

    // Остановка стрима
    fun stopBroadcast() {
        camera?.stopStream()
        vm.onEvent(TranslationEvent.StopStreaming)
    }

    // Запуск стрима
    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                if (it.prepareAudio() && it.prepareVideo()) {
                    streamState = StreamState.PLAY
                    it.startStream(rtmpUrl)
                    vm.onEvent(TranslationEvent.StartStreaming)
                } else {
                    streamState = StreamState.STOP
                    vm.onEvent(TranslationEvent.StopStreaming)
                }
            } else {
                it.stopStream()
                vm.onEvent(TranslationEvent.StopStreaming)
            }
        }
    }

    // TODO: Непонятно для чего нужен
    val connectionChecker = remember {
        object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
            override fun onConnectionFailedRtmp(reason: String) {}
            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {
                streamState = StreamState.PLAY
            }

            override fun onDisconnectRtmp() {
                streamState = StreamState.STOP
            }

            override fun onNewBitrateRtmp(bitrate: Long) {}
        }
    }

    var isShowDeleteAlert by remember { mutableStateOf(false) }
    var currentDeleteUser by remember { mutableStateOf<FullUserModel?>(null) }

    BottomSheetScaffold(
        sheetContent = {
            UsersBottomSheetContent(
                configuration = configuration,
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
        }, scaffoldState = scaffoldState
    ) {
        Box {
            TranslationScreen(
                translationStatus = translationScreenState.translationStatus
                    ?: TranslationStatus.PREVIEW,
                onCloseClicked = {
                    stopBroadcast()
                },
                translationUiState = translationScreenState,
                changeFacing = {
                    vm.onEvent(TranslationEvent.ChangeFacing)
                },
                onCameraClicked = {
                    vm.onEvent(TranslationEvent.ChangeVideoState)
                },
                onMicrophoneClicked = {
                    vm.onEvent(TranslationEvent.ChangeMicrophoneState)
                },
                initCamera = { view ->
                    camera = RtmpCamera2(view, connectionChecker)
                },
                startStreamPreview = {
                    camera?.startPreview()
                },
                stopStreamPreview = {
                    camera?.stopPreview()
                },
                startStream = {
                    translationScreenState.translationInfo?.let {
                        startBroadCast(
                            rtmpUrl = it.rtmp
                        )
                    }
                },
                remainTime = remainTime,
                userCount = translationScreenState.membersCount ?: 0,
                onChatClicked = {},
                onUsersClicked = {
                    scope.launch {
                        if (scaffoldState.drawerState.isOpen) {
                            scaffoldState.drawerState.close()
                            vm.onEvent(
                                TranslationEvent.UserBottomSheetOpened(
                                    isOpened = false
                                )
                            )
                        } else {
                            scaffoldState.drawerState.open()
                            vm.onEvent(
                                TranslationEvent.UserBottomSheetOpened(
                                    isOpened = true
                                )
                            )
                        }
                    }
                }
            )
        }
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
            cancel =  Pair(stringResource(id = R.string.translations_members_cancel)) {
                isShowDeleteAlert = false
            }
        )
    }
}

private enum class StreamState {
    STOP, PLAY
}