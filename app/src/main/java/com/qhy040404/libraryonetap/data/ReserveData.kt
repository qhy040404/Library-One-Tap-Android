package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode

object ReserveData {
    fun getAddCode(data: String) = data.decode<ReserveDTO>()?.data!!.addCode!!
}

data class ReserveDTO(
    val data: ReserveInnerDTO? = null,
    val success: Boolean,
)

data class ReserveInnerDTO(
    val addCode: String? = null,
    val area_name: String? = null,
    val attention_text: String? = null,
    val open_end: String? = null,
    val open_start: String? = null,
    val order_start: String? = null,
    val room_id: String? = null,
    val room_name: String? = null,
    val rule_cancel_time: String? = null,
    val rule_check_time: String? = null,
    val seat_id: String? = null,
    val seat_label: String? = null,
)
