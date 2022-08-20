package com.qhy040404.libraryonetap.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.qhy040404.libraryonetap.utils.extensions.CompatExtensions.inflateBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected lateinit var binding: VB

    private var parentActivityVisible = false
    private var visible = false
    private var localParentFragment: BaseFragment<VB>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun init()

    open fun onVisibilityChanged(visible: Boolean) {
        this.visible = visible
    }

    fun isFragmentVisible(): Boolean {
        return visible
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding(layoutInflater)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = binding.root

    override fun onResume() {
        super.onResume()
        onVisibilityChanged(true)
    }

    override fun onPause() {
        super.onPause()
        onVisibilityChanged(false)
    }
}
