package com.qhy040404.libraryonetap.datamodel

data class OrderListDataClass(
    val total: String? = null,
    val rows: List<OrderListInnerDataClass>? = null,
)
