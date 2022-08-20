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
    fun getPortalData(id: String, passwd: String, mode: Int): String {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build()
        )

        Requests.loginSso(URLManager.PORTAL_SSO_URL,
            GlobalValues.ctSso,
            URLManager.PORTAL_SSO_URL,
            needCheck = true)

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
