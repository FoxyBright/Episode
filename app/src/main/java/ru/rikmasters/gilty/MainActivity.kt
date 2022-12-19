package ru.rikmasters.gilty

import android.R.id.content
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowInsetsControllerCompat
import ru.rikmasters.gilty.core.app.appEntrypoint
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GSnackbar

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {

//    private val env: Environment by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowInsetsControllerCompat(
            window, window.decorView.findViewById(content)
        ).let {
            it.hide(1 shl 1)
            it.systemBarsBehavior = 2
        }
        
        setContent {
            appEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) }
            )
        }
    }
}