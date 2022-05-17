package com.qhy040404.libraryonetap

import android.app.Activity
import android.app.Application
import java.util.*

class LibraryOneTapApp : Application() {
    private val activityList: MutableList<Activity> = LinkedList()

    override fun onCreate() {
        super.onCreate()
        app = this
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