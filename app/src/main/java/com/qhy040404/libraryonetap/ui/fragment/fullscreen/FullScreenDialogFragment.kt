package com.qhy040404.libraryonetap.ui.fragment.fullscreen

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.view.FullScreenDialogView

class FullScreenDialogFragment(private val message: String) : DialogFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    LibraryOneTapApp.instance?.addFragment(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return FullScreenDialogView(requireContext()).apply {
      detail.text = message
    }
  }

  override fun onResume() {
    super.onResume()
    dialog?.apply {
      run {
        setCanceledOnTouchOutside(isCancelable)
        setCancelable(isCancelable)
      }
      window?.run {
        attributes = attributes?.apply {
          width = ViewGroup.LayoutParams.MATCH_PARENT
          height = ViewGroup.LayoutParams.MATCH_PARENT
          gravity = Gravity.BOTTOM
        }
      }
    }
  }

  override fun isCancelable() = false
}
