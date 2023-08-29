package com.qhy040404.libraryonetap.view

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.takisoft.preferencex.EditTextPreference

@Suppress("unused")
class PasswordPreference : EditTextPreference {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes)

  init {
    if (summaryProvider is SimpleSummaryProvider) {
      summaryProvider = PasswordSummaryProvider
    }
    setOnBindEditTextListener { editText ->
      editText.inputType =
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
      editText.typeface = Typeface.DEFAULT
      text?.let { editText.setSelection(it.length) }
    }
  }

  override fun setText(text: String?) {
    return if (text?.length!! > 16) {
      super.setText(text)
    } else {
      super.setText(DesEncryptUtils.strEnc(text, "q", "h", "y"))
    }
  }

  override fun getText(): String? {
    val currentText = super.getText()
    return if (currentText == null) {
      null
    } else {
      DesEncryptUtils.strDec(currentText, "q", "h", "y")
    }
  }

  object PasswordSummaryProvider : SummaryProvider<EditTextPreference> {
    override fun provideSummary(preference: EditTextPreference): CharSequence? {
      val text = preference.text
      return if (!text.isNullOrEmpty()) {
        PasswordTransformationMethod.getInstance().getTransformation(text, null)
      } else {
        SimpleSummaryProvider.getInstance().provideSummary(preference)
      }
    }
  }
}
