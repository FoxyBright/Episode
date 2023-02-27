package ru.rikmasters.gilty.shared.common.extentions

import java.io.File
import java.io.InputStream

interface FileSource {
    
    fun bytes(): ByteArray
}

data class JavaFileSource(
    val file: File,
): FileSource {
    
    override fun bytes(): ByteArray =
        file.readBytes()
}

data class InputStreamSource(
    val stream: InputStream,
): FileSource {
    
    override fun bytes(): ByteArray =
        stream.use { it.readBytes() }
}