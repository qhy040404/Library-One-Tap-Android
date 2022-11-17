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
import com.qhy040404.libraryonetap.constant.GlobalManager.des
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.GradesTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.tools.GradesUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class GradesMinorActivity : SimplePageActivity() {
    private var currentVisible = true

    override fun initializeViewPref() {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }
    }

    override fun initializeView() {
        initView()
        GradesTempValues.clear()
        innerThread = Thread(PrepareData())
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            if (GradesTempValues.secondSemestersName.isEmpty() || GradesTempValues.secondCourseCredits.isEmpty()) {
                add(Card(
                    "无数据"
                ))
            } else {
                add(Card(
                    "加权均分: ${
                        GradesUtils.calculateWeightedAverage(
                            GradesTempValues.secondCourseGrade,
                            GradesTempValues.secondCourseCredits
                        )
                    }  平均绩点: ${
                        GradesUtils.calculateAverageGP(
                            GradesTempValues.secondCourseGP,
                            GradesTempValues.secondCourseCredits
                        )
                    }"
                ))
            }
            for (semester in GradesTempValues.secondSemestersName) {
                add(Category(semester))
                val count =
                    GradesTempValues.secondCourseCountList[GradesTempValues.secondSemestersName
                        .indexOf(semester)]
                GradesTempValues.secondEndCount += count
                for (i in GradesTempValues.secondStartCount until GradesTempValues.secondEndCount) {
                    val head = """
                            ${GradesTempValues.secondCourseName[i]} : ${GradesTempValues.secondCourseStudyType[i]}
                        """.trimIndent()
                    val desc = """
                            ${GradesTempValues.secondCourseCode[i]}
                            分数: ${GradesTempValues.secondCourseGrade[i]}
                            学分: ${GradesTempValues.secondCourseCredits[i]}
                            绩点: ${GradesTempValues.secondCourseGP[i]}
                        """.trimIndent()
                    add(ClickableItem(
                        head,
                        desc
                    ))
                }
                GradesTempValues.secondStartCount = count
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
                    "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
                }.getOrDefault(Constants.STRING_NULL)
                val ltExecution = runCatching {
                    ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
                }.getOrDefault(Constants.STRING_NULL)

                if (ltData.isNotEmpty()) {
                    val rawData = "$id$passwd$ltData"
                    val rsa = des.strEnc(rawData, "1", "2", "3")

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
                    Requests.get(URLManager.getEduGradeUrl(GradesTempValues.minorStuId))

                val gradesJsonObject = JSONObject(gradesData)
                val semesters = gradesJsonObject.optJSONArray("semesters")!!
                val grades = gradesJsonObject.optJSONObject("semesterId2studentGrades")!!
                for (i in 0 until semesters.length()) {
                    GradesTempValues.secondSemesters.add(
                        semesters.optJSONObject(i).optInt("id")
                    )
                    GradesTempValues.secondSemestersName.add(
                        semesters.optJSONObject(i).optString("name")
                    )
                }
                for (semester in GradesTempValues.secondSemesters) {
                    val currentSemesterData = grades.optJSONArray(semester.toString())!!
                    val courseCount = currentSemesterData.length()
                    GradesTempValues.secondCourseCountList.add(courseCount)
                    for (j in 0 until courseCount) {
                        val currentCourse = currentSemesterData.optJSONObject(j)!!
                        GradesTempValues.secondCourseName.add(
                            currentCourse.optJSONObject("course")!!.optString("nameZh")
                        )
                        GradesTempValues.secondCourseCode.add(
                            currentCourse.optJSONObject("course")!!.optString("code")
                        )
                        GradesTempValues.secondCourseCredits.add(
                            currentCourse.optJSONObject("course")!!.optDouble("credits")
                        )
                        GradesTempValues.secondCourseGrade.add(
                            currentCourse.optString("gaGrade")
                        )
                        GradesTempValues.secondCourseGP.add(
                            currentCourse.optDouble("gp")
                        )
                        GradesTempValues.secondCourseStudyType.add(
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
