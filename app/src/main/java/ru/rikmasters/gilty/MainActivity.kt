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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.bottomsheet.deeplink.DeepLinker.deepLink
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.app.AppEntrypoint
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.internetCheck
import ru.rikmasters.gilty.core.data.source.WebSource.Companion.ENV_BASE_URL
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.R.style.Theme_Gilty
import ru.rikmasters.gilty.shared.common.ErrorConnection
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.common.extentions.ChatNotificationBlocker.clearSelectChat
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.utils.FireBaseService

@ExperimentalMaterial3Api
class MainActivity: ComponentActivity() {
    
    private val authManager by inject<AuthManager>()
    private val chatManager by inject<ChatManager>()
    private val regManager by inject<RegistrationManager>()
    private val profileManager by inject<ProfileManager>()
    private val context by inject<Context>()
    private val env by inject<Environment>()
    
    private var _intent: Intent? by mutableStateOf(null)
    
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Theme_Gilty)
        super.onCreate(savedInstanceState)
        
        MapKitFactory.getInstance().onStart()
        
        CoroutineScope(IO).launch {
            inject<MeetingManager>()
                .value.clearAddMeet()
        }
        
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
            var errorState by remember {
                mutableStateOf(false)
            }
            val corScope = rememberCoroutineScope()

            var lastConnectionState by remember { mutableStateOf(false) }
            
            AppEntrypoint(GiltyTheme)
            
            GiltyTheme { if(errorState) ErrorConnection() }
            corScope.launch {
                while(true) {
                    delay(2000)
                    errorState = !internetCheck(context)
                    lastConnectionState = internetCheck(context)
                }
            }
            
            suspend fun authorize(userId: String) {
                try {
                    clearSelectChat(this)
                    authManager.savePushToken(token)
                    chatManager.connectWebSocket(userId)
                }catch (e:Exception){
                    context.errorToast(message = e.message)
                }
            }
            
            LaunchedEffect(Unit, lastConnectionState) {
                env[ENV_BASE_URL] = "$HOST$PREFIX_URL"
                if(internetCheck(context))
                    profileManager
                        .storageProfile()
                        ?.let { authorize(it.id) }
                        ?: run {
                            if(
                                authManager.hasTokens()
                                && regManager.profileCompleted()
                            ) authorize(regManager.userId())
                        }
            }
            
            val scope = getKoin().createScope<MainActivity>()
            val asm by inject<AppStateModel>()
            val nav by inject<NavState>()
            
            LaunchedEffect(intent) {
                if(authManager.hasTokens()
                    && regManager.profileCompleted()
                ) deepLink(scope, asm, intent, nav)
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