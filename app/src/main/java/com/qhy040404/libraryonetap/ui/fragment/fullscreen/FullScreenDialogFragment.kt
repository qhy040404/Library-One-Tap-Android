package com.qhy040404.libraryonetap.ui.fragment.fullscreen

import android.view.Gravity
import android.view.ViewGroup
import com.qhy040404.libraryonetap.base.BaseBindingDialogFragment
import com.qhy040404.libraryonetap.databinding.FragmentFullscreenDialogBinding

class FullScreenDialogFragment(private val message: String) :
    BaseBindingDialogFragment<FragmentFullscreenDialogBinding>() {
    override fun onResume() {
        super.onResume()
        dialog?.window?.run {
            attributes = attributes?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.BOTTOM
            }
        }
    }

    override fun initOnce() {
        binding.pmDetail.text = message
    }

    override fun isCancelable(): Boolean = false

    override fun onBackPressed(): Boolean = true
}