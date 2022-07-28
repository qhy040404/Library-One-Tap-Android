package com.qhy040404.libraryonetap.fragment.tools

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.ElectricData
import com.qhy040404.libraryonetap.data.NetData
import com.qhy040404.libraryonetap.data.VolunteerData
import com.qhy040404.libraryonetap.ui.tools.BathReserveActivity
import com.qhy040404.libraryonetap.ui.tools.GradesMajorActivity
import com.qhy040404.libraryonetap.ui.tools.GradesMinorActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.tools.GetPortalData
import com.qhy040404.libraryonetap.utils.tools.NetworkStateUtils
import com.qhy040404.libraryonetap.utils.tools.PermissionUtils
import com.qhy040404.libraryonetap.utils.tools.VolunteerUtils
import com.qhy040404.libraryonetap.utils.web.Requests

class ToolsInitFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.tools_list, rootKey)

        if (GlobalValues.minorDetected) {
            findPreference<Preference>(Constants.TOOLS_GRADES_MINOR)?.isVisible = true
            GlobalValues.minorVisible = true
            GlobalValues.minorDetected = false
        }

        findPreference<Preference>(Constants.TOOLS_BATH)?.apply {
            setOnPreferenceClickListener {
                val netName = when (NetworkStateUtils.checkNetworkTypeStr(requireContext())) {
                    "WIFI" -> NetworkStateUtils.getSSID(requireContext())
                    "Cellular" -> "Cellular"
                    else -> "Error"
                }

                val permission: Array<String> = arrayOf("android.permission.ACCESS_FINE_LOCATION")

                @Suppress("SpellCheckingInspection")
                if (netName == "<unknown ssid>") {
                    if (PermissionUtils.checkPermission(requireActivity(), permission)) {
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_SHORT).show()
                    }
                } else if (netName == "DLUT-LingShui") {
                    if (AppUtils.checkDataAndDialog(requireContext(),
                            GlobalValues.id,
                            GlobalValues.passwd,
                            R.string.tools,
                            R.string.no_userdata)
                    ) {
                        startActivity(Intent(requireContext(), BathReserveActivity::class.java))
                    }
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.network_env_limit)
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
                Thread(GetNet()).start()
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_ELECTRIC)?.apply {
            setOnPreferenceClickListener {
                Thread(GetElectric()).start()
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VCARD)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkDataAndDialog(requireContext(),
                        GlobalValues.id,
                        GlobalValues.passwd,
                        R.string.tools,
                        R.string.no_userdata)
                ) {
                    startActivity(Intent(requireContext(), VCardActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_VOLUNTEER)?.apply {
            setOnPreferenceClickListener {
                Thread(GetVolunteer()).start()
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_GRADES_MAJOR)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkDataAndDialog(requireContext(),
                        GlobalValues.id,
                        GlobalValues.passwd,
                        R.string.tools,
                        R.string.no_userdata)
                ) {
                    startActivity(Intent(requireContext(), GradesMajorActivity::class.java))
                }
                true
            }
        }

        findPreference<Preference>(Constants.TOOLS_GRADES_MINOR)?.apply {
            setOnPreferenceClickListener {
                if (AppUtils.checkDataAndDialog(requireContext(),
                        GlobalValues.id,
                        GlobalValues.passwd,
                        R.string.tools,
                        R.string.no_userdata)
                ) {
                    startActivity(Intent(requireContext(), GradesMinorActivity::class.java))
                }
                true
            }
        }
    }

    private inner class GetNet : Runnable {
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
            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val checked = AppUtils.checkDataAndDialog(requireContext(),
                id,
                passwd,
                R.string.tools,
                R.string.no_userdata)

            if (checked) {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()

                val data: String = GetPortalData.getPortalData(id, passwd, 1)

                val remainFee = NetData.getFee(data)
                val usedNet = NetData.getDynamicUsedFlow(data)
                val remainNet = NetData.getDynamicRemainFlow(data)
                val netMessage =
                    LibraryOneTapApp.app.getString(R.string.remain_net_fee) + remainFee + LibraryOneTapApp.app.getString(
                        R.string.rmb) + "\n" + LibraryOneTapApp.app.getString(
                        R.string.used_net
                    ) + usedNet + LibraryOneTapApp.app.getString(R.string.gigabyte) + "\n" + LibraryOneTapApp.app.getString(
                        R.string.remain_net) + remainNet + LibraryOneTapApp.app.getString(
                        R.string.gigabyte
                    )

                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(netMessage)
                    .setTitle(R.string.net_title)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setCancelable(true)
                    .create()
                    .show()
            }
            Looper.loop()
        }
    }

    private inner class GetElectric : Runnable {
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
            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            val checked = AppUtils.checkDataAndDialog(requireContext(),
                id,
                passwd,
                R.string.tools,
                R.string.no_userdata)

            if (checked) {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()

                val data: String = GetPortalData.getPortalData(id, passwd, 0)

                @Suppress("SpellCheckingInspection", "LocalVariableName")
                val SSMC = ElectricData.getSSMC(data)
                val remainElectric = ElectricData.getResele(data)
                val electricMessage =
                    SSMC + "\n" + LibraryOneTapApp.app.getString(R.string.remain_electric) + remainElectric + LibraryOneTapApp.app.getString(
                        R.string.degree
                    )

                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(electricMessage)
                    .setTitle(R.string.electric_title)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setCancelable(true)
                    .create()
                    .show()
            }
            Looper.loop()
        }
    }

    private inner class GetVolunteer : Runnable {
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
            val checked = AppUtils.checkDataAndDialog(requireContext(),
                GlobalValues.name,
                GlobalValues.id,
                R.string.tools,
                R.string.no_userdata)

            if (checked) {
                Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT).show()

                val postData =
                    VolunteerUtils.createVolunteerPostData(GlobalValues.name, GlobalValues.id)
                val data = Requests.post(URLManager.VOLTIME_POST_URL, postData, GlobalValues.ctJson)

                val sameID = VolunteerData.getSameID(data)
                val sameName = VolunteerData.getSameName(data)

                if (sameID != 1 || sameName != 1) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.find_same_data)
                        .setTitle(R.string.volunteer_title)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                } else {
                    val totalHours: String =
                        VolunteerData.getTotalHours(data).toString() +
                                LibraryOneTapApp.app.getString(R.string.hours)
                    val message = GlobalValues.name + "\n" + GlobalValues.id + "\n" + totalHours
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(message)
                        .setTitle(R.string.volunteer_title)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                }
            }
            Looper.loop()
        }
    }
}