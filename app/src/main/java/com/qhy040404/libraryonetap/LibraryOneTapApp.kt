package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import com.dylanc.loadingstateview.LoadingStateDelegate
import com.dylanc.loadingstateview.LoadingStateView
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.utils.LoadingViewDelegate
import com.tencent.bugly.crashreport.CrashReport
import java.util.*

class LibraryOneTapApp : Application() {
    private val activityList: MutableList<Activity> = LinkedList()

    override fun onCreate() {
        super.onCreate()

        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.appChannel = BuildConfig.CHANNEL

        CrashReport.initCrashReport(
            applicationContext,
            Constants.BUGLY_APPID,
            BuildConfig.DEBUG,
            strategy
        )
        app = this
    }

    fun addActivity(activity: Activity) = activityList.add(activity)

    fun exit() {
        for (activity in activityList) {
            activity.finish()
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