package com.qhy040404.libraryonetap.data

import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.data.model.OrderListDataClass
import com.qhy040404.libraryonetap.utils.TimeUtils

@Suppress("FunctionName", "LocalVariableName", "PropertyName")
object OrderListData {
    private val today = TimeUtils.getToday("-", true)

    fun getTotal(data: String): String {
        return moshi.adapter(OrderListDataClass::class.java).fromJson(data)?.total!!
    }

    fun getOrder_id(data: String, mode: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var order_id = "oid"
        var notHasToday = true
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                order_id = list.order_id!!
                notHasToday = false
                break
            }
        }
        if (notHasToday) {
            for (i in mClass.rows.indices) {
                val list = mClass.rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                    order_id = list.order_id!!
                    break
                }
            }
        }
        return order_id
    }

    fun getOrder_process(data: String, mode: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var order_process = ""
        var notHasToday = true
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                order_process = list.order_process
                notHasToday = false
                break
            }
        }
        if (notHasToday) {
            for (i in mClass.rows.indices) {
                val list = mClass.rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                    order_process = list.order_process
                    break
                }
            }
        }
        return order_process
    }

    fun getSpace_name(data: String, mode: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var space_name = ""
        var notHasToday = true
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                space_name = list.space_name!!
                notHasToday = false
                break
            }
        }
        if (notHasToday) {
            for (i in mClass.rows.indices) {
                val list = mClass.rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                    space_name = list.space_name!!
                    break
                }
            }
        }
        return space_name
    }

    fun getSeat_label(data: String, mode: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var seat_label = ""
        var notHasToday = true
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                seat_label = list.seat_label.toString()
                notHasToday = false
                break
            }
        }
        if (notHasToday) {
            for (i in mClass.rows.indices) {
                val list = mClass.rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                    seat_label = list.seat_label.toString()
                    break
                }
            }
        }
        return seat_label
    }

    fun getOrder_date(data: String, mode: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var order_date = ""
        var notHasToday = true
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                order_date = list.order_date
                notHasToday = false
                break
            }
        }
        if (notHasToday) {
            for (i in mClass.rows.indices) {
                val list = mClass.rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                    order_date = list.order_date!!
                    break
                }
            }
        }
        return order_date
    }

    fun getBack_time(data: String, mode: String, prompt: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var back_time = ""
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if (list.order_process == "暂离" && list.order_type == mode) {
                if (list.back_time != "00:00:00") {
                    back_time = prompt + list.back_time
                }
                break
            }
        }
        return back_time
    }

    fun getAll_users(data: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var all_users = ""
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == "1") {
                all_users = list.all_users.toString()
                break
            }
        }
        return all_users
    }

    fun getFull_time(data: String): String {
        val mClass = moshi.adapter(OrderListDataClass::class.java).fromJson(data)
        var full_time = ""
        for (i in mClass?.rows!!.indices) {
            val list = mClass.rows[i]
            if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == "1") {
                full_time = list.order_start_time!!.split(" ").toTypedArray()[1] + "-" +
                        list.order_end_time!!.split(" ").toTypedArray()[1]
                break
            }
        }
        return full_time
    }
}