package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import ru.rikmasters.gilty.core.app.appEntrypoint
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GSnackbar

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            appEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) }
            )
        }
    }
}