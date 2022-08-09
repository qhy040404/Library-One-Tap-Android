package com.qhy040404.libraryonetap.ui.fragment.library

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.databinding.FragmentYanxiujianBinding
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@Suppress("LocalVariableName")
class YanxiujianFragment : BaseFragment<FragmentYanxiujianBinding>() {
    override fun init() = initView()

    private fun initView() {
        val textView2 = binding.textView2
        textView2.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            yanxiujian()
        }.also {
            it.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun yanxiujian() {
        val textView2 = binding.textView2
        val imageView2 = binding.imageView2
        val refresh2 = binding.button12
        val progressBar2 = binding.progressBar2

        val des = DesEncryptUtils()

        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        var loginSuccess = false
        var timer = 0
        var failLogin = false

        while (!loginSuccess && AppUtils.checkData(id, passwd)) {
            val ltResponse = Requests.get(URLManager.LIBRARY_SSO_URL)
            val ltData = try {
                "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
            } catch (_: Exception) {
                ""
            }
            val ltExecution = try {
                ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
            } catch (_: Exception) {
                ""
            }

            if (ltData != "") {
                val rawData = "$id$passwd$ltData"
                val rsa = des.strEnc(rawData, "1", "2", "3")

                delay(200L)

                Requests.post(
                    URLManager.LIBRARY_SSO_URL,
                    Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
                    GlobalValues.ctSso
                )
            }

            val session = Requests.post(URLManager.LIBRARY_SESSION_URL, "", GlobalValues.ctSso)
            if (SessionData.isSuccess(session)) {
                progressBar2.post { progressBar2.visibility = View.INVISIBLE }
                loginSuccess = true
            } else {
                timer++
                if (timer == 2) Requests.netLazyMgr.reset()
                if (timer >= 3) {
                    textView2.post {
                        textView2.text =
                            AppUtils.getResString(R.string.fail_to_login_three_times)
                    }
                    failLogin = true
                    break
                }
            }
        }
        val list = Requests.get(URLManager.LIBRARY_ORDER_LIST_URL)
        val total = OrderListData.getTotal(list)
        if (total != "0") {
            val space_name = OrderListData.getSpace_name(list, "1")
            val order_date = OrderListData.getOrder_date(list, "1")
            var order_id = OrderListData.getOrder_id(list, "1")
            var order_process = OrderListData.getOrder_process(list, "1")
            val all_users = OrderListData.getAll_users(list)
            val full_time = OrderListData.getFull_time(list)

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
                    val picture_bt = response.body!!.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                    imageView2.post { imageView2.setImageBitmap(bitmap) }
                }
            })
            textView2.post {
                textView2.text =
                    "order_id: $order_id\n\n$order_process\n\n$space_name\n$order_date\n$full_time\n\n$all_users"
            }
        } else if (!AppUtils.checkData(id, passwd)) {
            textView2.post {
                textView2.text = AppUtils.getResString(R.string.no_userdata)
            }
            progressBar2.post { progressBar2.visibility = View.INVISIBLE }
        } else if (failLogin) {
            AppUtils.pass()
        } else {
            textView2.post {
                textView2.text = AppUtils.getResString(R.string.login_timeout)
            }
        }
        refresh2.post {
            refresh2.setOnClickListener {
                Requests.netLazyMgr.reset()
                activity?.recreate()
            }
        }
    }
}