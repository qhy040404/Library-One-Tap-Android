package com.qhy040404.libraryonetap.ui.fragment.library

import android.annotation.SuppressLint
import android.os.StrictMode
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.constant.enums.OrderModes
import com.qhy040404.libraryonetap.data.CancelData
import com.qhy040404.libraryonetap.data.OrderListDTO
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.ReserveData
import com.qhy040404.libraryonetap.databinding.FragmentSingleBinding
import com.qhy040404.libraryonetap.ui.dialog.ReserveDialog
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.mLoad
import com.qhy040404.libraryonetap.utils.library.ReserveUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@Suppress("LocalVariableName")
class SingleFragment : BaseFragment<FragmentSingleBinding>() {
    override fun init() {
        binding.detailDetail.visibility = View.VISIBLE
        binding.detailRefresh.setOnClickListener {
            Requests.netLazyMgr.reset()
            CookieJarImpl.reset()
            SPUtils.spLazyMgr.reset()
            GlobalValues.netError = false
            Requests.libInitialized = false
            Requests.eduInitialized = false
            GlobalValues.initBasic()
            activity?.recreate()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            detail()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun detail() {
        val detail = binding.detailDetail
        val leave = binding.detailLeave
        val tempLeave = binding.detailTemp
        val enter = binding.detailEnter
        val qr = binding.detailQr
        val cancel = binding.detailCancel
        val reserve = binding.detailNew
        val reset = binding.detailReset
        val tempReset = binding.detailTempReset
        val loading = binding.detailLoading
        val type = binding.detailType

        if (!AppUtils.hasNetwork()) {
            runOnUiThread {
                detail.text = R.string.glb_net_disconnected.getString()
                loading.visibility = View.INVISIBLE
            }
            return
        }

        if (!TimeUtils.isServerAvailableTime()) {
            runOnUiThread {
                detail.text = R.string.df_server_unavailable.getString()
                loading.visibility = View.INVISIBLE
            }
            return
        }

        val loginSuccess = Requests.initLib()

        OrderListData.mClass = runCatching {
            Requests.get(URLManager.LIBRARY_ORDER_LIST_URL, detail).decode<OrderListDTO>()
        }.getOrNull()
        if (OrderListData.getTotal() != "0") {
            val space_name = OrderListData.getSpace_name(OrderModes.DETAIL)
            val seat_label = OrderListData.getSeat_label(OrderModes.DETAIL)
            val order_date = OrderListData.getOrder_date(OrderModes.DETAIL)
            var order_id = OrderListData.getOrder_id(OrderModes.DETAIL)
            var order_process = OrderListData.getOrder_process(OrderModes.DETAIL)
            val back_time = OrderListData.getBack_time(OrderModes.DETAIL)

            if (order_id == "oid") {
                order_id = R.string.df_no_valid_order.getString()
                runOnUiThread {
                    reserve.visibility = View.VISIBLE
                    reserve.isClickable = true
                }
            }

            when (order_process) {
                "审核通过" -> {
                    order_process = R.string.df_not_start.getString()

                    runOnUiThread {
                        cancel.visibility = View.VISIBLE
                        cancel.isClickable = true
                    }

                    val today = TimeUtils.getToday("-", true)
                    runOnUiThread {
                        if (order_date != today) {
                            reserve.visibility = View.VISIBLE
                            reserve.isClickable = true
                        } else {
                            reset.visibility = View.VISIBLE
                            reset.isClickable = true
                        }
                    }
                }

                "进行中" -> order_process = R.string.df_inside.getString()
                "暂离" -> {
                    order_process = R.string.df_outside.getString()
                    runOnUiThread {
                        tempReset.visibility = View.VISIBLE
                        tempReset.isClickable = true
                    }
                }
            }

            if (space_name.contains("临时")) {
                runOnUiThread {
                    reset.visibility = View.INVISIBLE
                    reset.isClickable = false
                }
            }

            runOnUiThread {
                enter.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_IN, order_id))
                            .build()
                    Requests.client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            runCatching { qr.mLoad(requireContext(), response.body!!.bytes()) }
                            runOnUiThread { type.text = R.string.df_enter.getString() }
                        }
                    })
                }
                leave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_OUT, order_id))
                            .build()
                    Requests.client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            runCatching { qr.mLoad(requireContext(), response.body!!.bytes()) }
                            runOnUiThread { type.text = R.string.df_leave.getString() }
                        }
                    })
                }
                tempLeave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_TEMP, order_id))
                            .build()
                    Requests.client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            runCatching { qr.mLoad(requireContext(), response.body!!.bytes()) }
                            runOnUiThread { type.text = R.string.df_temp.getString() }
                        }
                    })
                }
                cancel.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.df_cancel_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.df_cancel_confirm_yes) { _, _ ->
                            val message = CancelData.getMessage(
                                Requests.post(
                                    URLManager.LIBRARY_ORDER_OPERATION_URL,
                                    "order_id=$order_id&order_type=2&method=Cancel",
                                    GlobalValues.ctSso
                                )
                            )
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage(message)
                                .setTitle(R.string.library)
                                .setPositiveButton(R.string.glb_ok) { _, _ -> activity?.recreate() }
                                .setCancelable(true)
                                .create()
                                .show()
                        }
                        .setNegativeButton(R.string.df_cancel_confirm_no) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                }
                reserve.setOnClickListener {
                    ReserveDialog().showAlertDialog(requireActivity())
                }
                reset.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.df_reserve_reset_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.glb_ok) { _, _ ->
                            val roomCode = ReserveUtils.getResetRoomCode(space_name).toString()
                            val targetSeat = "\"seat_label\":\"$seat_label\""
                            var seat_id = ""

                            val availableMap = ReserveUtils.formatAvailableMap(
                                Requests.get(
                                    URLManager.getSeatAvailableUrl(
                                        TimeUtils.getToday("/", false),
                                        roomCode
                                    ),
                                    detail
                                )
                            )
                            val amList = availableMap.split(",")

                            for (element in amList) {
                                if (element == targetSeat) {
                                    val i = amList.indexOf(element)
                                    if (amList[i + 4] == Constants.RESERVE_VALID || amList[i + 4] == Constants.RESERVE_HAS_PERSON) {
                                        seat_id =
                                            amList[amList.indexOf(element) - 1].replace(
                                                "\"seat_id\":",
                                                ""
                                            ).replace("\"", "")
                                        break
                                    }
                                }
                            }

                            Requests.post(
                                URLManager.LIBRARY_ORDER_OPERATION_URL,
                                "order_id=$order_id&order_type=2&method=Cancel",
                                GlobalValues.ctSso
                            )

                            val addCodeOrigin = Requests.post(
                                URLManager.LIBRARY_RESERVE_ADDCODE_URL,
                                ReserveUtils.constructParaForAddCode(seat_id),
                                GlobalValues.ctVCard
                            )
                            val addCode = ReserveData.getAddCode(addCodeOrigin)
                            Requests.post(
                                URLManager.LIBRARY_RESERVE_FINAL_URL,
                                ReserveUtils.constructParaForFinalReserve(addCode),
                                GlobalValues.ctVCard
                            )
                            activity?.recreate()
                        }
                        .setNegativeButton(R.string.glb_no) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
                tempReset.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.df_reserve_reset_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.glb_ok) { _, _ ->
                            val roomCode = ReserveUtils.getResetRoomCode(space_name).toString()
                            val targetSeat = "\"seat_label\":\"$seat_label\""
                            var seat_id = ""

                            val availableMap = ReserveUtils.formatAvailableMap(
                                Requests.get(
                                    URLManager.getSeatAvailableUrl(
                                        TimeUtils.getToday("/", false),
                                        roomCode
                                    ), detail
                                )
                            )
                            val amList = availableMap.split(",")

                            for (element in amList) {
                                if (element == targetSeat) {
                                    val i = amList.indexOf(element)
                                    if (amList[i + 4] == Constants.RESERVE_VALID || amList[i + 4] == Constants.RESERVE_HAS_PERSON) {
                                        seat_id =
                                            amList[amList.indexOf(element) - 1].replace(
                                                "\"seat_id\":",
                                                ""
                                            ).replace("\"", "")
                                        break
                                    }
                                }
                            }

                            Requests.post(
                                URLManager.LIBRARY_ORDER_OPERATION_URL,
                                "order_id=$order_id&order_type=2&method=Release",
                                GlobalValues.ctSso
                            )

                            val addCodeOrigin = Requests.post(
                                URLManager.LIBRARY_RESERVE_ADDCODE_URL,
                                ReserveUtils.constructParaForAddCode(seat_id),
                                GlobalValues.ctVCard
                            )
                            val addCode = ReserveData.getAddCode(addCodeOrigin)
                            Requests.post(
                                URLManager.LIBRARY_RESERVE_FINAL_URL,
                                ReserveUtils.constructParaForFinalReserve(addCode),
                                GlobalValues.ctVCard
                            )
                            activity?.recreate()
                        }
                        .setNegativeButton(R.string.glb_no) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
                detail.text = """
                    order_id: $order_id

                    $order_process

                    $space_name
                    $seat_label
                    $order_date
                    $back_time
                """.trimIndent()
                loading.visibility = View.INVISIBLE
            }
        } else if (!AppUtils.checkData(GlobalValues.id, GlobalValues.passwd)) {
            runOnUiThread {
                detail.text = R.string.glb_no_userdata.getString()
                loading.visibility = View.INVISIBLE
            }
        } else if (!loginSuccess) {
            runOnUiThread {
                detail.text = R.string.glb_fail_to_login_three_times.getString()
            }
        } else if (!GlobalValues.netError) {
            runOnUiThread {
                detail.text = R.string.glb_login_timeout.getString()
            }
        }
    }
}
