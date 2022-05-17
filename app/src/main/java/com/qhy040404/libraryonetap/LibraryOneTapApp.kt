package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import android.os.Environment
import com.qhy040404.libraryonetap.activity.MainActivity
import com.qhy040404.libraryonetap.constant.Constants
import com.tencent.bugly.Bugly
import com.tencent.bugly.BuglyStrategy
import com.tencent.bugly.beta.Beta
import java.util.*

class LibraryOneTapApp : Application() {
    private val activityList: MutableList<Activity> = LinkedList()

    override fun onCreate() {
        super.onCreate()

        setBeta()

        Bugly.init(applicationContext, Constants.BUGLY_APPID,true)
        app = this
    }

    private fun setBeta() {
        Beta.autoInit=true
        Beta.autoCheckUpgrade=true
        Beta.upgradeCheckPeriod = 0*1000
        Beta.initDelay = 1*1000
        Beta.largeIconId=R.mipmap.launcher_lol
        Beta.smallIconId=R.mipmap.launcher_lol
        Beta.defaultBannerId=R.mipmap.launcher_lol
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Beta.showInterruptedStrategy=true
        Beta.canShowUpgradeActs.add(MainActivity::class.java)
        Beta.enableHotfix=false
    }

    fun addActivity(activity: Activity) {
        activityList.add(activity)
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