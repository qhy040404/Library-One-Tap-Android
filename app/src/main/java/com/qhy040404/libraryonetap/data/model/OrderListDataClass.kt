package com.qhy040404.libraryonetap.data.model

data class OrderListDataClass(
    val total: String? = null,
    val rows: List<OrderListInnerDataClass>? = null,
)
