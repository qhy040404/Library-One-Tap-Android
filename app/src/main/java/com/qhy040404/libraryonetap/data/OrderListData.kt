package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.annotation.OrderModes
import com.qhy040404.libraryonetap.data.model.OrderListDataClass
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.TimeUtils

@Suppress("FunctionName", "LocalVariableName", "PropertyName")
object OrderListData {
    var mClass: OrderListDataClass? = null

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
     * @param mode @OrderModes String detail or yanxiujian
     * @return id String
     */
    fun getOrder_id(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode, list.order_date)) {
                return list.order_id!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode)) {
                return list.order_id!!
            }
        }
        return "oid"
    }

    /**
     * get current order process
     * @param mode @OrderModes String detail or yanxiujian
     * @return process String
     */
    fun getOrder_process(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode, list.order_date)) {
                return list.order_process!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode)) {
                return list.order_process!!
            }
        }
        return ""
    }

    /**
     * get current order's space
     * @param mode @OrderModes String detail or yanxiujian
     * @return space String
     */
    fun getSpace_name(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode, list.order_date)) {
                return list.space_name!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode)) {
                return list.space_name!!
            }
        }
        return ""
    }

    /**
     * get current order's seat label
     * @param mode @OrderModes String detail or yanxiujian
     * @return seat label String
     */
    fun getSeat_label(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode, list.order_date)) {
                return list.seat_label.toString()
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode)) {
                return list.seat_label.toString()
            }
        }
        return ""
    }

    /**
     * get current order's date
     * @param mode @OrderModes String detail or yanxiujian
     * @return date String
     */
    fun getOrder_date(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode, list.order_date)) {
                return list.order_date!!
            }
        }
        for (list in mClass?.rows!!) {
            if (orderIsValid(list.order_process, list.order_type, mode)) {
                return list.order_date!!
            }
        }
        return ""
    }

    /**
     * get back time
     * @param mode @OrderModes String detail or yanxiujian
     * @return back time String
     */
    fun getBack_time(@OrderModes mode: String): String {
        for (list in mClass?.rows!!) {
            if (list.order_process == "暂离" && list.order_type == mode) {
                if (list.back_time != "00:00:00") {
                    return AppUtils.getResString(R.string.temp_end_time) + list.back_time
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
            if (orderIsValid(list.order_process, list.order_type, OrderModes.YANXIUJIAN)) {
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
            if (orderIsValid(list.order_process, list.order_type, OrderModes.YANXIUJIAN)) {
                return list.order_start_time!!.split(" ").toTypedArray()[1] + "-" +
                    list.order_end_time!!.split(" ").toTypedArray()[1]
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
