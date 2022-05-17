package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class NetworkStateUtils {
    private fun checkNetworkType(ctx: Context): Int {
        val context = ctx.applicationContext
        val netType = 0
        val manager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
            ?: return netType
        val networkInfo = manager.activeNetworkInfo ?: return netType
        return when (networkInfo.type) {
            ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_ETHERNET -> 1
            ConnectivityManager.TYPE_MOBILE -> when (networkInfo.subtype) {
                TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_EHRPD -> 2
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> 2
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE -> 2
                else -> netType
            }
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
        var wifiName = wifiManager.connectionInfo.ssid
        if (wifiName.contains("\"")) {
            wifiName = wifiName.substring(1, wifiName.length - 1)
        }
        return wifiName
    }
}