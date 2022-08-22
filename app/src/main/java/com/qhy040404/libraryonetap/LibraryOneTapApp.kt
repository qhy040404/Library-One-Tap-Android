package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.DialogFragment
import coil.Coil
import coil.ImageLoader
import com.absinthe.libraries.utils.utils.Utility
import com.google.android.material.color.DynamicColors
import com.qhy040404.libraryonetap.app.AppIconFetcherFactory
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.status.AppStatusHelper
import com.qhy040404.libraryonetap.utils.status.OnAppStatusListener
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.material.app.DayNightDelegate
import rikka.material.app.LocaleDelegate
import java.util.LinkedList
import kotlin.system.exitProcess

class LibraryOneTapApp : Application() {
    private val activityList: MutableList<Activity> = LinkedList()
    private val fragmentList: MutableList<DialogFragment> = LinkedList()
    private var delayTerminateJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.appChannel = BuildConfig.CHANNEL

        CrashReport.initCrashReport(
            applicationContext,
            BuildConfig.BUGLY_APPID,
            BuildConfig.DEBUG,
            strategy
        )
        app = this

        LocaleDelegate.defaultLocale = GlobalValues.locale
        DayNightDelegate.setApplicationContext(this)
        DayNightDelegate.setDefaultNightMode(AppUtils.getNightMode(GlobalValues.darkMode))
        DynamicColors.applyToActivitiesIfAvailable(this)

        Utility.init(this)
        GlobalValues.initBasic()

        Coil.setImageLoader {
            ImageLoader(this).newBuilder()
                .components {
                    add(AppIconFetcherFactory(this@LibraryOneTapApp))
                }
                .build()
        }
    }

    @DelicateCoroutinesApi
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppStatusHelper.register(this, object : OnAppStatusListener {
            override fun onFront() {
                Log.w("AppStatus", "Front")
                delayTerminateJob?.cancel()
            }

            override fun onBack() {
                Log.w("AppStatus", "Back")
                delayTerminateJob = GlobalScope.launch(Dispatchers.IO) {
                    delay(30000L)
                    withContext(Dispatchers.Main) {
                        this@LibraryOneTapApp.exit()
                        exitProcess(0)
                    }
                }.also {
                    it.start()
                }
            }
        })
    }

    fun addActivity(activity: Activity) {
        if (!activityList.contains(activity)) activityList.add(activity)
    }

    fun removeActivity(activity: Activity) {
        if (activityList.contains(activity)) activityList.remove(activity)
    }

    fun addFragment(fragment: DialogFragment) {
        if (!fragmentList.contains(fragment)) fragmentList.add(fragment)
    }

    fun dismissFragment() {
        for (fragment in fragmentList) {
            fragment.dismiss()
        }
        fragmentList.clear()
    }

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
