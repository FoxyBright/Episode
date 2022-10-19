package ru.rikmasters.gilty.core.app

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.rikmasters.gilty.core.log.Loggable

@Stable
class AppStateModel internal constructor(

    isSystemInDarkMode: Boolean,

    val systemUiController: SystemUiController,

    val snackbarHostState: SnackbarHostState,

): Loggable {

    var darkMode by mutableStateOf(isSystemInDarkMode)

    var dynamicColor by mutableStateOf(false)
}