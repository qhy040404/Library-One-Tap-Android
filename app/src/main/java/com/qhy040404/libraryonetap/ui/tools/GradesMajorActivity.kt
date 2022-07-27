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

class GradesMajorActivity : SimplePageActivity() {
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
        GradesTempValues.clear()
        Thread(PrepareData()).start()
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            if (GlobalValues.minorDetected) {
                add(ClickableItem(
                    "检测到辅修/双学位，已在工具箱添加入口",
                    ""
                ))
            }
            for (semester in GradesTempValues.semestersName) {
                add(Category(semester))
                val count =
                    GradesTempValues.courseCountList[GradesTempValues.semestersName.indexOf(semester)]
                GradesTempValues.endCount += count
                for (i in GradesTempValues.startCount until GradesTempValues.endCount) {
                    val head = """
                            ${GradesTempValues.courseName[i]} : ${GradesTempValues.courseStudyType[i]}
                        """.trimIndent()
                    val desc = """
                            ${GradesTempValues.courseCode[i]}
                            分数: ${GradesTempValues.courseGrade[i]}
                            学分: ${GradesTempValues.courseCredits[i]}
                            绩点: ${GradesTempValues.courseGP[i]}
                        """.trimIndent()
                    add(ClickableItem(
                        head,
                        desc
                    ))
                }
                GradesTempValues.startCount = count

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
                        MaterialAlertDialogBuilder(this@GradesMajorActivity)
                            .setTitle(R.string.grade_major_title)
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
                val initUrl = Requests.get(URLManager.EDU_GRADE_INIT_URL, true)
                val initData = Requests.get(URLManager.EDU_GRADE_INIT_URL)
                val stuId = if (initUrl.contains("semester-index")) {
                    initUrl.split("/").last().toInt()
                } else {
                    val initList =
                        initData.split("onclick=\"myFunction(this)\" value=\"")
                    if (initList.size == 3) {
                        GradesTempValues.secondStuId = initList[2].split("\"")[0].toInt()
                        if (!GlobalValues.minorVisible) {
                            GlobalValues.minorDetected = true
                        }
                    }
                    initList[1].split("\"")[0].toInt()
                }

                Thread.sleep(5000L)
                val gradesData = Requests.get(URLManager.getEduGradeUrl(stuId))

                val gradesJsonObject = JSONObject(gradesData)
                val semesters = gradesJsonObject.optJSONArray("semesters")!!
                val grades = gradesJsonObject.optJSONObject("semesterId2studentGrades")!!
                for (i in 0 until semesters.length()) {
                    GradesTempValues.semesters.add(
                        semesters.optJSONObject(i).optInt("id")
                    )
                    GradesTempValues.semestersName.add(
                        semesters.optJSONObject(i).optString("name")
                    )
                }
                for (semester in GradesTempValues.semesters) {
                    val currentSemesterData = grades.optJSONArray(semester.toString())!!
                    val courseCount = currentSemesterData.length()
                    GradesTempValues.courseCountList.add(courseCount)
                    for (j in 0 until courseCount) {
                        val currentCourse = currentSemesterData.optJSONObject(j)!!
                        GradesTempValues.courseName.add(
                            currentCourse.optJSONObject("course")!!.optString("nameZh")
                        )
                        GradesTempValues.courseCode.add(
                            currentCourse.optJSONObject("course")!!.optString("code")
                        )
                        GradesTempValues.courseCredits.add(
                            currentCourse.optJSONObject("course")!!.optDouble("credits")
                        )
                        GradesTempValues.courseGrade.add(
                            currentCourse.optString("gaGrade")
                        )
                        GradesTempValues.courseGP.add(
                            currentCourse.optDouble("gp")
                        )
                        GradesTempValues.courseStudyType.add(
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