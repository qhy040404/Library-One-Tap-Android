package com.qhy040404.libraryonetap.ui.interfaces

interface IFragment {
    fun onBackPressed(): Boolean = false
    fun finish()
}