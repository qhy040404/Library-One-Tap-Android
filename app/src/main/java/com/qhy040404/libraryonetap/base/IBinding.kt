package com.qhy040404.libraryonetap.base

import androidx.viewbinding.ViewBinding

internal sealed interface IBinding<VB : ViewBinding> {
    val binding: VB
}
