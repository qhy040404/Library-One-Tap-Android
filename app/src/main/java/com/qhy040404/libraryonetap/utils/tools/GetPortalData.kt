package com.qhy040404.libraryonetap.utils.tools

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
        Requests.loginSso(
            URLManager.PORTAL_SSO_URL,
            GlobalValues.ctSso,
            URLManager.PORTAL_SSO_URL
        ).let {
            if (it.not()) {
                return GlobalValues.ssoPrompt
            }
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
