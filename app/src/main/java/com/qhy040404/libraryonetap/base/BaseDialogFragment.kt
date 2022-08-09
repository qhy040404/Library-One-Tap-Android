package com.qhy040404.libraryonetap.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import com.qhy040404.libraryonetap.ui.interfaces.IFragment

abstract class BaseDialogFragment : DialogFragment(), IFragment {
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onCancelListener: DialogInterface.OnCancelListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                    this@BaseDialogFragment.onBackPressed()
                } else false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.run {
            setCanceledOnTouchOutside(isCancelable)
            setCancelable(isCancelable)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.onDismiss(dialog)
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancelListener?.onCancel(dialog)
        super.onCancel(dialog)
    }

    override fun finish() {
        activity?.finish()
    }
}