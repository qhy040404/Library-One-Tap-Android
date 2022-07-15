package com.qhy040404.libraryonetap.ui

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.databinding.SettingsActivityBinding
import com.qhy040404.libraryonetap.fragment.SettingsFragment

class SettingsActivity : BaseActivity<SettingsActivityBinding>() {
    override fun init() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }
}