package com.qhy040404.libraryonetap.ui.tools

import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.ExamsTempValues
import com.qhy040404.libraryonetap.temp.GradesTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONArray

class ExamsActivity : SimplePageActivity() {
    private var currentVisible = true

    override fun initializeViewPref() {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }
    }

    override fun initializeView() {
        initView()
        ExamsTempValues.clear()
        innerThread = Thread(PrepareData())
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            if (ExamsTempValues.courseName.isEmpty()) {
                add(Card(
                    "暂无考试"
                ))
            }
            for (name in ExamsTempValues.courseName) {
                val i = ExamsTempValues.courseName.indexOf(name)
                val desc = """
                    时间：${ExamsTempValues.examTime[i]}
                    楼宇：${ExamsTempValues.building[i]}
                    教室：${ExamsTempValues.room[i]}
                """.trimIndent()
                add(ClickableItem(
                    name,
                    desc
                ))
            }
        }
    }

    private fun initView() {
        LibraryOneTapApp.instance?.addActivity(this)

        findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.VISIBLE

        if (!GlobalValues.md3) {
            toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }
    }

    override fun onResume() {
        super.onResume()
        currentVisible = true
    }

    override fun onPause() {
        super.onPause()
        currentVisible = false
    }

    private inner class PrepareData : Runnable {
        override fun run() {
            Looper.prepare()
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )

            if (!AppUtils.hasNetwork()) {
                Handler(Looper.getMainLooper()).post {
                    MaterialAlertDialogBuilder(this@ExamsActivity)
                        .setMessage(R.string.net_disconnected)
                        .setTitle(R.string.exams_title)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            finish()
                        }
                        .setCancelable(true)
                        .create().also {
                            if (this@ExamsActivity.currentVisible) it.show()
                        }
                }
                Looper.loop()
                return
            }

            var majorStuId = 0

            val id = GlobalValues.id
            val passwd = GlobalValues.passwd

            var loginSuccess = false
            var timer = 0

            while (!loginSuccess && AppUtils.checkData(id, passwd)) {
                val ltResponse = Requests.get(URLManager.EDU_LOGIN_SSO_URL)
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

                if (ltData.isNotEmpty()) {
                    val rawData = "$id$passwd$ltData"
                    val rsa = GlobalManager.des.strEnc(rawData, "1", "2", "3")

                    Thread.sleep(200L)

                    Requests.post(
                        URLManager.EDU_LOGIN_SSO_URL,
                        Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
                        GlobalValues.ctSso
                    )
                }

                val session = Requests.get(URLManager.EDU_CHECK_URL)
                if (session.contains("person")) {
                    loginSuccess = true
                } else {
                    timer++
                    if (timer == 2) {
                        Requests.netLazyMgr.reset()
                        CookieJarImpl.reset()
                    }
                    if (timer >= 3) {
                        Handler(Looper.getMainLooper()).post {
                            MaterialAlertDialogBuilder(this@ExamsActivity)
                                .setTitle(R.string.exams_title)
                                .setMessage(when (session) {
                                    Constants.NET_DISCONNECTED -> R.string.net_disconnected
                                    Constants.NET_ERROR -> R.string.net_error
                                    Constants.NET_TIMEOUT -> R.string.net_timeout
                                    else -> R.string.fail_to_login_three_times
                                })
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    finish()
                                }
                                .setCancelable(false)
                                .create().also {
                                    if (this@ExamsActivity.currentVisible) it.show()
                                }
                        }
                        Looper.loop()
                        break
                    }
                }
            }
            if (loginSuccess) {
                if (GradesTempValues.majorStuId == 0) {
                    val initUrl = Requests.get(URLManager.EDU_GRADE_INIT_URL, null, true)
                    val initData = Requests.get(URLManager.EDU_GRADE_INIT_URL)
                    GradesTempValues.majorStuId = if (initUrl.contains("semester-index")) {
                        initUrl.split("/").last().toInt()
                    } else {
                        val initList =
                            initData.split("onclick=\"myFunction(this)\" value=\"")
                        if (initList.size == 3) {
                            val aStuId = initList[1].split("\"")[0].toInt()
                            val bStuId = initList[2].split("\"")[0].toInt()
                            when {
                                aStuId > bStuId -> {
                                    GradesTempValues.minorStuId = aStuId
                                    majorStuId = bStuId
                                }
                                bStuId > aStuId -> {
                                    GradesTempValues.minorStuId = bStuId
                                    majorStuId = aStuId
                                }
                                else -> throw IllegalStateException("Illegal Student ID")
                            }
                        }
                        majorStuId
                    }
                }

                Thread.sleep(3000L)
                var examsMinorData: String? = null

                val examsMajorData =
                    Requests.get(URLManager.getEduExamsUrl(GradesTempValues.majorStuId))
                        .split("var studentExamInfoVms = ")[1]
                        .split("];")[0] + "]"
                if (GradesTempValues.minorStuId != 0) examsMinorData =
                    Requests.get(URLManager.getEduExamsUrl(GradesTempValues.minorStuId))
                        .split("var studentExamInfoVms = ")[1]
                        .split("];")[0] + "]"

                val majorArray = JSONArray(examsMajorData)
                for (i in 0 until majorArray.length()) {
                    val all = majorArray.optJSONObject(i).optJSONObject("examPlace") ?: break
                    ExamsTempValues.courseName.add(
                        all.optString("courseNameStr")
                    )
                    ExamsTempValues.examTime.add(
                        all.optJSONObject("examTime")!!.optString("dateTimeString")
                    )
                    ExamsTempValues.building.add(
                        all.optJSONObject("room")!!.optJSONObject("building")!!.optString("nameZh")
                    )
                    ExamsTempValues.room.add(
                        all.optJSONObject("room")!!.optString("nameZh")
                    )
                }

                if (examsMinorData != null) {
                    val minorArray = JSONArray(examsMinorData)
                    for (i in 0 until minorArray.length()) {
                        val all = minorArray.optJSONObject(i).optJSONObject("examPlace") ?: break
                        ExamsTempValues.courseName.add(
                            all.optString("courseNameStr") + "辅修"
                        )
                        ExamsTempValues.examTime.add(
                            all.optJSONObject("examTime")!!.optString("dateTimeString")
                        )
                        ExamsTempValues.building.add(
                            all.optJSONObject("room")!!.optJSONObject("building")!!
                                .optString("nameZh")
                        )
                        ExamsTempValues.room.add(
                            all.optJSONObject("room")!!.optString("nameZh")
                        )
                    }
                }
            }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}
