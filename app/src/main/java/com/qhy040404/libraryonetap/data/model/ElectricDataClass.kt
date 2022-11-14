package com.qhy040404.libraryonetap.data.model

@Suppress("SpellCheckingInspection")
data class ElectricDataClass(
    val dormitoryInfo_list: List<ElectricInnerDataClass>? = null,
)

data class ElectricInnerDataClass(
    val SSMC: String? = null,
    val flag: String,
    val resele: String? = null,
)
