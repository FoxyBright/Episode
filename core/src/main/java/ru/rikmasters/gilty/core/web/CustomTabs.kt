package ru.rikmasters.gilty.core.web

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat

fun openInWeb(context: Context, uri: String) =
    openInWeb(context, Uri.parse(uri))

fun openInWeb(context: Context, uri: Uri) = CustomTabsIntent
    .Builder()
    .setUrlBarHidingEnabled(true)
    .setDefaultColorSchemeParams(colors())
    .build().let {
        it.intent.data = uri
        it.intent.addFlags(FLAG_ACTIVITY_NO_HISTORY)
        ContextCompat.startActivity(
            context, it.intent, it.startAnimationBundle
        )
    }

private fun colors() = CustomTabColorSchemeParams
    .Builder()
    .build()