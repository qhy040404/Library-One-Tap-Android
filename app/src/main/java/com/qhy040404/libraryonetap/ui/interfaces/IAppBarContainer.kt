package com.qhy040404.libraryonetap.ui.interfaces

import android.view.View

interface IAppBarContainer {
    fun scheduleAppbarLiftingStatus(isLifted: Boolean)
    fun setLiftOnScrollTargetView(targetView: View)
}
