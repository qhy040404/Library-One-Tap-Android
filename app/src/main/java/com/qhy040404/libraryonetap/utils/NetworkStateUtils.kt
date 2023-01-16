package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.Constants

@Suppress("DEPRECATION")
object NetworkStateUtils {
    fun checkNetworkType(): String {
        val manager =
            ContextCompat.getSystemService(LibraryOneTapApp.app, ConnectivityManager::class.java)
                ?: return Constants.GLOBAL_ERROR
        val networkCapabilities =
            manager.getNetworkCapabilities(manager.activeNetwork) ?: return Constants.GLOBAL_ERROR
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            else -> Constants.GLOBAL_ERROR
        }
    }

    fun getSSID(ctx: Context): String {
        val wifiManager: WifiManager =
            ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiName = wifiManager.connectionInfo.ssid
        return if (wifiName.startsWith("\"")) {
            wifiName.substring(1, wifiName.length - 1)
        } else {
            wifiName
        }
    }
}
