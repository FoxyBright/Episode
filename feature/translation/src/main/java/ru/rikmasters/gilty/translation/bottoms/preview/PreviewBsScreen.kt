package ru.rikmasters.gilty.translation.bottoms.preview

import android.view.SurfaceHolder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import ru.rikmasters.gilty.translation.shared.utils.map
import ru.rikmasters.gilty.translation.shared.utils.restartPreview
import ru.rikmasters.gilty.translation.shared.utils.stopPreview

@Composable
fun PreviewBsScreen(
    vm: PreviewBsViewModel,
    closeClicked: () -> Unit,
    startBroadcastClicked: () -> Unit
) {
    val context = LocalContext.current
    val facing by vm.selectedFacing.collectAsState()
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }

    val connectionChecker = remember {
        object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {}
            override fun onAuthSuccessRtmp() {}
            override fun onConnectionFailedRtmp(reason: String) {}
            override fun onConnectionStartedRtmp(rtmpUrl: String) {}
            override fun onConnectionSuccessRtmp() {}
            override fun onDisconnectRtmp() {}
            override fun onNewBitrateRtmp(bitrate: Long) {}
        }
    }
    val surfaceHolderCallback = remember {
        object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {}
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) { restartPreview(camera, facing, context) }
            override fun surfaceDestroyed(holder: SurfaceHolder) { stopPreview(camera) }
        }
    }

    LaunchedEffect(facing) {
        if (camera?.cameraFacing != facing.map()) {
            camera?.switchCamera()
        }
    }

    PreviewBsContent(
        initCamera = { camera = RtmpCamera2(it, connectionChecker) },
        surfaceHolderCallback = surfaceHolderCallback,
        onCloseClicked = closeClicked,
        changeFacing = { vm.onEvent(PreviewEvent.ToggleFacing(it)) },
        selectedStreamerFacing = facing,
        startBroadCast = startBroadcastClicked
    )
}