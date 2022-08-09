package com.qhy040404.libraryonetap.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.qhy040404.libraryonetap.ui.interfaces.IBindingFragment
import com.qhy040404.libraryonetap.utils.extensions.CompatExtensions.inflateBinding

abstract class BaseBindingDialogFragment<VB : ViewDataBinding> : BaseDialogFragment(),
    IBindingFragment<VB> {
    override lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding(layoutInflater)
        initOnce()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}