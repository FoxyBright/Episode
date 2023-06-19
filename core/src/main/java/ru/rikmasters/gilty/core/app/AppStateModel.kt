package ru.rikmasters.gilty.core.app

import androidx.compose.runtime.Stable
import com.google.accompanist.systemuicontroller.SystemUiController
import ru.rikmasters.gilty.core.app.ui.BottomSheetState

@Stable
class AppStateModel(
    val systemUi: SystemUiController,
    val bottomSheet: BottomSheetState,
    val keyboard: KeyboardController,
)