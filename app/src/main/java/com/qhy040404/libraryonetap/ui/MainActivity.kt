package com.qhy040404.libraryonetap.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.ui.secret.SecretActivity
import com.qhy040404.libraryonetap.ui.template.StartUpActivity
import com.qhy040404.libraryonetap.ui.tools.ToolsInitActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import kotlin.system.exitProcess

class MainActivity : StartUpActivity() {
    override fun init() {
        handleIntentFromShortcuts(intent)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initView()
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentFromShortcuts(intent)
    }

    private fun initView() = Thread(ShowVersion()).start()

    private fun handleIntentFromShortcuts(intent: Intent) {
        when (intent.action) {
            Constants.SHORTCUT_DETAIL -> startActivity(Intent(this, DetailActivity::class.java))
            Constants.SHORTCUT_TOOLS -> startActivity(Intent(this, ToolsInitActivity::class.java))
            Constants.SHORTCUT_VCARD -> startActivity(Intent(this, VCardActivity::class.java))
        }
    }

    private inner class ShowVersion : Runnable {
        override fun run() {
            Looper.prepare()

            var counter = 0
            var dogCounter = 0
            var b1 = true
            var b2 = true
            var b3 = true
            var b4 = true
            var b5 = true

            val versionView: TextView = findViewById(R.id.textView5)
            val dog: ImageView = findViewById(R.id.imageView4)
            val exit: Button = findViewById(R.id.button3)

            versionView.post { versionView.text = GlobalValues.version }

            versionView.setOnClickListener {
                counter += 1
                if (counter in 1..10 && b1) {
                    b1 = false
                    Toast.makeText(this@MainActivity, "没做检查更新，别点啦~", Toast.LENGTH_LONG).show()
                } else if (counter in 11..20 && b2) {
                    b2 = false
                    dog.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(dog, "alpha", 0F, 0.1F).setDuration(500).start()
                    Toast.makeText(this@MainActivity, "真的没做，骗你干嘛，别点啦~", Toast.LENGTH_LONG).show()
                } else if (counter in 20..30 && b3) {
                    b3 = false
                    ObjectAnimator.ofFloat(dog, "alpha", 0.1F, 0.3F).setDuration(1000).start()
                    Toast.makeText(this@MainActivity, "没有东西了啊，别点了啊喂", Toast.LENGTH_LONG).show()
                } else if (counter in 31..40 && b4) {
                    b4 = false
                    ObjectAnimator.ofFloat(dog, "alpha", 0.3F, 0.5F).setDuration(1000).start()
                    Toast.makeText(this@MainActivity, "你赢了，但是真的啥也没有哈哈哈哈哈哈", Toast.LENGTH_LONG)
                        .show()
                } else if (counter > 40 && b5) {
                    b5 = false
                    ObjectAnimator.ofFloat(dog, "alpha", 0.5F, 1F).setDuration(2500).start()
                }
            }

            dog.setOnClickListener {
                if (counter >= 40) {
                    dogCounter += 1
                    if (dogCounter >= 5) {
                        startActivity(Intent(this@MainActivity, SecretActivity::class.java))
                    }
                }
            }

            exit.setOnLongClickListener {
                LibraryOneTapApp.instance?.exit()
                exitProcess(0)
            }
            Looper.loop()
        }
    }

    fun buttonOpenDetail(view: View) = startActivity(Intent(this, DetailActivity::class.java))

    fun buttonOpenSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

    fun buttonYanxiujian(view: View) = startActivity(Intent(this, YanxiujianActivity::class.java))

    fun buttonTools(view: View) = startActivity(Intent(this, ToolsInitActivity::class.java))

    fun buttonExit(view: View) = LibraryOneTapApp.instance?.exit()

}