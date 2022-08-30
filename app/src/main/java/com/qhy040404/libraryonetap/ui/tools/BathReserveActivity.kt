package com.qhy040404.libraryonetap.ui.tools

import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.databinding.ActivityBathReserveBinding
import com.qhy040404.libraryonetap.utils.AppUtils
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
        binding.toolbar.title = AppUtils.getResString(R.string.bath_title)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        binding.bathText.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            bathReserve()
        }.also {
            it.start()
        }
    }

    private fun bathReserve() {
        val spinner = binding.bathSpinner
        val reserve = binding.bathReserve
        val text = binding.bathText

        var targetRoom = 20
        ArrayAdapter.createFromResource(
            this@BathReserveActivity,
            R.array.bath_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.post { spinner.adapter = adapter }
        }

        val time = BathUtils.getBathTime()

        Requests.loginSso(URLManager.BATH_SSO_URL, GlobalValues.ctSso)

        text.post { text.text = getString(R.string.loaded) }

        reserve.post {
            reserve.setOnClickListener {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
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
                text.post { text.text = getString(R.string.request_sent) }
            }
        }

        spinner.post {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (spinner.selectedItem.toString()) {
                        "西山1楼", "West 1F" -> targetRoom = 20
                        "西山2楼", "West 2F" -> targetRoom = 21
                        "西山3楼", "West 3F" -> targetRoom = 17
                        "北山男", "North Male" -> targetRoom = 18
                        "北山女", "North Female" -> targetRoom = 19
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }
}
