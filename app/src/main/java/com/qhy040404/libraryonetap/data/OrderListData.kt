package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.enums.OrderModes
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString

@Suppress("FunctionName", "LocalVariableName", "PropertyName")
object OrderListData {
    var mClass: OrderListDTO? = null

    private val today = TimeUtils.getToday("-", true)
    private val orderProcesses = arrayOf("进行中", "暂离", "审核通过")

    /**
     * get total order count
     * @return total order count String
     */
    fun getTotal() = runCatching {
        mClass?.total!!
    }.getOrDefault("0")

    /**
     * get current order id
     * @param mode OrderModes enum
     * @return id String
     */
    fun getOrder_id(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    mode.toString(),
                    list.order_date
                )
            ) {
                return list.order_id!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode.toString())) {
                return list.order_id!!
            }
        }
        return "oid"
    }

    /**
     * get current order process
     * @param mode OrderModes enum
     * @return process String
     */
    fun getOrder_process(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    mode.toString(),
                    list.order_date
                )
            ) {
                return list.order_process!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode.toString())) {
                return list.order_process!!
            }
        }
        return ""
    }

    /**
     * get current order's space
     * @param mode OrderModes enum
     * @return space String
     */
    fun getSpace_name(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    mode.toString(),
                    list.order_date
                )
            ) {
                return list.space_name!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode.toString())) {
                return list.space_name!!
            }
        }
        return ""
    }

    /**
     * get current order's seat label
     * @param mode OrderModes enum
     * @return seat label String
     */
    fun getSeat_label(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    mode.toString(),
                    list.order_date
                )
            ) {
                return list.seat_label.toString()
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode.toString())) {
                return list.seat_label.toString()
            }
        }
        return ""
    }

    /**
     * get current order's date
     * @param mode OrderModes enum
     * @return date String
     */
    fun getOrder_date(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    mode.toString(),
                    list.order_date
                )
            ) {
                return list.order_date!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode.toString())) {
                return list.order_date!!
            }
        }
        return ""
    }

    /**
     * get back time
     * @param mode OrderModes enum
     * @return back time String
     */
    fun getBack_time(mode: OrderModes): String {
        for (list in mClass?.rows!!) {
            if (list.order_process == "暂离" && list.order_type == mode.toString()) {
                if (list.back_time != "00:00:00") {
                    return R.string.df_temp_end_time.getString() + list.back_time
                }
                break
            }
        }
        return ""
    }

    /**
     * get all users ( yanxiujian )
     * @return all users String
     */
    fun getAll_users(): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    OrderModes.YANXIUJIAN.toString()
                )
            ) {
                return list.all_users.toString()
            }
        }
        return ""
    }

    /**
     * get full time ( yanxiujian )
     * @return full time String
     */
    fun getFull_time(): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(
                    list.order_process,
                    list.order_type,
                    OrderModes.YANXIUJIAN.toString()
                )
            ) {
                return list.order_start_time!!.substringAfter(" ") + "-" +
                    list.order_end_time!!.substringAfter(" ")
            }
        }
        return ""
    }

    private fun orderIsValid(process: String?, type: String?, typeValidator: String): Boolean {
        return process in orderProcesses && type == typeValidator
    }

    private fun orderIsValid(
        process: String?,
        type: String?,
        typeValidator: String,
        date: String?,
    ): Boolean {
        return process in orderProcesses && type == typeValidator && date == today
    }
}

data class OrderListDTO(
    val total: String? = null,
    val rows: List<OrderListInnerDTO>? = null,
)

data class OrderListInnerDTO(
    val all_users: String? = null,
    val area_id: String? = null,
    val back_time: String? = null,
    val order_admin_user: String? = null,
    val order_date: String? = null,
    val order_end_time: String? = null,
    val order_id: String? = null,
    val order_process: String? = null,
    val order_start_time: String? = null,
    val order_type: String? = null,
    val order_time: String? = null,
    val order_users: String? = null,
    val punish_status: String? = null,
    val seat_label: String? = null,
    val space_name: String? = null,
)
