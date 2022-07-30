package com.qhy040404.libraryonetap.utils.status

import android.app.Activity
import android.app.Application
import android.os.Bundle

object AppStatusHelper {
    private var mOnAppStatusListener: OnAppStatusListener? = null

    fun register(app: Application, listener: OnAppStatusListener) {
        mOnAppStatusListener = listener
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun unregister(app: Application) {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private val activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
            private var activityStartCount = 0
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
            override fun onActivityStarted(p0: Activity) {
                activityStartCount++
                if (activityStartCount == 1) {
                    mOnAppStatusListener?.onFront()
                }
            }

            override fun onActivityResumed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {
                activityStartCount--
                if (activityStartCount == 0) {
                    mOnAppStatusListener?.onBack()
                }
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        }
}