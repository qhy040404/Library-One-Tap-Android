package com.qhy040404.libraryonetap

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.tools.ToolsInitActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        Thread(ShowVersion()).start()
    }

    private inner class ShowVersion : Runnable {
        override fun run() {
            Looper.prepare()

            var counter = 0
            var b1 = true
            var b2 = true
            var b3 = true
            var b4 = true

            val versionView: TextView = findViewById(R.id.textView5)

            val packManager = packageManager
            val packInfo = packManager.getPackageInfo(packageName, 0)
            val versionCode = packInfo.longVersionCode
            val versionName = packInfo.versionName

            val version = getString(R.string.app_name) + " " + versionName + "($versionCode)"

            versionView.text = version

            versionView.setOnClickListener {
                counter += 1
                if (counter in 1..10 && b1) {
                    b1 = false
                    val makeText =
                        Toast.makeText(this@MainActivity, "没做检查更新，别点啦~", Toast.LENGTH_LONG)
                    makeText.show()
                } else if (counter in 11..20 && b2) {
                    b2 = false
                    val makeText =
                        Toast.makeText(this@MainActivity, "真的没做，骗你干嘛，别点啦~", Toast.LENGTH_LONG)
                    makeText.show()
                } else if (counter in 20..30 && b3) {
                    b3 = false
                    val makeText =
                        Toast.makeText(this@MainActivity, "没有东西了啊，别点了啊喂", Toast.LENGTH_LONG)
                    makeText.show()
                } else if (counter > 30 && b4) {
                    b4 = false
                    val makeText =
                        Toast.makeText(this@MainActivity, "你赢了，但是真的啥也没有哈哈哈哈哈哈", Toast.LENGTH_LONG)
                    makeText.show()
                }
            }
            Looper.loop()
        }
    }

    fun buttonOpenDetail(view: View) {
        val intent = Intent(this, DetailActivity::class.java)
        startActivity(intent)
    }

    fun buttonOpenSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun buttonYanxiujian(view: View) {
        val intent = Intent(this, YanxiujianActivity::class.java)
        startActivity(intent)
    }

    fun buttonTools(view: View) {
        val intent = Intent(this, ToolsInitActivity::class.java)
        startActivity(intent)
    }

    fun buttonExit(view: View) {
        exitProcess(0)
    }

}