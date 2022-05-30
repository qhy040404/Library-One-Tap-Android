package com.qhy040404.libraryonetap.ui.tools

import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.fragment.tools.ToolsInitNewFragment
import com.qhy040404.libraryonetap.ui.templates.StartUpActivity

class ToolsInitNewActivity :StartUpActivity(){
    override fun init() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.tools_list_screen, ToolsInitNewFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun getLayoutId(): Int = R.layout.activity_tools_init_new
}