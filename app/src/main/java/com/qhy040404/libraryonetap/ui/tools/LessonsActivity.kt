package com.qhy040404.libraryonetap.ui.tools

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.tools.Lesson
import com.qhy040404.libraryonetap.recyclerview.simplepage.Card
import com.qhy040404.libraryonetap.recyclerview.simplepage.Category
import com.qhy040404.libraryonetap.recyclerview.simplepage.Clickable
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.getStringAndFormat
import com.qhy040404.libraryonetap.utils.extensions.substringBetween
import com.qhy040404.libraryonetap.utils.web.Requests
import org.json.JSONObject

class LessonsActivity : BaseEduActivity() {
  private var courseTableAvailable = true
  private var semester: String = Constants.STRING_NULL
  private val lessons = mutableListOf<Lesson>()

  override fun initializeViewPref() {
    if (!GlobalValues.md3) {
      setTheme(AppUtils.getThemeID(GlobalValues.theme))
    }
  }

  override fun initializeView() {
    LibraryOneTapApp.instance?.addActivity(this)

    findViewById<ProgressBar>(R.id.simple_progressbar).isVisible = true

    if (!GlobalValues.md3) {
      toolbar.setTitleTextColor(getColor(R.color.white))
      supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
    }
  }

  override suspend fun setData() {
    if (!AppUtils.hasNetwork()) {
      runOnUiThread {
        MaterialAlertDialogBuilder(this@LessonsActivity)
          .setMessage(R.string.glb_net_disconnected)
          .setTitle(R.string.lessons_title)
          .setPositiveButton(R.string.glb_ok) { _, _ ->
            finish()
          }
          .setCancelable(true)
          .create().also {
            if (this@LessonsActivity.currentVisible) {
              it.show()
            }
          }
      }
      return
    }

    if (!Requests.initEdu()) {
      runOnUiThread {
        showInitFailedAlertDialog(R.string.lessons_title)
      }
    } else {
      val source = Requests.get(URLManager.EDU_COURSE_TABLE_URL)
      runCatching {
        JSONObject(source).let {
          if (it.optString("exception") == "org.apache.shiro.authz.UnauthorizedException") {
            courseTableAvailable = false
          }
        }
      }.onSuccess {
        return
      }
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
              "COMPULSORY" -> R.string.ls_compulsory.getString()
              "ELECTIVE" -> R.string.ls_elective.getString()
              else -> throw IllegalStateException("Illegal compulsory state")
            },
            lesson.optJSONObject("course")!!.optString("nameZh"),
            lesson.optJSONObject("course")!!.optDouble("credits"),
            lesson.optJSONArray("teacherAssignmentList")!!.let {
              buildString {
                for (t in 0 until it.length()) {
                  append(
                    it.optJSONObject(t)!!.optJSONObject("person")!!
                      .optString("nameZh")
                  )
                  append(",")
                }
              }.trim(',')
            },
            cultivateType.optJSONObject(lessonId.toString())!!.optString("nameZh")
          )
        )
      }
      syncRecycleView()
    }
  }

  override fun onItemsCreated(items: MutableList<Any>) {
    super.onItemsCreated(items)
    items.apply {
      if (!courseTableAvailable) {
        add(Card(R.string.ls_unavailable.getString()))
        return
      }
      add(
        Category(
          semester
        )
      )
      add(
        Card(
          R.string.ls_stat.getStringAndFormat(
            lessons.count(),
            lessons.sumOf { it.credit }
          )
        )
      )
      lessons.forEach {
        val head = "${it.type}  ${it.name}"
        val desc = R.string.ls_template.getStringAndFormat(
          it.teacher,
          it.code,
          it.credit,
          it.compulsory
        )
        add(
          Clickable(
            head,
            desc
          )
        )
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
}
