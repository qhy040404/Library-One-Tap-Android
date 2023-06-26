package com.qhy040404.libraryonetap.ui.fragment.library

import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.constant.enums.OrderModes
import com.qhy040404.libraryonetap.data.CancelDTO
import com.qhy040404.libraryonetap.data.OrderListDTO
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.ReserveDTO
import com.qhy040404.libraryonetap.databinding.FragmentSingleBinding
import com.qhy040404.libraryonetap.ui.dialog.ReserveDialog
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.getStringAndFormat
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
        binding.detailDetail.isVisible = true
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

    private fun detail() {
        val detail = binding.detailDetail
        val cancel = binding.detailCancel
        val reserve = binding.detailNew
        val reset = binding.detailReset
        val tempReset = binding.detailTempReset
        val loading = binding.detailLoading

        if (AppUtils.hasNetwork().not()) {
            runOnUiThread {
                detail.text = R.string.glb_net_disconnected.getString()
                loading.isVisible = false
            }
            return
        }

        if (AppUtils.checkData(GlobalValues.id, GlobalValues.passwd).not()) {
            runOnUiThread {
                detail.text = R.string.glb_no_userdata.getString()
                loading.isVisible = false
            }
            return
        }

        if (TimeUtils.isServerAvailableTime().not()) {
            runOnUiThread {
                detail.text = R.string.df_server_unavailable.getString()
                loading.isVisible = false
            }
            return
        }

        if (Requests.initLib().not()) {
            runOnUiThread {
                detail.text = GlobalValues.ssoPrompt
            }
            return
        }

        OrderListData.mClass = runCatching {
            Requests.get(URLManager.LIBRARY_ORDER_LIST_URL).decode<OrderListDTO>()
        }.getOrElse {
            runOnUiThread {
                detail.text = GlobalValues.netPrompt
            }
            return
        }

        if (OrderListData.getTotal() != "0") {
            val order_id = OrderListData.getOrder_id(OrderModes.DETAIL) ?: run {
                runOnUiThread {
                    reserve.isVisible = true
                    reserve.isClickable = true
                }
                R.string.df_no_valid_order.getString()
            }

            val space_name = OrderListData.getSpace_name(OrderModes.DETAIL)
            val seat_label = OrderListData.getSeat_label(OrderModes.DETAIL)
            val order_date = OrderListData.getOrder_date(OrderModes.DETAIL)
            var order_process = OrderListData.getOrder_process(OrderModes.DETAIL)
            val back_time = OrderListData.getBack_time(OrderModes.DETAIL)

            when (order_process) {
                "审核通过" -> {
                    order_process = R.string.df_not_start.getString()

                    runOnUiThread {
                        cancel.isVisible = true
                        cancel.isClickable = true
                    }

                    val today = TimeUtils.getToday("-", true)
                    runOnUiThread {
                        if (order_date != today) {
                            reserve.isVisible = true
                            reserve.isClickable = true
                        } else {
                            reset.isVisible = true
                            reset.isClickable = true
                        }
                    }
                }

                "进行中" -> order_process = R.string.df_inside.getString()
                "暂离" -> {
                    order_process = R.string.df_outside.getString()
                    runOnUiThread {
                        tempReset.isVisible = true
                        tempReset.isClickable = true
                    }
                }
            }

            if (space_name.contains("临时")) {
                runOnUiThread {
                    reset.isVisible = false
                    reset.isClickable = false
                }
            }

            runOnUiThread {
                setEnterClickListener(order_id)
                setLeaveClickListener(order_id)
                setTempLeaveClickListener(order_id)
                setCancelClickListener(order_id)
                setReserveClickListener()
                setResetClickListener(order_id, space_name, seat_label)
                setTempResetClickListener(order_id, space_name, seat_label)
                detail.text = R.string.df_order_id_format.getStringAndFormat(
                    order_id,
                    order_process,
                    space_name,
                    seat_label,
                    order_date,
                    back_time
                )
                loading.isVisible = false
            }
        }
    }

    private fun setEnterClickListener(order_id: String) {
        binding.detailEnter.setOnClickListener {
            getQrCode(Constants.LIBRARY_METHOD_IN, order_id, R.string.df_enter)
        }
    }

    private fun setLeaveClickListener(order_id: String) {
        binding.detailLeave.setOnClickListener {
            getQrCode(Constants.LIBRARY_METHOD_OUT, order_id, R.string.df_leave)
        }
    }

    private fun setTempLeaveClickListener(order_id: String) {
        binding.detailTemp.setOnClickListener {
            getQrCode(Constants.LIBRARY_METHOD_TEMP, order_id, R.string.df_temp)
        }
    }

    private fun getQrCode(method: String, order_id: String, @StringRes resId: Int) {
        val request =
            Request.Builder()
                .url(URLManager.getQRUrl(method, order_id))
                .build()
        Requests.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                runCatching {
                    binding.detailQr.mLoad(
                        requireContext(),
                        response.body!!.bytes()
                    )
                }
                runOnUiThread { binding.detailType.text = resId.getString() }
            }
        })
    }

    private fun setCancelClickListener(order_id: String) {
        if (binding.detailCancel.isVisible.not()) return
        binding.detailCancel.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.df_cancel_confirm)
                .setTitle(R.string.library)
                .setPositiveButton(R.string.df_cancel_confirm_yes) { _, _ ->
                    val message = Requests.post(
                        URLManager.LIBRARY_ORDER_OPERATION_URL,
                        "order_id=$order_id&order_type=2&method=Cancel",
                        GlobalValues.ctSso
                    ).decode<CancelDTO>()?.message

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
    }

    private fun setReserveClickListener() {
        if (binding.detailNew.isVisible.not()) return
        binding.detailNew.setOnClickListener {
            ReserveDialog().showAlertDialog(requireActivity())
        }
    }

    private fun setResetClickListener(order_id: String, space_name: String, seat_label: String) {
        if (binding.detailReset.isVisible.not()) return
        binding.detailReset.setOnClickListener {
            reReserve(order_id, space_name, seat_label)
        }
    }

    private fun setTempResetClickListener(
        order_id: String,
        space_name: String,
        seat_label: String,
    ) {
        if (binding.detailTempReset.isVisible.not()) return
        binding.detailTempReset.setOnClickListener {
            reReserve(order_id, space_name, seat_label)
        }
    }

    private fun reReserve(order_id: String, space_name: String, seat_label: String) {
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
                        )
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

                finalReserve(order_id, seat_id)

                activity?.recreate()
            }
            .setNegativeButton(R.string.glb_no) { _, _ -> }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun finalReserve(order_id: String, seat_id: String) {
        repeat(5) {
            Requests.post(
                URLManager.LIBRARY_ORDER_OPERATION_URL,
                "order_id=$order_id&order_type=2&method=Release",
                GlobalValues.ctSso
            )

            val addCode = Requests.post(
                URLManager.LIBRARY_RESERVE_ADDCODE_URL,
                ReserveUtils.constructParaForAddCode(seat_id),
                GlobalValues.ctVCard
            ).decode<ReserveDTO>()?.data?.addCode ?: ""

            Requests.post(
                URLManager.LIBRARY_RESERVE_FINAL_URL,
                ReserveUtils.constructParaForFinalReserve(addCode),
                GlobalValues.ctVCard
            ).let {
                if (it.contains("预约成功")) return
            }
        }
    }
}
