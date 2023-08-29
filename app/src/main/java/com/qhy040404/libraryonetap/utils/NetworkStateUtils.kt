package com.qhy040404.libraryonetap.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.Constants

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
}
