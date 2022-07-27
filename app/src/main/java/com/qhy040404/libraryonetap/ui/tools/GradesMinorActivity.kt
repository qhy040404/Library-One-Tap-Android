package com.qhy040404.libraryonetap.ui.tools

import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.GradesTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class GradesMinorActivity : SimplePageActivity() {
    override fun initialization() {
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

        initView()
        Thread(PrepareData()).start()
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            for (semester in GradesTempValues.secondSemestersName) {
                add(Category(semester))
                val count =
                    GradesTempValues.secondCourseCountList[GradesTempValues.secondSemestersName.indexOf(
                        semester)]
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
    }

    private inner class PrepareData : Runnable {
        override fun run() {
            Looper.prepare()
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

            val id = GlobalValues.id
            val passwd = GlobalValues.passwd

            var loginSuccess = false
            var timer = 0
            while (!loginSuccess && AppUtils.checkData(id, passwd)) {
                val ltResponse: String = Requests.get(URLManager.EDU_LOGIN_SSO_URL)
                val ltData: String = try {
                    "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
                } catch (_: Exception) {
                    ""
                }
                val ltExecution: String = try {
                    ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
                } catch (_: Exception) {
                    ""
                }

                if (ltData != "") {
                    val rawData = "$id$passwd$ltData"
                    val rsa: String = des.strEnc(rawData, "1", "2", "3")

                    Requests.post(
                        URLManager.EDU_LOGIN_SSO_URL,
                        Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
                        GlobalValues.ctSso
                    )
                }

                val session: String = Requests.get(URLManager.EDU_CHECK_URL)
                if (session.contains("person")) {
                    loginSuccess = true
                } else {
                    timer++
                    if (timer >= 3) {
                        MaterialAlertDialogBuilder(this@GradesMinorActivity)
                            .setTitle(R.string.grade_minor_title)
                            .setMessage(R.string.failTimes)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                finish()
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                        Looper.loop()
                        break
                    }
                }
            }
            if (loginSuccess) {
                val gradesData =
                    Requests.get(URLManager.getEduGradeUrl(GradesTempValues.secondStuId))

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
            Looper.loop()
        }
    }
}