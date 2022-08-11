package com.qhy040404.libraryonetap.utils.tools

import android.os.StrictMode
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
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
        val des = DesEncryptUtils()

        var loginSuccess = false
        var timer = 0

        while (!loginSuccess) {
            val ltResponse = Requests.get(URLManager.PORTAL_SSO_URL)
            val ltData = try {
                "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
            } catch (_: Exception) {
                ""
            }
            val ltExecution = try {
                ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
            } catch (_: Exception) {
                ""
            }

            if (ltData != "") {
                val rawData = "$id$passwd$ltData"
                val rsa = des.strEnc(rawData, "1", "2", "3")

                Requests.post(
                    URLManager.PORTAL_SSO_URL,
                    Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
                    GlobalValues.ctSso
                )
            }

            val session = Requests.get(URLManager.PORTAL_SSO_URL)
            if (!session.contains("统一身份")) {
                loginSuccess = true
            }
            timer++
            if (timer >= 3) break
        }

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