package com.qhy040404.libraryonetap.ui.tools

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.datetime.Datetime
import com.qhy040404.datetime.Datetime.Companion.toDateTime
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.ExamsTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONArray

class ExamsActivity : SimplePageActivity() {
    private var currentVisible = true
    private val tempValues = ExamsTempValues()

    override fun initializeViewPref() {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }
    }

    override fun initializeView() {
        initView()
        innerThread = Thread(PrepareData())
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            if (tempValues.courseName.isEmpty()) {
                add(Card(
                    "暂无考试"
                ))
            }
            val finishedList: MutableList<List<String>> = mutableListOf()
            for (i in tempValues.courseName.indices) {
                val examTime = tempValues.examTime[i].split("~").first().toDateTime()
                val now = Datetime.now()
                if (examTime.isBefore(now)) {
                    finishedList.add(
                        listOf(
                            tempValues.courseName[i],
                            """
                                时间：${tempValues.examTime[i]}
                                考场：${tempValues.room[i]}
                            """.trimIndent()
                        )
                    )
                    continue
                }

                val name = tempValues.courseName[i]
                val desc = """
                    时间：${tempValues.examTime[i]}
                    考场：${tempValues.room[i]}
                """.trimIndent()
                add(ClickableItem(
                    name,
                    desc
                ))
            }
            if (finishedList.size != 0) {
                add(Category("已完成"))
                finishedList.forEach {
                    add(ClickableItem(
                        it.first(),
                        it.last()
                    ))
                }
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        currentVisible = true
    }

    override fun onPause() {
        super.onPause()
        currentVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        LibraryOneTapApp.instance?.removeActivity(this)
    }

    private inner class PrepareData : Runnable {
        override fun run() {
            Looper.prepare()
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )

            if (!AppUtils.hasNetwork()) {
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@ExamsActivity)
                        .setMessage(R.string.net_disconnected)
                        .setTitle(R.string.exams_title)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            finish()
                        }
                        .setCancelable(true)
                        .create().also {
                            if (this@ExamsActivity.currentVisible) {
                                it.show()
                            }
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
                val ltData = runCatching {
                    "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
                }.getOrDefault(Constants.STRING_NULL)
                val ltExecution = runCatching {
                    ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
                }.getOrDefault(Constants.STRING_NULL)

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
                        runOnUiThread {
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
                                    if (this@ExamsActivity.currentVisible) {
                                        it.show()
                                    }
                                }
                        }
                        Looper.loop()
                        break
                    }
                }
            }
            if (loginSuccess) {
                if (GlobalValues.majorStuId == 0) {
                    val initUrl = Requests.get(URLManager.EDU_GRADE_INIT_URL, null, true)
                    val initData = Requests.get(URLManager.EDU_GRADE_INIT_URL)
                    GlobalValues.majorStuId = if (initUrl.contains("semester-index")) {
                        initUrl.split("/").last().toInt()
                    } else {
                        val initList =
                            initData.split("onclick=\"myFunction(this)\" value=\"")
                        if (initList.size == 3) {
                            val aStuId = initList[1].split("\"")[0].toInt()
                            val bStuId = initList[2].split("\"")[0].toInt()
                            when {
                                aStuId > bStuId -> {
                                    GlobalValues.minorStuId = aStuId
                                    majorStuId = bStuId
                                }
                                bStuId > aStuId -> {
                                    GlobalValues.minorStuId = bStuId
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
                    Requests.get(URLManager.getEduExamsUrl(GlobalValues.majorStuId))
                        .split("var studentExamInfoVms = ")[1]
                        .split("];")[0] + "]"
                if (GlobalValues.minorStuId != 0) {
                    Thread.sleep(3000L)
                    examsMinorData =
                        Requests.get(URLManager.getEduExamsUrl(GlobalValues.minorStuId))
                            .split("var studentExamInfoVms = ")[1]
                            .split("];")[0] + "]"
                }

                val majorArray = JSONArray(examsMajorData)
                for (i in 0 until majorArray.length()) {
                    val course = majorArray.optJSONObject(i)!!
                    tempValues.courseName.add(
                        course.optJSONObject("course")!!.optString("nameZh")
                    )
                    tempValues.examTime.add(
                        course.optString("examTime")
                    )
                    tempValues.room.add(
                        if (!course.isNull("examPlace")) {
                            course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                .optString("nameZh")
                        } else {
                            "暂未安排考场"
                        }
                    )
                }

                if (examsMinorData != null) {
                    val minorArray = JSONArray(examsMinorData)
                    for (i in 0 until minorArray.length()) {
                        val course = minorArray.optJSONObject(i)!!
                        tempValues.courseName.add(
                            course.optJSONObject("course")!!.optString("nameZh") + "辅修"
                        )
                        tempValues.examTime.add(
                            course.optString("examTime")
                        )
                        tempValues.room.add(
                            if (!course.isNull("examPlace")) {
                                course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                    .optString("nameZh")
                            } else {
                                "暂未安排考场"
                            }
                        )
                    }
                }
            }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}
