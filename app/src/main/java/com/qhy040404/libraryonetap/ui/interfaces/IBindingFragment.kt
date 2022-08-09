package com.qhy040404.libraryonetap.ui.interfaces

import androidx.annotation.MainThread
import androidx.databinding.ViewDataBinding

internal interface IBindingFragment<VB : ViewDataBinding> : IBinding<VB> {
    @MainThread
    fun initOnce() {
    }
}