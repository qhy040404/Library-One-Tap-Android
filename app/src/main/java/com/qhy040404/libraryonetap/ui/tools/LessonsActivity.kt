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
import com.qhy040404.libraryonetap.data.tools.Lesson
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.recycleview.simplepage.Card
import com.qhy040404.libraryonetap.recycleview.simplepage.Category
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class LessonsActivity : SimplePageActivity() {
    private var currentVisible = true
    private var semester: String = Constants.STRING_NULL
    private val lessons = mutableListOf<Lesson>()

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
                semester
            ))
            add(Card(
                "共 ${lessons.count()} 门课\n总学分: ${lessons.sumOf { it.credit }}"
            ))
            lessons.forEach {
                val head = "${it.type}  ${it.name}"
                val desc =
                    "教师: ${it.teacher}\n教学班: ${it.code}\n学分: ${it.credit}\n类型: ${it.compulsory}"
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
                val semesterId = source.substringBetween("selected=\"selected\"", ">")
                    .trim()
                    .substringAfter("=").trim('"').toInt()
                semester = source
                    .substringBetween("selected=\"selected\"", "</option>")
                    .trim()
                    .substringAfter(">")
                val courseData = Requests.get(URLManager.getEduCourseUrl(semesterId))

                val courseJsonObject = JSONObject(courseData)

                val cultivateType = courseJsonObject.optJSONObject("lesson2CultivateTypeMap")!!
                val lessonArray = courseJsonObject.optJSONArray("lessons")!!
                for (i in 0 until lessonArray.length()) {
                    val lesson = lessonArray.optJSONObject(i)
                    val lessonId = lesson.optInt("id")
                    lessons.add(
                        Lesson(
                            lessonId,
                            lesson.optString("code"),
                            when (lesson.optJSONArray("compulsorys")!!.optString(0)) {
                                "COMPULSORY" -> "必修"
                                "ELECTIVE" -> "选修"
                                else -> throw IllegalStateException("Illegal compulsory state")
                            },
                            lesson.optJSONObject("course")!!.optString("nameZh"),
                            lesson.optJSONObject("course")!!.optDouble("credits"),
                            lesson.optJSONArray("teacherAssignmentList")!!.let {
                                buildString {
                                    for (t in 0 until it.length()) {
                                        append(it.optJSONObject(t)!!.optJSONObject("person")!!
                                            .optString("nameZh"))
                                        append(",")
                                    }
                                }.trim(',')
                            },
                            cultivateType.optJSONObject(lessonId.toString())!!.optString("nameZh")
                        )
                    )
                }
            }
            syncRecycleView()
            findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.INVISIBLE
        }
    }
}
