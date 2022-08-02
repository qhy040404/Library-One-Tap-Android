package com.qhy040404.libraryonetap.ui.tools

import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity
import com.qhy040404.libraryonetap.utils.AppUtils

class ExamsActivity : SimplePageActivity() {
    override fun initializeViewPref() {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }
    }

    override fun initializeView() {
        TODO("Not yet implemented")
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        TODO("Not yet implemented")
    }
}