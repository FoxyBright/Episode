package ru.rikmasters.gilty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.app.AppEntrypoint
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.presentation.model.FireBaseService
import ru.rikmasters.gilty.shared.BuildConfig
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GLoader
import ru.rikmasters.gilty.ui.GSnackbar

const val TOPIC_ALL = "all"

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    private val env by inject<Environment>()
    private val authManager by inject<AuthManager>()
    
    private var _intent: Intent? by mutableStateOf(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        FireBaseService.sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        
        var token = ""
        FirebaseMessaging
            .getInstance()
            .token
            .addOnSuccessListener {
                FireBaseService.token = it
                token = it
            }
        
        FirebaseMessaging
            .getInstance()
            .subscribeToTopic(TOPIC_ALL)
        
        setContent {
            AppEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) },
                { isLoading, content -> GLoader(isLoading, content) }
            )
            
            LaunchedEffect(Unit) {
                
                val isAuthorized = MutableStateFlow(authManager.isAuthorized())
                if(isAuthorized.value) authManager.savePushToken(token)
                
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