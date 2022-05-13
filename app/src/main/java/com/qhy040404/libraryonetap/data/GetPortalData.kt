package com.qhy040404.libraryonetap.data

import android.os.StrictMode
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.web.Requests
import okhttp3.MediaType

//mode 0:elec, mode 1:net
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

    val ct_json: MediaType = requests.strToMT("application/json;charset=UTF-8")
    val ct_sso: MediaType = requests.strToMT("application/x-www-form-urlencoded; charset=utf-8")

    val requestUrl =
        "https://sso.dlut.edu.cn/cas/login?service=https%3A%2F%2Fportal.dlut.edu.cn%2Ftp%2Fview%3Fm%3Dup#act=portal/viewhome"
    val elecUrl = "https://portal.dlut.edu.cn/tp/up/subgroup/getElectricCharge"
    val netUrl = "https://portal.dlut.edu.cn/tp/up/subgroup/getWlzzList"

    val ltResponse: String = requests.get(requestUrl)
    val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

    val rawData = "$id$passwd$ltData"
    val rsa: String = des.strEnc(rawData, "1", "2", "3")

    requests.post(requestUrl, requests.loginPostData(id, passwd, ltData, rsa), ct_sso)

    if (mode == 0) {
        returnJson = requests.post(elecUrl, "{}", ct_json)
    } else if (mode == 1) {
        returnJson = requests.post(netUrl, "{}", ct_json)
    }

    return returnJson
}