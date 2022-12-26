package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.os.Build
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import androidx.core.content.getSystemService

@Suppress("DEPRECATION")
fun vibrate(
    context: Context,
    time: Long = 50,
    amplitude: Int = 2
) {
    context.getSystemService<Vibrator>()?.let {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            it.vibrate(createOneShot(time, amplitude))
        else it.vibrate(time)
    }
}