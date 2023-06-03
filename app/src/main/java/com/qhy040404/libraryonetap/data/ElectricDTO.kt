package com.qhy040404.libraryonetap.data

data class ElectricDTO(
    val dormitoryInfo_list: List<ElectricInnerDTO>? = null,
    val flag: String,
) {
    @Suppress("SpellCheckingInspection")
    data class ElectricInnerDTO(
        val SSMC: String? = null,
        val ZSBH: String? = null,
        val flag: String,
        val resele: String? = null,
    )
}
