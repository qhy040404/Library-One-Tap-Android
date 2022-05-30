package com.qhy040404.libraryonetap.fragment.tools

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.datamodel.ElectricData
import com.qhy040404.libraryonetap.datamodel.NetData
import com.qhy040404.libraryonetap.datamodel.VolunteerData
import com.qhy040404.libraryonetap.ui.tools.BathReserveActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.tools.GetPortalData
import com.qhy040404.libraryonetap.utils.tools.NetworkStateUtils
import com.qhy040404.libraryonetap.utils.tools.PermissionUtils
import com.qhy040404.libraryonetap.utils.tools.VolunteerUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import com.tencent.bugly.crashreport.BuglyLog

class ToolsInitNewFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.tools_list, rootKey)

        findPreference<Preference>(Constants.TOOLS_BATH)?.apply {
            setOnPreferenceClickListener {
                val netName = when (NetworkStateUtils().checkNetworkTypeStr(requireContext())) {
                    "WIFI" -> NetworkStateUtils().getSSID(requireContext())
                    "Cellular" -> "Cellular"
                    else -> "Error"
                }

                val permission: Array<String> = arrayOf("android.permission.ACCESS_FINE_LOCATION")

                if (netName == "<unknown ssid>") {
                    if (PermissionUtils().checkPermission(requireActivity(), permission)) {
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
                    }
                } else if (netName == "DLUT-LingShui") {
                    startActivity(Intent(requireContext(), BathReserveActivity::class.java))
                } else {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.networkLimit)
                        .setTitle(R.string.bath_title)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_NET)?.apply {
            setOnPreferenceClickListener {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()
                Thread(getNet()).start()
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_ELECTRIC)?.apply {
            setOnPreferenceClickListener {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()
                Thread(getElectric()).start()
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VCARD)?.apply {
            setOnPreferenceClickListener {
                startActivity(Intent(requireContext(),VCardActivity::class.java))
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VOLUNTEER)?.apply {
            setOnPreferenceClickListener {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()
                Thread(getVolunteer()).start()
                true
            }
        }
    }

    private inner class getNet : Runnable {
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
            val netData = NetData()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val data: String = GetPortalData.getPortalData(id, passwd, 1)

            BuglyLog.d("netOriginalData", data)

            val remainFee = netData.getFee(data)
            val usedNet = netData.getDynamicUsedFlow(data)
            val remainNet = netData.getDynamicRemainFlow(data)
            val netMessage =
                getString(R.string.remainNetFeeAndColon) + remainFee + getString(R.string.rmb) + "\n" + getString(
                    R.string.usedNetAndColon
                ) + usedNet + getString(R.string.gigabyte) + "\n" + getString(R.string.remainNetAndColon) + remainNet + getString(
                    R.string.gigabyte
                )

            AlertDialog.Builder(requireContext())
                .setMessage(netMessage)
                .setTitle(R.string.remainNet)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }

    private inner class getElectric : Runnable {
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
            val electricData = ElectricData()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val data: String = GetPortalData.getPortalData(id, passwd, 0)

            BuglyLog.d("elecOriginalData", data)

            val SSMC = electricData.getSSMC(data)
            val remainElectric = electricData.getResele(data)
            val electricMessage =
                SSMC + "\n" + getString(R.string.remainElectricAndColon) + remainElectric + getString(
                    R.string.degree
                )

            AlertDialog.Builder(requireContext())
                .setMessage(electricMessage)
                .setTitle(R.string.remainElectric)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }

    private inner class getVolunteer : Runnable {
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
            val postData =
                VolunteerUtils.createVolunteerPostData(GlobalValues.name, GlobalValues.id)
            val data = Requests().post(URLManager.VOLTIME_POST_URL, postData, GlobalValues.ctJson)

            val sameID = VolunteerData().getSameID(data)
            val sameName = VolunteerData().getSameName(data)

            if (sameID != 1 || sameName != 1) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.sameData)
                    .setTitle(R.string.volunteer_title)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setCancelable(true)
                    .create()
                    .show()
            } else {
                val totalHours: String =
                    VolunteerData().getTotalHours(data).toString() + getString(R.string.hours)

                val message = GlobalValues.name + "\n" + GlobalValues.id + "\n" + totalHours
                AlertDialog.Builder(requireContext())
                    .setMessage(message)
                    .setTitle(R.string.volunteer_title)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setCancelable(true)
                    .create()
                    .show()
            }
            Looper.loop()
        }
    }
}