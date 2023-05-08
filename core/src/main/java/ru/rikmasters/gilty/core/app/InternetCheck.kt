package ru.rikmasters.gilty.core.app

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI

fun internetCheck(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    
    return connectivityManager.getNetworkCapabilities(
        connectivityManager.activeNetwork
    )?.let {
        it.hasTransport(TRANSPORT_CELLULAR)
                || it.hasTransport(TRANSPORT_WIFI)
                || it.hasTransport(TRANSPORT_ETHERNET)
    } ?: false
}