package com.qhy040404.libraryonetap.ui.tools

import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.databinding.ActivityBathReserveBinding
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.tools.BathUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BathReserveActivity : BaseActivity<ActivityBathReserveBinding>() {
    override fun init() = initView()

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.bath_title)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        val textViewBath: TextView = findViewById(R.id.textView3)
        textViewBath.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            bathReserve()
        }.also {
            it.start()
        }
    }

    private fun bathReserve() {
        val spinner: Spinner = findViewById(R.id.spinner2)
        val reserve: Button = findViewById(R.id.button15)
        val textViewBath: TextView = findViewById(R.id.textView3)

        var targetRoom = 20
        ArrayAdapter.createFromResource(
            this@BathReserveActivity,
            R.array.bath_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.post { spinner.adapter = adapter }
        }

        val des = DesEncryptUtils()

        val id: String = GlobalValues.id
        val passwd: String = GlobalValues.passwd

        val time = BathUtils.getBathTime()

        val ltResponse: String = Requests.get(URLManager.BATH_SSO_URL)
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

        val rawData = "$id$passwd$ltData"
        val rsa: String = des.strEnc(rawData, "1", "2", "3")

        Requests.post(
            URLManager.BATH_SSO_URL,
            Requests.loginPostData(id, passwd, ltData, rsa, ltExecution),
            GlobalValues.ctSso
        )

        textViewBath.post { textViewBath.text = getString(R.string.loaded) }

        reserve.post {
            reserve.setOnClickListener {
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
                @Suppress("SpellCheckingInspection")
                val savePostData = "mealorder=0&goodsid=$targetRoom&goodsnum=1&addlocation=1"

                @Suppress("SpellCheckingInspection")
                val cartPostData = "goodsShopcarId=$targetRoom&rulesid=$time"

                @Suppress("SpellCheckingInspection")
                val mainPostData = "goodsid=$targetRoom%2C&ruleid=$time"

                @Suppress("SpellCheckingInspection")
                val payPostData = "goodis=$targetRoom&payway=nopay"

                Requests.post(URLManager.BATH_SAVE_CART_URL, savePostData, GlobalValues.ctSso)
                Requests.post(URLManager.BATH_UPDATE_CART_URL, cartPostData, GlobalValues.ctSso)
                Requests.post(URLManager.BATH_MAIN_FUNC_URL, mainPostData, GlobalValues.ctSso)
                Requests.post(URLManager.BATH_PAY_URL, payPostData, GlobalValues.ctSso)
                textViewBath.post { textViewBath.text = getString(R.string.request_sent) }
            }
        }

        spinner.post {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (spinner.selectedItem.toString()) {
                        "??????1???", "West 1F" -> targetRoom = 20
                        "??????2???", "West 2F" -> targetRoom = 21
                        "??????3???", "West 3F" -> targetRoom = 17
                        "?????????", "North Male" -> targetRoom = 18
                        "?????????", "North Female" -> targetRoom = 19
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}