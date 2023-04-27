package com.qhy040404.libraryonetap.ui.tools

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.datetime.Datetime
import com.qhy040404.datetime.Datetime.Companion.toDatetime
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.tools.Exam
import com.qhy040404.libraryonetap.recyclerview.SimplePageActivity
import com.qhy040404.libraryonetap.recyclerview.simplepage.Card
import com.qhy040404.libraryonetap.recyclerview.simplepage.Category
import com.qhy040404.libraryonetap.recyclerview.simplepage.Clickable
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.delay
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
        LibraryOneTapApp.instance?.addActivity(this)

        findViewById<ProgressBar>(R.id.simple_progressbar).visibility = View.VISIBLE

        if (!GlobalValues.md3) {
            toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        super.onItemsCreated(items)
        items.apply {
            if (exams.isEmpty()) {
                add(
                    Card(
                        R.string.ex_empty.getString()
                    )
                )
            }

            exams.filter { it.startTime > now }.forEach { pending ->
                val name = pending.name
                val desc = String.format(
                    R.string.ex_template.getString(),
                    pending.time,
                    pending.room
                )
                add(
                    Clickable(
                        name,
                        desc
                    )
                )
            }

            exams.filter { it.startTime < now }.let { finished ->
                if (finished.isNotEmpty()) {
                    add(Category(R.string.ex_finished_list.getString()))
                    finished.forEach { finish ->
                        val name = finish.name
                        val desc = String.format(
                            R.string.ex_template.getString(),
                            finish.time,
                            finish.room
                        )
                        add(
                            Clickable(
                                name,
                                desc
                            )
                        )
                    }
                }
            }
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

    override suspend fun setData() {
        if (!AppUtils.hasNetwork()) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@ExamsActivity)
                    .setMessage(R.string.glb_net_disconnected)
                    .setTitle(R.string.exams_title)
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(true)
                    .create().also {
                        if (this@ExamsActivity.currentVisible) {
                            it.show()
                        }
                    }
            }
            return
        }

        var majorStuId = 0
        val loginSuccess = Requests.initEdu()

        if (!loginSuccess.first) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@ExamsActivity)
                    .setTitle(R.string.exams_title)
                    .setMessage(
                        when (GlobalValues.netPrompt) {
                            Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected
                            Constants.NET_ERROR -> R.string.glb_net_error
                            Constants.NET_TIMEOUT -> R.string.glb_net_timeout
                            else -> R.string.glb_fail_to_login_three_times
                        }
                    )
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create().also {
                        if (this@ExamsActivity.currentVisible) {
                            it.show()
                        }
                    }
            }
        } else {
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

            if (!loginSuccess.second) {
                delay(2000L)
            }
            var examsMinorData: String? = null

            val examsMajorData =
                Requests.get(URLManager.getEduExamsUrl(GlobalValues.majorStuId))
                    .substringBetween("var studentExamInfoVms = ", "];") + "]"
            if (GlobalValues.minorStuId > 0) {
                delay(2000L)
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
                        "${datetime[0]} ${datetime[1].substringBefore("~")}".toDatetime(),
                        "${datetime[0]} ${datetime[1].substringAfter("~")}".toDatetime(),
                        if (!course.isNull("examPlace")) {
                            course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                .optString("nameZh")
                        } else {
                            R.string.ex_empty_room.getString()
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
                                .optString("nameZh") + R.string.ex_minor.getString(),
                            time,
                            "${datetime[0]} ${datetime[1].substringBefore("~")}".toDatetime(),
                            "${datetime[0]} ${datetime[1].substringAfter("~")}".toDatetime(),
                            if (!course.isNull("examPlace")) {
                                course.optJSONObject("examPlace")!!.optJSONObject("room")!!
                                    .optString("nameZh")
                            } else {
                                R.string.ex_empty_room.getString()
                            }
                        )
                    )
                }
            }
            exams.sortBy { it.startTime }
            syncRecycleView()
        }
    }
}
