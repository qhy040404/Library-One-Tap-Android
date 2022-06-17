package com.qhy040404.libraryonetap.datamodel

import com.qhy040404.libraryonetap.constant.GlobalManager.gson
import com.qhy040404.libraryonetap.utils.TimeUtils
import java.io.Serializable

@Suppress("FunctionName")
object OrderListData {
    fun getTotal(data: String): String {
        return gson.fromJson(data, GsonData::class.java).total!!
    }

    fun getOrder_id(data: String, mode: String): String {
        return gson.fromJson(data, GsonData::class.java).getOrder_id(mode)
    }

    fun getOrder_process(data: String, mode: String): String {
        return gson.fromJson(data, GsonData::class.java).getOrder_process(mode)
    }

    fun getSpace_name(data: String, mode: String): String {
        return gson.fromJson(data, GsonData::class.java).getSpace_name(mode)
    }

    fun getSeat_label(data: String, mode: String): String {
        return gson.fromJson(data, GsonData::class.java).getSeat_label(mode)
    }

    fun getOrder_date(data: String, mode: String): String {
        return gson.fromJson(data, GsonData::class.java).getOrder_date(mode)
    }

    fun getBack_time(data: String, mode: String, prompt: String): String {
        return gson.fromJson(data, GsonData::class.java).getBack_time(mode, prompt)
    }

    fun getAll_users(data: String): String {
        return gson.fromJson(data, GsonData::class.java).getAll_users()
    }

    fun getFull_time(data: String): String {
        return gson.fromJson(data, GsonData::class.java).getFull_time()
    }

    @Suppress("LocalVariableName", "PropertyName")
    private class GsonData : Serializable {
        val today = TimeUtils.getToday("-", true)
        val total: String? = null
        val rows: List<RowsBean>? = null

        fun getOrder_id(mode: String): String {
            var order_id = "oid"
            var notHasToday = true
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                    order_id = list.order_id!!
                    notHasToday = false
                    break
                }
            }
            if (notHasToday) {
                for (i in rows.indices) {
                    val list = rows[i]
                    if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                        order_id = list.order_id!!
                        break
                    }
                }
            }
            return order_id
        }

        fun getOrder_process(mode: String): String {
            var order_process = ""
            var notHasToday = true
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                    order_process = list.order_process
                    notHasToday = false
                    break
                }
            }
            if (notHasToday) {
                for (i in rows.indices) {
                    val list = rows[i]
                    if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                        order_process = list.order_process
                        break
                    }
                }
            }
            return order_process
        }

        fun getSpace_name(mode: String): String {
            var space_name = ""
            var notHasToday = true
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                    space_name = list.space_name!!
                    notHasToday = false
                    break
                }
            }
            if (notHasToday) {
                for (i in rows.indices) {
                    val list = rows[i]
                    if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                        space_name = list.space_name!!
                        break
                    }
                }
            }
            return space_name
        }

        fun getSeat_label(mode: String): String {
            var seat_label = ""
            var notHasToday = true
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                    seat_label = list.seat_label.toString()
                    notHasToday = false
                    break
                }
            }
            if (notHasToday) {
                for (i in rows.indices) {
                    val list = rows[i]
                    if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                        seat_label = list.seat_label.toString()
                        break
                    }
                }
            }
            return seat_label
        }

        fun getOrder_date(mode: String): String {
            var order_date = ""
            var notHasToday = true
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode && list.order_date == today) {
                    order_date = list.order_date
                    notHasToday = false
                    break
                }
            }
            if (notHasToday) {
                for (i in rows.indices) {
                    val list = rows[i]
                    if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == mode) {
                        order_date = list.order_date!!
                        break
                    }
                }
            }
            return order_date
        }

        fun getBack_time(mode: String, prompt: String): String {
            var back_time = ""
            for (i in rows!!.indices) {
                val list = rows[i]
                if (list.order_process == "暂离" && list.order_type == mode) {
                    if (list.back_time != "00:00:00") {
                        back_time = prompt + list.back_time
                    }
                    break
                }
            }
            return back_time
        }

        fun getAll_users(): String {
            var all_users = ""
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == "1") {
                    all_users = list.all_users.toString()
                    break
                }
            }
            return all_users
        }

        fun getFull_time(): String {
            var full_time = ""
            for (i in rows!!.indices) {
                val list = rows[i]
                if ((list.order_process == "进行中" || list.order_process == "暂离" || list.order_process == "审核通过") && list.order_type == "1") {
                    full_time = list.order_start_time!!.split(" ")
                        .toTypedArray()[1] + "-" + list.order_end_time!!.split(" ")
                        .toTypedArray()[1]
                    break
                }
            }
            return full_time
        }

        class RowsBean : Serializable {
            val order_id: String? = null
            val order_type: String? = null
            val space_name: String? = null
            val seat_label: Any? = null
            val all_users: Any? = null
            val order_start_time: String? = null
            val order_date: String? = null
            val back_time: String? = null
            val order_end_time: String? = null
            val order_process: String? = null
        }
    }
}