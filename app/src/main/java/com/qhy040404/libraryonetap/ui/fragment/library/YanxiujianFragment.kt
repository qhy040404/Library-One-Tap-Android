package com.qhy040404.libraryonetap.ui.fragment.library

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.annotation.OrderModes
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.model.OrderListDataClass
import com.qhy040404.libraryonetap.databinding.FragmentYanxiujianBinding
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.extensions.ViewExtensions.mLoad
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
        binding.yxjDetail.visibility = View.VISIBLE
        binding.yxjRefresh.setOnClickListener {
            Requests.netLazyMgr.reset()
            CookieJarImpl.reset()
            SPUtils.spLazyMgr.reset()
            GlobalValues.netError = false
            GlobalValues.librarySessionReady = null
            GlobalValues.initBasic()
            activity?.recreate()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            yanxiujian()
        }.also {
            it.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun yanxiujian() {
        val detail = binding.yxjDetail
        val qr = binding.yxjQr
        val loading = binding.yxjLoading

        val loginSuccess: Boolean

        while (true) {
            if (GlobalValues.librarySessionReady != null) {
                loginSuccess = GlobalValues.librarySessionReady!!
                loading.post {
                    loading.visibility = View.INVISIBLE
                }
                break
            }
        }

        val list = Requests.get(URLManager.LIBRARY_ORDER_LIST_URL, detail)
        OrderListData.mClass =
            runCatching {
                moshi.adapter(OrderListDataClass::class.java).fromJson(list.trim())
            }.getOrNull()
        val total = OrderListData.getTotal()
        if (total != "0") {
            val space_name = OrderListData.getSpace_name(OrderModes.YANXIUJIAN)
            val order_date = OrderListData.getOrder_date(OrderModes.YANXIUJIAN)
            var order_id = OrderListData.getOrder_id(OrderModes.YANXIUJIAN)
            var order_process = OrderListData.getOrder_process(OrderModes.YANXIUJIAN)
            val all_users = OrderListData.getAll_users()
            val full_time = OrderListData.getFull_time()

            if (order_id == "oid") {
                order_id = AppUtils.getResString(R.string.no_valid_order)
            }

            when (order_process) {
                "审核通过" -> order_process = AppUtils.getResString(R.string.not_start)
                "进行中" -> order_process = AppUtils.getResString(R.string.inside)
                "暂离" -> order_process = AppUtils.getResString(R.string.outside)
            }

            val request = Request.Builder().url(URLManager.LIBRARY_QR_CERT_URL).build()
            val call = Requests.client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                override fun onResponse(call: Call, response: Response) {
                    qr.mLoad(requireContext(), response.body!!.bytes())
                }
            })
            detail.post {
                detail.text = """
                    order_id: $order_id

                    $order_process

                    $space_name
                    $order_date
                    $full_time
                    $all_users
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
