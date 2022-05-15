package com.qhy040404.libraryonetap.secret

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.views.ModifiedVideoView

class SecretActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret)

        Thread(Play()).start()
    }

    private inner class Play : Runnable {
        override fun run() {
            val videoView: ModifiedVideoView = findViewById(R.id.videoView)

            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.lol))
            videoView.start()
        }
    }
}