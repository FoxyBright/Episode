package ru.rikmasters.gilty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.bottomsheet.DeepLinker.deepLink
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.app.AppEntrypoint
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.internetCheck
import ru.rikmasters.gilty.core.app.ui.ErrorConnection
import ru.rikmasters.gilty.core.data.source.WebSource.Companion.ENV_BASE_URL
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.presentation.model.FireBaseService
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.shared.LoadingIndicator
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.ui.GBottomSheetBackground
import ru.rikmasters.gilty.ui.GLoader
import ru.rikmasters.gilty.ui.GSnackbar

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    private val env by inject<Environment>()
    private val authManager by inject<AuthManager>()
    private val profileManager by inject<ProfileManager>()
    private val regManager by inject<RegistrationManager>()
    private val chatManager by inject<ChatManager>()
    private val context by inject<Context>()
    
    private var _intent: Intent? by mutableStateOf(null)
    
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // TODO вынести в BuildConfig
        MapKitFactory.setApiKey("6eb87a4e-7668-4cf6-a691-36051b71e2e5")
        MapKitFactory.initialize(this)
        FireBaseService.sharedPref = getSharedPreferences(
            "sharedPref", MODE_PRIVATE
        )
        
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
            .subscribeToTopic("all")
        
        setContent {
            var errorState by remember { mutableStateOf(false) }
            val corScope = rememberCoroutineScope()
            
            AppEntrypoint(
                GiltyTheme,
                { GBottomSheetBackground(it) },
                { GSnackbar(it) },
                { isLoading, content -> GLoader(isLoading, content) },
                { state, offset, trigger -> LoadingIndicator(state, offset, trigger) }
            )
            
            if(errorState) ErrorConnection()
            
            corScope.launch {
                while(true) {
                    delay(2000)
                    errorState = !internetCheck(context)
                }
            }
            
            LaunchedEffect(Unit) {
                val userAuthorized = profileManager.checkProfileStore()
                
                if(internetCheck(context)) if(userAuthorized) {
                    authManager.savePushToken(token)
                    chatManager.connect(
                        profileManager.getProfile(false).id
                    )
                } else if(
                    authManager.isAuthorized()
                    && regManager.profileCompleted()
                ) {
                    authManager.savePushToken(token)
                    chatManager.connect(
                        regManager.userId()
                    )
                }
                
                env[ENV_BASE_URL] = "$HOST$PREFIX_URL"
            }
            
            val asm by inject<AppStateModel>()
            val scope = getKoin().createScope<MainActivity>()
            
            LaunchedEffect(intent) {
                if(authManager.isAuthorized()
                    && regManager.profileCompleted()
                ) deepLink(scope, asm, intent)
            }
        }
    }
    
    override fun getIntent() = _intent ?: super.getIntent()
        ?.let { _intent = it; _intent!! }
    
    
    override fun setIntent(newIntent: Intent?) {
        _intent = newIntent
        super.setIntent(newIntent)
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        _intent = intent
    }
}