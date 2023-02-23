package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class Permissions {
    
    companion object{
    
        fun openNotificationSettings(context: Context): Intent {
            val intent = Intent()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:${context.packageName}")
            }
            return intent
        }
    }
}