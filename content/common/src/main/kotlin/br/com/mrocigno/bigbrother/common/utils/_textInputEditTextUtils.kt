package br.com.mrocigno.bigbrother.common.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.setInputLayoutError(error: String?) {
    (parent.parent as? TextInputLayout)?.error = error
}