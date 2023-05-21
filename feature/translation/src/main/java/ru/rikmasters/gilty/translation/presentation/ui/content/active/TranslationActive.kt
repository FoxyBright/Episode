package ru.rikmasters.gilty.translation.presentation.ui.content.active

import android.content.res.Configuration
import android.view.SurfaceHolder
import androidx.compose.runtime.Composable
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

@Composable
fun TranslationActive(
    initializeCamera: (OpenGlView) -> Unit,
    meetingModel: FullMeetingModel?,
    remainTime: String,
    onCloseClicked: () -> Unit,
    onChatClicked: () -> Unit,
    membersCount: Int,
    onMembersClicked: () -> Unit,
    cameraEnabled: Boolean,
    microphoneEnabled: Boolean,
    onCameraClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    changeFacing: () -> Unit,
    surfaceHolderCallback: SurfaceHolder.Callback,
    orientation: Int
) {
    when(orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            TranslationActiveLandscape(
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
                surfaceHolderCallback = surfaceHolderCallback
            )
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            TranslationActivePortrait(
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
                surfaceHolderCallback = surfaceHolderCallback
            )
        }
    }
}