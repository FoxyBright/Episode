package ru.rikmasters.gilty.shared.shared

import android.content.Context
import id.zelory.compressor.Compressor
import java.io.File

suspend infix fun File.compress(con: Context) =
    Compressor.compress(con, this)

