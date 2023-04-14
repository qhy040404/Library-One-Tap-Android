package com.qhy040404.libraryonetap.ui.fragment.tools

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.ElectricData
import com.qhy040404.libraryonetap.data.NetData
import com.qhy040404.libraryonetap.data.VolunteerData
import com.qhy040404.libraryonetap.ui.tools.BathReserveActivity
import com.qhy040404.libraryonetap.ui.tools.ExamsActivity
import com.qhy040404.libraryonetap.ui.tools.GradesActivity
import com.qhy040404.libraryonetap.ui.tools.LessonsActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.NetworkStateUtils
import com.qhy040404.libraryonetap.utils.PermissionUtils
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.tools.BathUtils
import com.qhy040404.libraryonetap.utils.tools.GetPortalData
import com.qhy040404.libraryonetap.utils.tools.VolunteerUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ToolsInitFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.tools_list, rootKey)

        findPreference<Preference>(Constants.TOOLS_BATH)?.apply {
            if (!BathUtils.isBathTimeValid()) {
                isVisible = false
            }
            setOnPreferenceClickListener {
                if (AppUtils.checkData(
                        GlobalValues.id,
                        GlobalValues.passwd,
                        requireContext(),
                        R.string.mn_tools,
                        R.string.glb_no_userdata
                    )
                ) {
                    val netName = when (NetworkStateUtils.checkNetworkType()) {
                        "WIFI" -> NetworkStateUtils.getSSID()
                        "Cellular" -> "Cellular"
                        else -> Constants.GLOBAL_ERROR
                    }

                    @Suppress("SpellCheckingInspection")
                    if (netName == "<unknown ssid>") {
                        if (PermissionUtils.checkPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                childFragmentManager,
                                R.string.br_permission_prompt
                            )
                        ) {
                            requireContext().showToast(R.string.glb_error)
                        }
                    } else {
                        startActivity(Intent(requireContext(), BathReserveActivity::class.java))
                    }
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_NET)?.apply {
            setOnPreferenceClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    getNet()
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_ELECTRIC)?.apply {
            setOnPreferenceClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    getElectric()
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VCARD)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkData(
                        GlobalValues.id,
                        GlobalValues.passwd,
                        requireContext(),
                        R.string.mn_tools,
                        R.string.glb_no_userdata
                    )
                ) {
                    startActivity(Intent(requireContext(), VCardActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VOLUNTEER)?.apply {
            setOnPreferenceClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    getVolunteer()
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_GRADES_MAJOR)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkData(
                        GlobalValues.id,
                        GlobalValues.passwd,
                        requireContext(),
                        R.string.mn_tools,
                        R.string.glb_no_userdata
                    )
                ) {
                    startActivity(Intent(requireContext(), GradesActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_LESSONS)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkData(
                        GlobalValues.id,
                        GlobalValues.passwd,
                        requireContext(),
                        R.string.mn_tools,
                        R.string.glb_no_userdata
                    )
                ) {
                    startActivity(Intent(requireContext(), LessonsActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_EXAMS)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkData(
                        GlobalValues.id,
                        GlobalValues.passwd,
                        requireContext(),
                        R.string.mn_tools,
                        R.string.glb_no_userdata
                    )
                ) {
                    startActivity(Intent(requireContext(), ExamsActivity::class.java))
                }
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (BathUtils.isTimeNearValidTime()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val current = BathUtils.isBathTimeValid()
                while (isVisible) {
                    if (BathUtils.isBathTimeValid() != current) {
                        withContext(Dispatchers.Main) {
                            findPreference<Preference>(Constants.TOOLS_BATH)?.isVisible =
                                BathUtils.isBathTimeValid()
                        }
                        break
                    }
                }
            }
        }
    }

    private suspend fun getNet() {
        if (!AppUtils.hasNetwork()) {
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.glb_net_disconnected)
                    .setTitle(R.string.net_title)
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
            return
        }

        if (checkIdPass()) {
            withContext(Dispatchers.Main) {
                requireContext().showToast(R.string.glb_loading)
            }

            val data = GetPortalData.getPortalData(1)

            val remainFee = NetData.getFee(data)
            val usedNet = NetData.getDynamicUsedFlow(data)
            val remainNet = NetData.getDynamicRemainFlow(data)
            val netMessage = if (!AppUtils.isError(remainFee, usedNet, remainNet)) {
                R.string.tlp_remain_net_fee.getString() + remainFee +
                    R.string.rmb.getString() + "\n" +
                    R.string.tlp_used_net.getString() + usedNet +
                    R.string.gigabyte.getString() + "\n" +
                    R.string.tlp_remain_net.getString() + remainNet +
                    R.string.gigabyte.getString()
            } else {
                when (data) {
                    Constants.NET_ERROR -> R.string.glb_net_error.getString()
                    Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected.getString()
                    Constants.NET_TIMEOUT -> R.string.glb_net_timeout.getString()
                    else -> {
                        if (data.contains("异常")) {
                            R.string.glb_net_api_error.getString()
                        } else {
                            R.string.glb_fail_to_login_three_times.getString()
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(netMessage)
                    .setTitle(R.string.net_title)
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }
    }

    private suspend fun getElectric() {
        if (!AppUtils.hasNetwork()) {
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.glb_net_disconnected)
                    .setTitle(R.string.electric_title)
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
            return
        }

        if (checkIdPass()) {
            withContext(Dispatchers.Main) {
                requireContext().showToast(R.string.glb_loading)
            }

            val data = GetPortalData.getPortalData(0)

            @Suppress("SpellCheckingInspection", "LocalVariableName")
            val SSMC = ElectricData.getSSMC(data)
            val remainElectric = ElectricData.getResele(data)
            val electricMessage = if (!AppUtils.isError(SSMC, remainElectric)) {
                SSMC + "\n" +
                    if (remainElectric != Constants.API_ERROR) {
                        R.string.tlp_remain_electric.getString() + remainElectric + R.string.degree.getString()
                    } else {
                        R.string.glb_net_api_error.getString()
                    }
            } else {
                when (data) {
                    Constants.NET_ERROR -> R.string.glb_net_error.getString()
                    Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected.getString()
                    Constants.NET_TIMEOUT -> R.string.glb_net_timeout.getString()
                    else -> {
                        if (data.contains("error-code")) {
                            R.string.glb_net_api_error.getString()
                        } else {
                            R.string.glb_fail_to_login_three_times.getString()
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(electricMessage)
                    .setTitle(R.string.electric_title)
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }
    }

    private suspend fun getVolunteer() {
        if (!AppUtils.hasNetwork()) {
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.glb_net_disconnected)
                    .setTitle(R.string.volunteer_title)
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create()
                    .show()
            }
            return
        }

        if (checkIdPass(true)) {
            withContext(Dispatchers.Main) {
                requireContext().showToast(R.string.glb_loading)
            }

            val postData =
                VolunteerUtils.createVolunteerPostData(GlobalValues.name, GlobalValues.id)
            val data = Requests.post(URLManager.VOLTIME_POST_URL, postData, GlobalValues.ctJson)
            val latestDate =
                R.string.tlp_vol_update_time.getString() +
                    JSONObject(Requests.get(URLManager.VOLTIME_LATEST_URL)).optString("lastDate")

            val sameID = VolunteerData.getSameID(data)
            val sameName = VolunteerData.getSameName(data)

            if (sameID == -1 || sameName == -1) {
                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(
                            when (data) {
                                Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected
                                Constants.NET_ERROR -> R.string.glb_net_error
                                Constants.NET_TIMEOUT -> R.string.glb_net_timeout
                                else -> R.string.glb_unknown_error
                            }
                        )
                        .setTitle(R.string.volunteer_title)
                        .setPositiveButton(R.string.glb_ok, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            } else if (sameID == 0 || sameName == 0) {
                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.tlp_invalid_id_or_name)
                        .setTitle(R.string.volunteer_title)
                        .setPositiveButton(R.string.glb_ok, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            } else if (sameID != 1 || sameName != 1) {
                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.tlp_find_same_data)
                        .setTitle(R.string.volunteer_title)
                        .setPositiveButton(R.string.glb_ok, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            } else {
                val totalHours = VolunteerData.getTotalHours(data).toString() +
                    R.string.hours.getString()
                val title = R.string.volunteer_title.getString() + "\n" + latestDate
                val message = GlobalValues.name + "\n" + GlobalValues.id + "\n" + totalHours
                withContext(Dispatchers.Main) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(message)
                        .setTitle(title)
                        .setPositiveButton(R.string.glb_ok, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            }
        }
    }

    private suspend fun checkIdPass(isVolunteer: Boolean = false): Boolean {
        return withContext(Dispatchers.Main) {
            AppUtils.checkData(
                GlobalValues.id,
                if (!isVolunteer) {
                    GlobalValues.passwd
                } else {
                    GlobalValues.name
                },
                requireContext(),
                R.string.mn_tools,
                R.string.glb_no_userdata
            )
        }
    }
}
