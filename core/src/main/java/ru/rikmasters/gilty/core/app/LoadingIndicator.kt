package ru.rikmasters.gilty.core.app

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.core.content.getSystemService
import com.google.accompanist.swiperefresh.SwipeRefreshState
import ru.rikmasters.gilty.core.R

@Composable
fun LoadingIndicator(
    swipeRefreshState: SwipeRefreshState,
    offset: Dp,
    trigger: Dp,
) {
    val context = LocalContext.current
    
    val size = remember(offset) { min(offset, trigger) }
    
    LaunchedEffect(size == trigger) {
        if(size == trigger)
            vibrate(context)
    }
    
    Box(Modifier, Alignment.Center) {
        Loader(
            resource = R.raw.loaging,
            modifier = Modifier.size(size),
            isPlaying = swipeRefreshState.isRefreshing
        )
    }
}

@Suppress("DEPRECATION")
fun vibrate(
    context: Context,
    time: Long = 50,
    amplitude: Int = 2
) {
    context.getSystemService<Vibrator>()?.let {
        if(SDK_INT >= O)
            it.vibrate(createOneShot(time, amplitude))
        else it.vibrate(time)
    }
}