package ru.rikmasters.gilty.translations.webrtc.model.signalling

enum class SignalingCommand {
    UNKNOW,
    REQUEST_OFFER,
    ANSWER,
    OFFER,
    CANDIDATE
}

fun String.toSignallingCommand() =
    when(this) {
        "request_offer" -> SignalingCommand.REQUEST_OFFER
        "answer" -> SignalingCommand.ANSWER
        "offer" -> SignalingCommand.OFFER
        else -> SignalingCommand.UNKNOW
    }

fun SignalingCommand.toJsonString() =
    when(this) {
        SignalingCommand.REQUEST_OFFER -> "request_offer"
        SignalingCommand.ANSWER -> "answer"
        SignalingCommand.OFFER -> "offer"
        SignalingCommand.UNKNOW -> "unknow"
        SignalingCommand.CANDIDATE -> "candidate"
    }

