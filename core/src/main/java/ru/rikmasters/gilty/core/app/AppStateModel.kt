package ru.rikmasters.gilty.core.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.accompanist.systemuicontroller.SystemUiController
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.log.Loggable

@Stable
class AppStateModel internal constructor(

    isSystemInDarkMode: Boolean,

    val systemUi: SystemUiController,

    val snackbar: SnackbarHostState,

    val bottomSheet: BottomSheetState,
    
    val keyboard: KeyboardController,

): Loggable {

    var darkMode by mutableStateOf(isSystemInDarkMode)

    var dynamicColor by mutableStateOf(false)
}