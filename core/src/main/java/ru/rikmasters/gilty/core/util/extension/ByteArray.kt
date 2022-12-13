package ru.rikmasters.gilty.core.util.extension

fun ByteArray.toHex(): String =
    joinToString(separator = "") { byte -> "%02x".format(byte) }
        .replace(Regex(".."), "$0 ")