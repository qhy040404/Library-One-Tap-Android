package com.qhy040404.libraryonetap.ui.fragment.library

import android.annotation.SuppressLint
import android.os.StrictMode
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.annotation.OrderModes
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.CancelData
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.ReserveData
import com.qhy040404.libraryonetap.data.model.OrderListDataClass
import com.qhy040404.libraryonetap.databinding.FragmentDetailBinding
import com.qhy040404.libraryonetap.ui.dialog.ReserveDialog
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.ViewExtensions.mLoad
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
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override fun init() {
        binding.detailDetail.visibility = View.VISIBLE
        binding.detailRefresh.setOnClickListener {
            Requests.netLazyMgr.reset()
            CookieJarImpl.reset()
            SPUtils.spLazyMgr.reset()
            GlobalValues.netError = false
            GlobalValues.librarySessionReady = null
            GlobalValues.initBasic()
            activity?.recreate()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            detail()
        }.also {
            it.start()
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

        val loginSuccess = Requests.loginSso(URLManager.LIBRARY_SSO_URL,
            GlobalValues.ctSso,
            URLManager.LIBRARY_SESSION_URL,
            loading,
            needCheck = true,
            hasSessionJson = true)
        GlobalValues.librarySessionReady = loginSuccess

        val list = Requests.get(URLManager.LIBRARY_ORDER_LIST_URL, detail)
        OrderListData.mClass =
            runCatching {
                moshi.adapter(OrderListDataClass::class.java).fromJson(list.trim())
            }.getOrNull()
        val total = OrderListData.getTotal()
        if (total != "0") {
            val space_name = OrderListData.getSpace_name(OrderModes.DETAIL)
            val seat_label = OrderListData.getSeat_label(OrderModes.DETAIL)
            val order_date = OrderListData.getOrder_date(OrderModes.DETAIL)
            var order_id = OrderListData.getOrder_id(OrderModes.DETAIL)
            var order_process = OrderListData.getOrder_process(OrderModes.DETAIL)
            val back_time = OrderListData.getBack_time(OrderModes.DETAIL)

            if (order_id == "oid") {
                order_id = AppUtils.getResString(R.string.no_valid_order)
                reserve.post {
                    reserve.visibility = View.VISIBLE
                    reserve.isClickable = true
                }
            }

            if (order_process == "审核通过") {
                order_process = AppUtils.getResString(R.string.not_start)

                cancel.post {
                    cancel.visibility = View.VISIBLE
                    cancel.isClickable = true
                }

                val today = TimeUtils.getToday("-", true)
                if (order_date != today) {
                    reserve.post {
                        reserve.visibility = View.VISIBLE
                        reserve.isClickable = true
                    }
                } else {
                    reset.post {
                        reset.visibility = View.VISIBLE
                        reset.isClickable = true
                    }
                }
            } else if (order_process == "进行中") {
                order_process = AppUtils.getResString(R.string.inside)
            } else if (order_process == "暂离") {
                order_process = AppUtils.getResString(R.string.outside)
                tempReset.post {
                    tempReset.visibility = View.VISIBLE
                    tempReset.isClickable = true
                }
            }

            enter.post {
                enter.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_IN, order_id))
                            .build()
                    val call = Requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            qr.mLoad(requireContext(), response.body!!.bytes())
                            type.post { type.text = AppUtils.getResString(R.string.enter) }
                        }
                    })
                }
            }
            leave.post {
                leave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_OUT, order_id))
                            .build()
                    val call = Requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            qr.mLoad(requireContext(), response.body!!.bytes())
                            type.post { type.text = AppUtils.getResString(R.string.leave) }
                        }
                    })
                }
            }
            tempLeave.post {
                tempLeave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(URLManager.getQRUrl(Constants.LIBRARY_METHOD_TEMP, order_id))
                            .build()
                    val call = Requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            qr.mLoad(requireContext(), response.body!!.bytes())
                            type.post { type.text = AppUtils.getResString(R.string.temp) }
                        }
                    })
                }
            }
            cancel.post {
                cancel.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.cancel_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.cancel_confirm_yes) { _, _ ->
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
                                .setPositiveButton(R.string.ok) { _, _ -> activity?.recreate() }
                                .setCancelable(true)
                                .create()
                                .show()
                        }
                        .setNegativeButton(R.string.cancel_confirm_no) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                }
            }
            reserve.post {
                reserve.setOnClickListener {
                    ReserveDialog().showAlertDialog(requireActivity())
                }
            }
            reset.post {
                reset.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.reserve_reset_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.ok) { _, _ ->
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
                        .setNegativeButton(R.string.no) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
            }
            tempReset.post {
                tempReset.setOnClickListener {
                    StrictMode.setThreadPolicy(
                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.reserve_reset_confirm)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.ok) { _, _ ->
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
                        .setNegativeButton(R.string.no) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
            }
            detail.post {
                detail.text = """
                    order_id: $order_id

                    $order_process

                    $space_name
                    $seat_label
                    $order_date
                    $back_time
                """.trimIndent()
            }
        } else if (!AppUtils.checkData(GlobalValues.id, GlobalValues.passwd)) {
            detail.post {
                detail.text = AppUtils.getResString(R.string.no_userdata)
            }
            loading.post { loading.visibility = View.INVISIBLE }
        } else if (!loginSuccess) {
            detail.post {
                detail.text =
                    AppUtils.getResString(R.string.fail_to_login_three_times)
            }
        } else if (GlobalValues.netError) {
            AppUtils.pass()
        } else {
            detail.post {
                detail.text = AppUtils.getResString(R.string.login_timeout)
            }
        }
    }
}
