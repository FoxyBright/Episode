package ru.rikmasters.gilty.translations.webrtc.model.signalling
data class SignallingMessage(
    val command: SignalingCommand,
    val body: Map<String, *>
)

fun  Map<String, *>.mapToMessage(): SignallingMessage {
    val commandValue = this["command"] as? String
    val command = commandValue?.toSignallingCommand() ?: SignalingCommand.UNKNOW
    return SignallingMessage(
        command = command,
        body = this
    )
}

data class AnswerMessage(
    val command: String,
    val id: Int,
    val sdp: SdpAnswer
)