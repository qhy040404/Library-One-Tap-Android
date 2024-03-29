package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import coil.Coil
import coil.ImageLoader
import com.absinthe.libraries.utils.extensions.dp
import com.absinthe.libraries.utils.utils.Utility
import com.google.android.material.color.DynamicColors
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.status.AppStatusHelper
import com.qhy040404.libraryonetap.utils.status.OnAppStatusListener
import com.tencent.bugly.crashreport.CrashReport
import jonathanfinerty.once.Once
import kotlin.system.exitProcess
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import rikka.material.app.LocaleDelegate

class LibraryOneTapApp : Application() {
  private val activityList = mutableListOf<Activity>()
  private val fragmentList = mutableListOf<DialogFragment>()
  private var delayTerminateJob: Job? = null

  override fun onCreate() {
    super.onCreate()

    val strategy = CrashReport.UserStrategy(applicationContext).apply {
      appChannel = BuildConfig.CHANNEL
    }

    CrashReport.initCrashReport(
      applicationContext,
      BuildConfig.BUGLY_APPID,
      BuildConfig.DEBUG,
      strategy
    )
    app = this

    LocaleDelegate.defaultLocale = GlobalValues.locale
    AppCompatDelegate.setDefaultNightMode(AppUtils.getNightMode(GlobalValues.darkMode))
    DynamicColors.applyToActivitiesIfAvailable(this)

    Utility.init(this)
    Once.initialise(this)
    GlobalValues.initBasic()

    Coil.setImageLoader {
      ImageLoader.Builder(this)
        .components {
          add(AppIconKeyer())
          add(AppIconFetcher.Factory(40.dp, false, this@LibraryOneTapApp))
        }
        .build()
    }
  }

  @DelicateCoroutinesApi
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    AppStatusHelper.register(
      this,
      object : OnAppStatusListener {
        override fun onFront() {
          delayTerminateJob?.cancel()
        }

        override fun onBack() {
          delayTerminateJob = GlobalScope.launch(Dispatchers.IO) {
            delay(30000L)
            withContext(Dispatchers.Main) {
              this@LibraryOneTapApp.exit()
              exitProcess(0)
            }
          }
        }
      }
    )
    Handler(Looper.getMainLooper()).post {
      while (true) {
        Looper.loop()
      }
    }
  }

  fun addActivity(activity: Activity) {
    if (!activityList.contains(activity)) {
      activityList.add(activity)
    }
  }

  fun removeActivity(activity: Activity) {
    if (activityList.contains(activity)) {
      activityList.remove(activity)
    }
  }

  fun addFragment(fragment: DialogFragment) {
    if (!fragmentList.contains(fragment)) {
      fragmentList.add(fragment)
    }
  }

  fun dismissFragment() {
    fragmentList.forEach {
      it.dismiss()
    }
    fragmentList.clear()
  }

  fun exit() {
    activityList.forEach {
      it.finish()
    }
    activityList.clear()
  }

  companion object {
    lateinit var app: Application

    var instance: LibraryOneTapApp? = null
      get() {
        if (null == field) {
          field = LibraryOneTapApp()
        }
        return field
      }
      private set
  }
}
