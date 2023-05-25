package ru.rikmasters.gilty.translation.presentation.ui.logic

import android.content.res.Configuration
import android.view.SurfaceHolder
import androidx.compose.runtime.Composable
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.translation.model.ConnectionStatus
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.presentation.ui.content.active.TranslationActive
import ru.rikmasters.gilty.translation.presentation.ui.content.completed.TranslationCompleted
import ru.rikmasters.gilty.translation.presentation.ui.content.expired.TranslationExpired
import ru.rikmasters.gilty.translation.presentation.ui.content.preview.TranslationPreview

@Composable
fun TranslationStateManager(
    translationStatus: TranslationStatus,
    initializeCamera: (OpenGlView) -> Unit,
    onCloseClicked: () -> Unit,
    changeFacing: () -> Unit,
    selectedFacing: Facing,
    startBroadcast: () -> Unit,
    meetingModel: FullMeetingModel?,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    remainTime: String,
    membersCount: Int,
    onChatClicked: () -> Unit,
    onMembersClicked: () -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    orientation: Int,
    connectionStatus: ConnectionStatus,
    onReconnectClicked: () -> Unit,
    bsOpened: Boolean,
    configuration: Configuration,
    thumbnailUrl: String?,
    onTurnOnClicked: () -> Unit,
    onTimerClicked: () -> Unit,
    isHighlightTimer: Boolean,
    addTimerValue: String,
    onToChatPressed: () -> Unit
) {
    when(translationStatus) {
        TranslationStatus.PREVIEW -> {
            TranslationPreview(
                initializeCamera = initializeCamera,
                onCloseClicked = onCloseClicked,
                changeFacing = changeFacing,
                selectedFacing = selectedFacing,
                startBroadCast = startBroadcast,
                surfaceHolderCallback = surfaceHolderCallback
            )
        }
        TranslationStatus.STREAM -> {
            TranslationActive(
                initializeCamera = initializeCamera,
                meetingModel = meetingModel,
                remainTime = remainTime,
                onCloseClicked = onCloseClicked,
                onChatClicked = onChatClicked,
                membersCount = membersCount,
                onMembersClicked = onMembersClicked,
                cameraEnabled = cameraEnabled,
                microphoneEnabled = microphoneEnabled,
                onCameraClicked = onCameraClicked,
                onMicrophoneClicked = onMicrophoneClicked,
                changeFacing = changeFacing,
                surfaceHolderCallback = surfaceHolderCallback,
                orientation = orientation,
                connectionStatus = connectionStatus,
                onReconnectClicked = onReconnectClicked,
                bsOpened = bsOpened,
                configuration = configuration,
                onTimerClicked = onTimerClicked,
                isHighlightTimer = isHighlightTimer,
                addTimerValue = addTimerValue
            )
        }
        TranslationStatus.EXPIRED -> {
            TranslationExpired(
                thumbnailUrl = thumbnailUrl,
                meetingModel = meetingModel,
                remainTime = remainTime,
                membersCount = membersCount,
                onCloseClicked = onCloseClicked,
                onTurnOnClicked = onTurnOnClicked
            )
        }
        TranslationStatus.COMPLETED -> {
            TranslationCompleted(
                thumbnailUrl = thumbnailUrl,
                onToChatPressed = onToChatPressed
            )
        }
    }
}