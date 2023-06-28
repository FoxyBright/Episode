package ru.rikmasters.gilty.translations.webrtc.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaRecorder.AudioSource
import android.os.Build
import org.webrtc.AddIceObserver
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.IceCandidateErrorEvent
import org.webrtc.MediaConstraints
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.audio.JavaAudioDeviceModule
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun PeerConnection.addRtcIceCandidate(iceCandidate: IceCandidate): Result<Unit> {
    return suspendCoroutine { cont ->
        addIceCandidate(
            iceCandidate,
            object : AddIceObserver {
                override fun onAddSuccess() {
                    cont.resume(Result.success(Unit))
                }

                override fun onAddFailure(error: String?) {
                    cont.resume(Result.failure(RuntimeException(error)))
                }
            }
        )
    }
}

suspend fun PeerConnection.setRemoteDescription(sessionDescription: SessionDescription): Result<Unit> {
   webRtcLog("[SET_REMOTE_DESCRIPTION] $sessionDescription")
    return setValue {
        setRemoteDescription(
            it,
            SessionDescription(
                sessionDescription.type,
                sessionDescription.description.mungeCodecs()
            )
        )
    }
}


fun createPeerConnectionFactory(context: Context, eglBaseContext: EglBase.Context): Pair<PeerConnectionFactory, EglBase.Context> {
    val videoDecoderFactory by lazy {
        DefaultVideoDecoderFactory(
            eglBaseContext
        )
    }
    val videoEncoderFactory by lazy {
        DefaultVideoEncoderFactory(eglBaseContext, true, true)
    }
    PeerConnectionFactory.initialize(
        PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
    )
    val audioDeviceModule = JavaAudioDeviceModule.builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        .createAudioDeviceModule()
    return Pair(PeerConnectionFactory.builder()
        .setVideoDecoderFactory(videoDecoderFactory)
        .setVideoEncoderFactory(videoEncoderFactory)
        .setAudioDeviceModule(audioDeviceModule)
        .createPeerConnectionFactory(), eglBaseContext)
}

suspend fun PeerConnection.setLocalDescription(sessionDescription: SessionDescription): Result<SessionDescription> {
    val sdp = SessionDescription(
        sessionDescription.type,
        sessionDescription.description.mungeCodecs()
    )
    val setResult = setValue {
        setLocalDescription(
            it,
            sdp
        )
    }
    return if (setResult.isSuccess) {
        Result.success(sdp)
    } else {
        Result.failure(RuntimeException())
    }
}


suspend fun PeerConnection.createAnswer(mediaConstraints: MediaConstraints): Result<SessionDescription> {
    return createValue {
        createAnswer(
            it,
            mediaConstraints
        )
    }
}

suspend inline fun createValue(
    crossinline call: (SdpObserver) -> Unit
): Result<SessionDescription> = suspendCoroutine {
    val observer = object : SdpObserver {

        /**
         * Handling of create values.
         */
        /**
         * Handling of create values.
         */
        override fun onCreateSuccess(description: SessionDescription?) {
            if (description != null) {
                it.resume(Result.success(description))
            } else {
                it.resume(Result.failure(RuntimeException("SessionDescription is null!")))
            }
        }

        override fun onCreateFailure(message: String?) =
            it.resume(Result.failure(RuntimeException(message)))

        /**
         * We ignore set results.
         */
        /**
         * We ignore set results.
         */
        override fun onSetSuccess() = Unit
        override fun onSetFailure(p0: String?) = Unit
    }

    call(observer)
}


suspend inline fun setValue(
    crossinline call: (SdpObserver) -> Unit
): Result<Unit> = suspendCoroutine {
    val observer = object : SdpObserver {
        /**
         * We ignore create results.
         */
        override fun onCreateFailure(p0: String?) = Unit
        override fun onCreateSuccess(p0: SessionDescription?) = Unit

        /**
         * Handling of set values.
         */
        override fun onSetSuccess() = it.resume(Result.success(Unit))
        override fun onSetFailure(message: String?) =
            it.resume(Result.failure(RuntimeException(message)))
    }

    call(observer)
}


private fun String.mungeCodecs(): String {
    return this.replace("vp9", "VP9").replace("vp8", "VP8").replace("h264", "H264")
}

fun SessionDescription.stringify(): String =
    "SessionDescription(type=$type, description=$description)"

fun MediaStreamTrack.stringify(): String {
    return "MediaStreamTrack(id=${id()}, kind=${kind()}, enabled: ${enabled()}, state=${state()})"
}

fun IceCandidateErrorEvent.stringify(): String {
    return "IceCandidateErrorEvent(errorCode=$errorCode, $errorText, address=$address, port=$port, url=$url)"
}