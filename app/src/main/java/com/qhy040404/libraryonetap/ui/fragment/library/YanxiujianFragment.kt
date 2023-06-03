package com.qhy040404.libraryonetap.ui.fragment.library

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.constant.enums.OrderModes
import com.qhy040404.libraryonetap.data.OrderListDTO
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.databinding.FragmentYanxiujianBinding
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.getStringAndFormat
import com.qhy040404.libraryonetap.utils.extensions.mLoad
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
class YanxiujianFragment : BaseFragment<FragmentYanxiujianBinding>() {
    override fun init() {
        binding.yxjDetail.isVisible = true
        binding.yxjRefresh.setOnClickListener {
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
            yanxiujian()
        }
    }

    private fun yanxiujian() {
        val detail = binding.yxjDetail
        val qr = binding.yxjQr
        val loading = binding.yxjLoading

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
                detail.text = GlobalValues.netPrompt
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
            val order_id = OrderListData.getOrder_id(OrderModes.YANXIUJIAN)
                ?: R.string.df_no_valid_order.getString()

            val space_name = OrderListData.getSpace_name(OrderModes.YANXIUJIAN)
            val order_date = OrderListData.getOrder_date(OrderModes.YANXIUJIAN)
            var order_process = OrderListData.getOrder_process(OrderModes.YANXIUJIAN)
            val all_users = OrderListData.getAll_users()
            val full_time = OrderListData.getFull_time()

            order_process = when (order_process) {
                "审核通过" -> R.string.df_not_start.getString()
                "进行中" -> R.string.df_inside.getString()
                "暂离" -> R.string.df_outside.getString()
                else -> order_process
            }

            val request = Request.Builder().url(URLManager.LIBRARY_QR_CERT_URL).build()
            Requests.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                override fun onResponse(call: Call, response: Response) {
                    runCatching {
                        qr.mLoad(requireContext(), response.body!!.bytes())
                    }.onFailure {
                        return
                    }
                }
            })
            runOnUiThread {
                detail.text = R.string.df_order_id_format.getStringAndFormat(
                    order_id,
                    order_process,
                    space_name,
                    order_date,
                    full_time,
                    all_users
                )
                loading.isVisible = false
            }
        }
    }
}
