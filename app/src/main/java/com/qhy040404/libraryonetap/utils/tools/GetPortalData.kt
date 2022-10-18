package com.qhy040404.libraryonetap.utils.tools

import android.os.StrictMode
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.utils.web.Requests

object GetPortalData {
    /**
     * mode 0:electric
     * mode 1:net
     */
    fun getPortalData(mode: Int): String {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        if (!GlobalValues.mainSessionReady) {
            Requests.init()
        }

        Requests.get(URLManager.PORTAL_DIRECT_URL)

        return when (mode) {
            0 -> Requests.post(
                URLManager.PORTAL_ELEC_URL,
                Constants.PORTAL_DEFAULT_POST,
                GlobalValues.ctJson
            )
            1 -> Requests.post(
                URLManager.PORTAL_NET_URL,
                Constants.PORTAL_DEFAULT_POST,
                GlobalValues.ctJson
            )
            else -> throw IllegalArgumentException("Unknown mode")
        }
    }
}
