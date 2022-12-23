package ru.rikmasters.gilty.core.app

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.core.view.WindowCompat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun rememberKeyboardController(): KeyboardController {
    val softwareController = LocalSoftwareKeyboardController.current!!
    val window = (LocalContext.current as Activity).window
    return remember { KeyboardController(softwareController, window) }
}

interface SoftInputMode { val value: Int }

enum class SoftInputState(
    override val value: Int
): SoftInputMode {
    Unspecified(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED),
    Unchanged(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED),
    Hidden(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN),
    AlwaysHidden(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN),
    Visible(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE),
    AlwaysVisible(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
}

enum class SoftInputAdjust(
    override val value: Int
): SoftInputMode {
    Unspecified(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED),
    Pan(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN),
    Nothing(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
}

@OptIn(ExperimentalComposeUiApi::class)
@Stable
class KeyboardController(
    private val softwareController: SoftwareKeyboardController,
    private val window: Window
) {
    companion object {
        const val DEFAULT_DECOR_FITS_SYSTEM_WINDOWS = false
        val DEFAULT_SOFT_INPUT_STATE = SoftInputState.Hidden
        val DEFAULT_SOFT_INPUT_ADJUST = SoftInputAdjust.Pan
    }
    
    fun hide() { softwareController.hide() }
    
    fun show() { softwareController.show() }
    
    fun setDecorFitsWindows(boolean: Boolean?) {
        WindowCompat.setDecorFitsSystemWindows(
            window,
            boolean ?: DEFAULT_DECOR_FITS_SYSTEM_WINDOWS
        )
    }
    
    fun resetSoftInputAdjust() =
        doSetSoftInputMode(DEFAULT_SOFT_INPUT_ADJUST)
    
    fun resetSoftInputState() =
        doSetSoftInputMode(DEFAULT_SOFT_INPUT_STATE)
    
    fun resetSoftInputMode() {
        resetSoftInputAdjust()
        resetSoftInputState()
    }
    
    fun setSoftInputMode(adjust: SoftInputAdjust) =
        doSetSoftInputMode(adjust)
    
    fun setSoftInputMode(state: SoftInputState) =
        doSetSoftInputMode(state)
    
    private fun doSetSoftInputMode(mode: SoftInputMode) {
        window.setSoftInputMode(mode.value)
    }
    
    init {
        setDecorFitsWindows(null)
        resetSoftInputMode()
    }
}