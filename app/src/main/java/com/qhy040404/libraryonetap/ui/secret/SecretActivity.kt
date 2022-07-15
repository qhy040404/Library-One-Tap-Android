package com.qhy040404.libraryonetap.ui.secret

import android.net.Uri
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.databinding.ActivitySecretBinding
import com.qhy040404.libraryonetap.view.ModifiedVideoView

class SecretActivity : BaseActivity<ActivitySecretBinding>() {
    override fun init() = Thread(Play()).start()

    private inner class Play : Runnable {
        override fun run() {
            val videoView: ModifiedVideoView = findViewById(R.id.videoView)

            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.lol))
            videoView.start()
        }
    }
}