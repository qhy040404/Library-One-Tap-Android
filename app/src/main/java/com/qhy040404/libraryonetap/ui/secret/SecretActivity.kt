package com.qhy040404.libraryonetap.ui.secret

import android.net.Uri
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.databinding.ActivitySecretBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecretActivity : BaseActivity<ActivitySecretBinding>() {
    override fun init() {
        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.secret_title)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            play()
        }.also {
            it.start()
        }
    }

    private fun play() {
        val videoView = binding.videoView

        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.lol))
        videoView.start()
    }
}
