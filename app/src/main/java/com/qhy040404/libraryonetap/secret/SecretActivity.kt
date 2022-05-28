package com.qhy040404.libraryonetap.secret

import android.net.Uri
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.ui.StartUpActivity
import com.qhy040404.libraryonetap.view.ModifiedVideoView

class SecretActivity : StartUpActivity() {
    override fun init() = Thread(Play()).start()

    override fun getLayoutId(): Int = R.layout.activity_secret

    private inner class Play : Runnable {
        override fun run() {
            val videoView: ModifiedVideoView = findViewById(R.id.videoView)

            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.lol))
            videoView.start()
        }
    }
}