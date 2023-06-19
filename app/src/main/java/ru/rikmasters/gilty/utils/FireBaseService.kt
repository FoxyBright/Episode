package ru.rikmasters.gilty.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri.parse
import android.os.Build
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.rikmasters.gilty.MainActivity
import ru.rikmasters.gilty.shared.R
import kotlin.random.Random

private const val CHATS_CHANNEL = "chats"
private const val MEETS_CHANNEL = "messages"

class FireBaseService: FirebaseMessagingService() {
    
    companion object {
        
        var sharedPref: SharedPreferences? = null
        
        var token: String?
            get() = sharedPref?.getString("token", "")
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }
    
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }
    
    @SuppressLint("WrongConstant", "UnspecifiedImmutableFlag")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        
        if(Build.VERSION.SDK_INT >= O) {
            createNotificationChannel(
                getString(R.string.chats_push_channel_name),
                getString(R.string.chats_push_channel_descriptions),
                CHATS_CHANNEL, notificationManager
            )
            createNotificationChannel(
                getString(R.string.meets_push_channel_name),
                getString(R.string.meets_push_channel_descriptions),
                MEETS_CHANNEL, notificationManager
            )
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.data = parse(
            if(message.data["type"] == "CHAT_MESSAGE")
                "${message.data["type"]}${message.data["content"]}"
            else message.data.toString()
        )
        
        if(notifyRule(message))
            notificationManager.notify(
                Random.nextInt(),
                notification(message, intent)
            )
    }
    
    private fun Context.notifyRule(
        message: RemoteMessage,
    ) = if(message.data["type"] != "CHAT_MESSAGE")
        true else message.data["content"]
        ?.substringAfter("\"chat\":{\"id\":\"")
        ?.substringBefore("\"")
        ?.let { chatId ->
            this.getSharedPreferences("sharedPref", MODE_PRIVATE)
                .getString("opened_chat", "")
                .let { opened ->
                    if(opened.isNullOrBlank())
                        return true
                    chatId != opened
                }
        } ?: true
    
    @RequiresApi(O)
    private fun createNotificationChannel(
        name: String,
        description: String,
        channel_id: String,
        notificationManager: NotificationManager,
    ) {
        val chanel = NotificationChannel(
            channel_id, name, IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(
            chanel.apply {
                this.description = description
                enableLights(true)
                lightColor = Color.GREEN
            })
    }
    
    private fun Context.notification(
        message: RemoteMessage,
        intent: Intent,
    ) = NotificationCompat
        .Builder(
            (this), when(message.data["type"]) {
                "CHAT_MESSAGE" -> CHATS_CHANNEL
                else -> MEETS_CHANNEL
            }
        )
        .setContentTitle(message.notification?.title)
        .setContentText(message.notification?.body)
        .setSmallIcon(R.drawable.small_notification_icon)
        .setAutoCancel(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this, 0, intent,
                (FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
            )
        ).build()
}