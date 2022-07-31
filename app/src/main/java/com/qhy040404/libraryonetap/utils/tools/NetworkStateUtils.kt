package com.qhy040404.libraryonetap.utils.tools

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
object NetworkStateUtils {
    private fun checkNetworkType(ctx: Context): Int {
        val context = ctx.applicationContext
        val netType = 0
        val manager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
            ?: return netType
        val networkCapabilities =
            manager.getNetworkCapabilities(manager.activeNetwork) ?: return netType
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2
            else -> netType
        }
    }

    fun checkNetworkTypeStr(ctx: Context): String {
        return when (checkNetworkType(ctx)) {
            1 -> "WIFI"
            2 -> "Cellular"
            else -> "Error"
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