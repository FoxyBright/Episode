package ru.rikmasters.gilty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.core.app.AppEntrypoint
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.shared.BuildConfig
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GLoader
import ru.rikmasters.gilty.ui.GSnackbar

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    private val env by inject<Environment>()
    
    private var _intent: Intent? by mutableStateOf(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AppEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) },
                { isLoading, content -> GLoader(isLoading, content) }
            )
            LaunchedEffect(Unit) {
                env[WebSource.ENV_BASE_URL] = BuildConfig.HOST + BuildConfig.PREFIX_URL
            }
        }
    }
    
    override fun getIntent(): Intent? =
        _intent ?: super.getIntent()?.let { _intent = it; _intent!! }
    
    override fun setIntent(newIntent: Intent?) {
        _intent = newIntent
        super.setIntent(newIntent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        _intent = intent
    }
}