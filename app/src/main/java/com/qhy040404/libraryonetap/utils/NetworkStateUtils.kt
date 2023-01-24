package com.qhy040404.libraryonetap.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.Constants

@Suppress("DEPRECATION")
object NetworkStateUtils {
    fun checkNetworkType(): String {
        val manager = LibraryOneTapApp.app.getSystemService<ConnectivityManager>()
            ?: return Constants.GLOBAL_ERROR
        val networkCapabilities =
            manager.getNetworkCapabilities(manager.activeNetwork) ?: return Constants.GLOBAL_ERROR
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            else -> Constants.GLOBAL_ERROR
        }
    }

    fun getSSID(): String {
        val wifiManager =
            LibraryOneTapApp.app.getSystemService<WifiManager>() ?: return "<unknown ssid>"
        return wifiManager.connectionInfo.ssid.let {
            if (it.startsWith("\"")) {
                it.substring(1, it.length - 1)
            } else {
                it
            }
        }
    }
}
