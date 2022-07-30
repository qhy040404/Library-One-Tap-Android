package com.qhy040404.libraryonetap.fragment.library

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseFragment
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.CancelData
import com.qhy040404.libraryonetap.data.OrderListData
import com.qhy040404.libraryonetap.data.ReserveData
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.databinding.FragmentDetailBinding
import com.qhy040404.libraryonetap.ui.dialog.ReserveDialog
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.ReserveUtils
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@Suppress("LocalVariableName")
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override fun init() = initView()

    private fun initView() {
        val textView: TextView = binding.textView
        textView.visibility = View.VISIBLE
        Thread(Detail()).start()
    }

    private inner class Detail : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            Looper.prepare()
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build()
            )

            val textView = binding.textView
            val leave = binding.button4
            val tempLeave: Button = binding.button5
            val enter: Button = binding.button6
            val imageView: ImageView = binding.imageView
            val refresh: Button = binding.button7
            val cancel: Button = binding.button10
            val reserve: Button = binding.button11
            val reset: Button = binding.button9
            val tempReset: Button = binding.button14
            val progressBar: ProgressBar = binding.progressBar

            val des = DesEncryptUtils()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            var loginSuccess = false
            var timer = 0
            var failLogin = false

            while (!loginSuccess && AppUtils.checkData(id, passwd)) {
                val ltResponse: String = Requests.get(URLManager.LIBRARY_SSO_URL)
                val ltData: String = try {
                    "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
                } catch (_: Exception) {
                    ""
                }
                val ltExecution: String = try {
                    ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
                } catch (_: Exception) {
                    ""
                }

                if (ltData != "") {
                    val rawData = "$id$passwd$ltData"
                    val rsa: String = des.strEnc(rawData, "1", "2", "3")

                    Requests.post(
                        URLManager.LIBRARY_SSO_URL,
                        Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
                        GlobalValues.ctSso
                    )
                }

                val session: String =
                    Requests.post(URLManager.LIBRARY_SESSION_URL, "", GlobalValues.ctSso)
                if (SessionData.isSuccess(session)) {
                    progressBar.post { progressBar.visibility = View.INVISIBLE }
                    loginSuccess = true
                } else {
                    timer++
                    if (timer >= 3) {
                        textView.post {
                            textView.text =
                                LibraryOneTapApp.app.getString(R.string.fail_to_login_three_times)
                        }
                        failLogin = true
                        break
                    }
                }
            }
            val list = Requests.get(URLManager.LIBRARY_ORDER_LIST_URL)
            val total = OrderListData.getTotal(list)
            if (total != "0") {
                val space_name = OrderListData.getSpace_name(list, "2")
                val seat_label = OrderListData.getSeat_label(list, "2")
                val order_date = OrderListData.getOrder_date(list, "2")
                var order_id = OrderListData.getOrder_id(list, "2")
                var order_process = OrderListData.getOrder_process(list, "2")
                val back_time =
                    OrderListData.getBack_time(list,
                        "2",
                        LibraryOneTapApp.app.getString(R.string.temp_end_time))

                if (order_id == "oid") {
                    order_id = LibraryOneTapApp.app.getString(R.string.no_valid_order)
                    reserve.post {
                        reserve.visibility = View.VISIBLE
                        reserve.isClickable = true
                    }
                }

                if (order_process == "审核通过") {
                    order_process = LibraryOneTapApp.app.getString(R.string.not_start)

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
                    order_process = LibraryOneTapApp.app.getString(R.string.inside)
                } else if (order_process == "暂离") {
                    order_process = LibraryOneTapApp.app.getString(R.string.outside)
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
                                val picture_bt = response.body!!.bytes()
                                val pictureInput = response.body!!.byteStream()
                                val bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post { imageView.setImageBitmap(bitmap) }
                                pictureInput.close()
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
                                val picture_bt = response.body!!.bytes()
                                val pictureInput = response.body!!.byteStream()
                                val bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post { imageView.setImageBitmap(bitmap) }
                                pictureInput.close()
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
                                val picture_bt = response.body!!.bytes()
                                val pictureInput = response.body!!.byteStream()
                                val bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post { imageView.setImageBitmap(bitmap) }
                                pictureInput.close()
                            }
                        })
                    }
                }
                cancel.post {
                    cancel.setOnClickListener {
                        StrictMode.setThreadPolicy(
                            StrictMode.ThreadPolicy.Builder()
                                .detectDiskReads().detectDiskWrites().detectNetwork()
                                .penaltyLog().build()
                        )
                        StrictMode.setVmPolicy(
                            StrictMode.VmPolicy.Builder()
                                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                                .penaltyLog().penaltyDeath().build()
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
                            StrictMode.ThreadPolicy.Builder()
                                .detectDiskReads().detectDiskWrites().detectNetwork()
                                .penaltyLog().build()
                        )
                        StrictMode.setVmPolicy(
                            StrictMode.VmPolicy.Builder()
                                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                                .penaltyLog().penaltyDeath().build()
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
                                        URLManager.constructAvailableUrl(
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
                                                )
                                                    .replace("\"", "")
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
                                    URLManager.LIBRART_RESERVE_FINAL_URL,
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
                            StrictMode.ThreadPolicy.Builder()
                                .detectDiskReads().detectDiskWrites().detectNetwork()
                                .penaltyLog().build()
                        )
                        StrictMode.setVmPolicy(
                            StrictMode.VmPolicy.Builder()
                                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                                .penaltyLog().penaltyDeath().build()
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
                                        URLManager.constructAvailableUrl(
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
                                                )
                                                    .replace("\"", "")
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
                                    URLManager.LIBRART_RESERVE_FINAL_URL,
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
                textView.post {
                    textView.text =
                        "order_id: $order_id\n\n$order_process\n\n$space_name\n$seat_label\n$order_date\n$back_time"
                }
            } else if (!AppUtils.checkData(id, passwd)) {
                textView.post {
                    textView.text = LibraryOneTapApp.app.getString(R.string.no_userdata)
                }
                progressBar.post { progressBar.visibility = View.INVISIBLE }
            } else if (failLogin) {
                AppUtils.pass()
            } else {
                textView.post {
                    textView.text = LibraryOneTapApp.app.getString(R.string.login_timeout)
                }
            }
            refresh.post { refresh.setOnClickListener { activity?.recreate() } }
        }
    }
}
