package com.qhy040404.libraryonetap.ui.tools

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.GradesTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.tools.GradesUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class GradesMinorActivity : SimplePageActivity() {
    private var currentVisible = true
    private val tempValues = GradesTempValues()

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
            if (tempValues.semestersName.isEmpty() || tempValues.courseCredits.isEmpty()) {
                add(Card(
                    "无数据"
                ))
            } else {
                add(Card(
                    "加权均分: ${
                        GradesUtils.calculateWeightedAverage(
                            tempValues.courseGrade,
                            tempValues.courseCredits
                        )
                    }  平均绩点: ${
                        GradesUtils.calculateAverageGP(
                            this@GradesMinorActivity,
                            tempValues.courseGP,
                            tempValues.courseGrade,
                            tempValues.courseCredits
                        )
                    }"
                ))
            }
            for (semester in tempValues.semestersName) {
                add(Category(semester))
                val count =
                    tempValues.courseCountList[tempValues.semestersName.indexOf(semester)]
                if (count == 0) {
                    add(Card(
                        "请先评教"
                    ))
                    continue
                }
                tempValues.endCount += count
                for (i in tempValues.startCount until tempValues.endCount) {
                    val head = """
                            ${tempValues.courseName[i]} : ${tempValues.courseStudyType[i]}
                        """.trimIndent()
                    val desc = """
                            ${tempValues.courseCode[i]}
                            分数: ${tempValues.courseGrade[i]}
                            学分: ${tempValues.courseCredits[i]}
                            绩点: ${tempValues.courseGP[i]}
                        """.trimIndent()
                    add(ClickableItem(
                        head,
                        desc
                    ))
                }
                tempValues.startCount = count
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
                    MaterialAlertDialogBuilder(this@GradesMinorActivity)
                        .setMessage(R.string.net_disconnected)
                        .setTitle(R.string.grade_major_title)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            finish()
                        }
                        .setCancelable(true)
                        .create().also {
                            if (this@GradesMinorActivity.currentVisible) {
                                it.show()
                            }
                        }
                }
                Looper.loop()
                return
            }

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
                            MaterialAlertDialogBuilder(this@GradesMinorActivity)
                                .setTitle(R.string.grade_major_title)
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
                                    if (this@GradesMinorActivity.currentVisible) {
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
                Thread.sleep(1000L)
                val gradesData =
                    Requests.get(URLManager.getEduGradeUrl(GlobalValues.minorStuId))

                val gradesJsonObject = JSONObject(gradesData)
                val semesters = gradesJsonObject.optJSONArray("semesters")!!
                val grades = gradesJsonObject.optJSONObject("semesterId2studentGrades")!!
                for (i in 0 until semesters.length()) {
                    tempValues.semesters.add(
                        semesters.optJSONObject(i).optInt("id")
                    )
                    tempValues.semestersName.add(
                        semesters.optJSONObject(i).optString("name")
                    )
                }
                for (semester in tempValues.semesters) {
                    val currentSemesterData = grades.optJSONArray(semester.toString())!!
                    val courseCount = currentSemesterData.length()
                    tempValues.courseCountList.add(courseCount)
                    for (j in 0 until courseCount) {
                        val currentCourse = currentSemesterData.optJSONObject(j)!!
                        tempValues.courseName.add(
                            currentCourse.optJSONObject("course")!!.optString("nameZh")
                        )
                        tempValues.courseCode.add(
                            currentCourse.optJSONObject("course")!!.optString("code")
                        )
                        tempValues.courseCredits.add(
                            currentCourse.optJSONObject("course")!!.optDouble("credits")
                        )
                        tempValues.courseGrade.add(
                            currentCourse.optString("gaGrade")
                        )
                        tempValues.courseGP.add(
                            currentCourse.optDouble("gp")
                        )
                        tempValues.courseStudyType.add(
                            currentCourse.optJSONObject("studyType")!!.optString("text")
                        )
                    }
                }
            }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}
