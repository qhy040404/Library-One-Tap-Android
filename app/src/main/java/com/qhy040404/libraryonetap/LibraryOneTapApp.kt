package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.absinthe.libraries.utils.utils.Utility
import com.google.android.material.color.DynamicColors
import com.qhy040404.libraryonetap.app.AppIconFetcherFactory
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.tencent.bugly.crashreport.CrashReport
import rikka.material.app.DayNightDelegate
import rikka.material.app.LocaleDelegate
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

        LocaleDelegate.defaultLocale = AppUtils.locale
        DayNightDelegate.setApplicationContext(this)
        DayNightDelegate.setDefaultNightMode(AppUtils.getNightMode(GlobalValues.darkMode))
        DynamicColors.applyToActivitiesIfAvailable(this)

        Utility.init(this)

        Coil.setImageLoader {
            ImageLoader(this).newBuilder()
                .components {
                    add(AppIconFetcherFactory(this@LibraryOneTapApp))
                }
                .build()
        }
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