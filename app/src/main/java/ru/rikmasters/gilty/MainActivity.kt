package ru.rikmasters.gilty

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.core.app.appEntrypoint
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GSnackbar
import java.net.URL

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val env: Environment by inject()

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