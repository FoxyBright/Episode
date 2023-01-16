package ru.rikmasters.gilty.core.permission

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.core.app.ActivityCompat


object PermissionUtils {
    fun requestPermissions(context: Context) {
        val activity = context as Activity
        if(SDK_INT >= 30) try {
            activity.startActivityForResult(
                Intent(
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                ).addCategory("android.intent.category.DEFAULT"), (101)
            )
        } catch(_: Exception) {
            activity.startActivityForResult(
                Intent(
                    ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                ), (101)
            )
        }
        else ActivityCompat.requestPermissions(
            activity, arrayOf(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            ), (101)
        )
    }
}