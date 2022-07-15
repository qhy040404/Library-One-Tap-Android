package com.qhy040404.libraryonetap.fragment

import androidx.recyclerview.widget.RecyclerView
import rikka.widget.borderview.BorderViewDelegate

interface IListController {
    fun onReturnTop()
    fun getBorderViewDelegate(): BorderViewDelegate?
    fun isAllowRefreshing(): Boolean
    fun getSuitableLayoutManager(): RecyclerView.LayoutManager?
}