package ru.rikmasters.gilty.shared.common.extentions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri.parse
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions

class Permissions {
    
    companion object {
        
        fun openNotificationSettings(context: Context): Intent {
            val intent = Intent()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = parse("package:${context.packageName}")
            }
            return intent
        }
        
        fun requestPermission(context: Context) {
            requestPermissions(
                context as Activity, arrayOf(
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE
                ), (101)
            )
        }
        
        // opens the application settings to give access to all files. Is a dangerous permission
        @Suppress("unused")
        @RequiresApi(Build.VERSION_CODES.R)
        fun openStorageSettings(context: Context): Intent {
            val activity = context as Activity
            return try {
                Intent(
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    parse("package:${activity.packageName}")
                ).addCategory("android.intent.category.DEFAULT")
            } catch(_: Exception) {
                Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }
        }
    }
}