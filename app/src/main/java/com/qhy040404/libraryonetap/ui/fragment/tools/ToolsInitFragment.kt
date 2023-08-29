package com.qhy040404.libraryonetap.ui.fragment.tools

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
import com.qhy040404.libraryonetap.data.ElectricDTO
import com.qhy040404.libraryonetap.data.NetDTO
import com.qhy040404.libraryonetap.data.VolunteerDTO
import com.qhy040404.libraryonetap.ui.tools.ExamsActivity
import com.qhy040404.libraryonetap.ui.tools.GradesActivity
import com.qhy040404.libraryonetap.ui.tools.LessonsActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.getStringAndFormat
import com.qhy040404.libraryonetap.utils.extensions.showToast
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

      val remainFee = data.decode<NetDTO>()?.fee
      val usedNet = data.decode<NetDTO>()?.dynamicUsedFlow
      val remainNet = data.decode<NetDTO>()?.dynamicRemainFlow
      val netMessage = if (remainFee != null && usedNet != null && remainNet != null) {
        R.string.tlp_net_template.getStringAndFormat(
          remainFee,
          usedNet,
          remainNet
        )
      } else {
        when (data) {
          Constants.NET_ERROR -> R.string.glb_net_error.getString()
          Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected.getString()
          Constants.NET_TIMEOUT -> R.string.glb_net_timeout.getString()
          else -> {
            if (data.contains("异常")) {
              R.string.glb_net_api_error.getString()
            } else {
              data
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

      val flag = data.decode<ElectricDTO>()?.dormitoryInfo_list?.first()?.flag

      @Suppress("SpellCheckingInspection", "LocalVariableName")
      val SSMC = data.decode<ElectricDTO>()?.dormitoryInfo_list?.first()?.SSMC
      val remainElectric = data.decode<ElectricDTO>()?.dormitoryInfo_list?.first()?.resele
      val electricMessage = if (SSMC != null && remainElectric != null) {
        R.string.tlp_electric_template.getStringAndFormat(
          SSMC,
          remainElectric
        )
      } else {
        when (data) {
          Constants.NET_ERROR -> R.string.glb_net_error.getString()
          Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected.getString()
          Constants.NET_TIMEOUT -> R.string.glb_net_timeout.getString()
          else -> {
            if (data.contains("error-code") || flag == "exception") {
              R.string.glb_net_api_error.getString()
            } else {
              data
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
      val latestDate = R.string.tlp_vol_update_time.getStringAndFormat(
        JSONObject(Requests.get(URLManager.VOLTIME_LATEST_URL)).optString("lastDate")
      )

      val sameID = data.decode<VolunteerDTO>()?.numSameID
      val sameName = data.decode<VolunteerDTO>()?.numSameName

      if (sameID == null || sameName == null) {
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
        val totalHours =
          "${data.decode<VolunteerDTO>()?.totalDuration}${R.string.hours.getString()}"
        val title = "${R.string.volunteer_title.getString()}\n$latestDate"
        val message = "${GlobalValues.name}\n${GlobalValues.id}\n$totalHours"
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
