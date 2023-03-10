package ru.rikmasters.gilty.core.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.google.accompanist.systemuicontroller.SystemUiController
import ru.rikmasters.gilty.core.app.ui.BottomSheetState
import ru.rikmasters.gilty.core.log.Loggable

@Stable
class AppStateModel internal constructor(

    isSystemInDarkMode: Boolean,

    val systemUi: SystemUiController,
    
    @Suppress("unused")
    val snackbar: SnackbarHostState,

    val bottomSheet: BottomSheetState,
    
    val keyboard: KeyboardController,
    

): Loggable {

    var darkMode by mutableStateOf(isSystemInDarkMode)

    var dynamicColor by mutableStateOf(false)
}