package ru.rikmasters.gilty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowCompat
import ru.rikmasters.gilty.core.app.AppEntrypoint
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GSnackbar

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AppEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) }
            )
        }
    }
}