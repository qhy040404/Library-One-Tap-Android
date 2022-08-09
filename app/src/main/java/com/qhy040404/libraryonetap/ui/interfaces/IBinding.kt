package com.qhy040404.libraryonetap.ui.interfaces

import androidx.databinding.ViewDataBinding

internal sealed interface IBinding<VB : ViewDataBinding> {
    val binding: VB
}