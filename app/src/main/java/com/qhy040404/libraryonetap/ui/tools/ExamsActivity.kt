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
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.tools.Exam
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONArray

class ExamsActivity : SimplePageActivity() {
    private var currentVisible = true
    private val exams = mutableListOf<Exam>()
    private val now = Datetime.now()

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
            if (exams.isEmpty()) {
                add(Card(
                    AppUtils.getResString(R.string.no_exams)
                ))
            }

            exams.filter { it.startTime > now }.forEach { pending ->
                val name = pending.name
                val desc = String.format(
                    AppUtils.getResString(R.string.exam_template),
                    pending.time,
                    pending.room
                )
                add(ClickableItem(
                    name,
                    desc
                ))
            }

            exams.filter { it.startTime < now }.let { finished ->
                if (finished.isNotEmpty()) {
                    add(Category(AppUtils.getResString(R.string.finished_list)))
                    finished.forEach { finish ->
                        val name = finish.name
                        val desc = String.format(
                            AppUtils.getResString(R.string.exam_template),
                            finish.time,
                            finish.room
                        )
                        add(ClickableItem(
                            name,
                            desc
                        ))
                    }
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
                    ltResponse.substringBetween("LT", "cas", includeDelimiter = true)
                }.getOrDefault(Constants.STRING_NULL)
                val ltExecution = runCatching {
                    ltResponse.substringBetween("name=\"execution\" value=\"", "\"")
                }.getOrDefault(Constants.STRING_NULL)

                if (ltData.isNotEmpty()) {
                    val rawData = "$id$passwd$ltData"
                    val rsa = DesEncryptUtils.strEnc(rawData, "1", "2", "3")

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
                        initUrl.substringAfter("/").toInt()
                    } else {
                        val initList =
                            initData.split("onclick=\"myFunction(this)\" value=\"")
                        if (initList.size == 3) {
                            val aStuId = initList[1].substringBefore("\"").toInt()
                            val bStuId = initList[2].substringBefore("\"").toInt()
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

                Thread.sleep(2500L)
                var examsMinorData: String? = null

                val examsMajorData =
                    Requests.get(URLManager.getEduExamsUrl(GlobalValues.majorStuId))
                        .substringBetween("var studentExamInfoVms = ", "];") + "]"
                if (GlobalValues.minorStuId != 0) {
                    Thread.sleep(2500L)
                    examsMinorData =
                        Requests.get(URLManager.getEduExamsUrl(GlobalValues.minorStuId))
                            .substringBetween("var studentExamInfoVms = ", "];") + "]"
                }

                val majorArray = JSONArray(examsMajorData)
                for (i in 0 until majorArray.length()) {
                    val course = majorArray.optJSONObject(i)!!
                    val time = course.optString("examTime")
                    val datetime = time.split(" ")
                    exams.add(
                        Exam(
                            course.optJSONObject("course")!!.optString("nameZh"),
                            time,
                            "${datetime[0]} ${datetime[1].substringBefore("~")}".toDateTime(),
                            "${datetime[0]} ${datetime[1].substringAfter("~")}".toDateTime(),
                            if (!course.isNull("examPlace")) {
                                course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                    .optString("nameZh")
                            } else {
                                AppUtils.getResString(R.string.empty_exam_place)
                            }
                        )
                    )
                }

                if (examsMinorData != null) {
                    val minorArray = JSONArray(examsMinorData)
                    for (i in 0 until minorArray.length()) {
                        val course = minorArray.optJSONObject(i)!!
                        val time = course.optString("examTime")
                        val datetime = time.split(" ")
                        exams.add(
                            Exam(
                                course.optJSONObject("course")!!
                                    .optString("nameZh") + AppUtils.getResString(R.string.minor),
                                time,
                                "${datetime[0]} ${datetime[1].substringBefore("~")}".toDateTime(),
                                "${datetime[0]} ${datetime[1].substringAfter("~")}".toDateTime(),
                                if (!course.isNull("examPlace")) {
                                    course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                        .optString("nameZh")
                                } else {
                                    AppUtils.getResString(R.string.empty_exam_place)
                                }
                            )
                        )
                    }
                }
            }
            exams.sortBy { it.startTime }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}
