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
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.temp.LessonsTempValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.replaceAll
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class LessonsActivity : SimplePageActivity() {
    private var currentVisible = true
    private val tempValues = LessonsTempValues()

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
            add(Category(
                tempValues.semesterName
            ))
            add(Card(
                "共 ${tempValues.lessonId.count()} 门课\n总学分: ${tempValues.lessonCredit.sum()}"
            ))
            for (i in tempValues.lessonId.indices) {
                val head = "${tempValues.lessonType[i]}  ${tempValues.lessonName[i]}"
                val desc =
                    "教师: ${tempValues.lessonTeacher[i]}\n教学班: ${tempValues.lessonCode[i]}\n学分: ${tempValues.lessonCredit[i]}\n类型: ${tempValues.lessonCompulsory[i]}\n考核方式: ${tempValues.lessonExamMode[i]}\n开课学院: ${tempValues.lessonOpenDepart[i]}"
                add(ClickableItem(
                    head,
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
        @Suppress("SpellCheckingInspection")
        override fun run() {
            Looper.prepare()
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )

            if (!AppUtils.hasNetwork()) {
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@LessonsActivity)
                        .setMessage(R.string.net_disconnected)
                        .setTitle(R.string.lessons_title)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            finish()
                        }
                        .setCancelable(true)
                        .create().also {
                            if (this@LessonsActivity.currentVisible) {
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
                            MaterialAlertDialogBuilder(this@LessonsActivity)
                                .setTitle(R.string.lessons_title)
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
                                    if (this@LessonsActivity.currentVisible) {
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
                val source = Requests.get(URLManager.EDU_COURSE_TABLE_URL)
                val semesterId = source.substringAfter("selected=\"selected\"")
                    .substringBefore(">")
                    .trim()
                    .split("\"")[1].toInt()
                tempValues.semesterName = source.substringAfter("selected=\"selected\"")
                    .substringBefore("</option>")
                    .trim()
                    .split(">").last()
                val courseData = Requests.get(URLManager.getEduCourseUrl(semesterId))

                val courseJsonObject = JSONObject(courseData)

                val cultivateType = courseJsonObject.optJSONObject("lesson2CultivateTypeMap")!!
                val lessons = courseJsonObject.optJSONArray("lessons")!!
                for (i in 0 until lessons.length()) {
                    val lesson = lessons.optJSONObject(i)
                    val lessonId = lesson.optInt("id")
                    tempValues.lessonId.add(lessonId)
                    tempValues.lessonCode.add(
                        lesson.optString("code")
                    )
                    tempValues.lessonCompulsory.add(
                        lesson.optString("compulsorysStr")
                    )
                    tempValues.lessonName.add(
                        lesson.optJSONObject("course")!!.optString("nameZh")
                    )
                    tempValues.lessonCredit.add(
                        lesson.optJSONObject("course")!!.optDouble("credits")
                    )
                    tempValues.lessonExamMode.add(
                        lesson.optJSONObject("examMode")!!.optString("nameZh")
                    )
                    tempValues.lessonOpenDepart.add(
                        lesson.optJSONObject("openDepartment")!!.optString("nameZh")
                    )
                    tempValues.lessonTeacher.add(
                        lesson.optString("teacherAssignmentStr").replaceAll(",", "\n\t\t\t\t\t")
                    )
                    tempValues.lessonType.add(
                        cultivateType.optJSONObject(lessonId.toString())!!.optString("nameZh")
                    )
                }
            }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}