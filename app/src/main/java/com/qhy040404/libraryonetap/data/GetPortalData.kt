package com.qhy040404.libraryonetap.data

import android.os.StrictMode
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues.ctJson
import com.qhy040404.libraryonetap.constant.GlobalValues.ctSso
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.web.Requests

object GetPortalData {
    //mode 0:electric, mode 1:net
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
        val requests = Requests()
        val des = desEncrypt()

        var returnJson = ""

        val ltResponse: String = requests.get(URLManager.PORTAL_SSO_URL)
        val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

        val rawData = "$id$passwd$ltData"
        val rsa: String = des.strEnc(rawData, "1", "2", "3")

        requests.post(
            URLManager.PORTAL_SSO_URL,
            requests.loginPostData(id, passwd, ltData, rsa),
            ctSso
        )

        if (mode == 0) {
            returnJson =
                requests.post(URLManager.PORTAL_ELEC_URL, Constants.PORTAL_DEFAULT_POST, ctJson)
        } else if (mode == 1) {
            returnJson =
                requests.post(URLManager.PORTAL_NET_URL, Constants.PORTAL_DEFAULT_POST, ctJson)
        }

        return returnJson
    }
}