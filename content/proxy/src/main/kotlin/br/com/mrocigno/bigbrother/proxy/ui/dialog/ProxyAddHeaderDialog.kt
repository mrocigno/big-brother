package br.com.mrocigno.bigbrother.proxy.ui.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText
import br.com.mrocigno.bigbrother.common.R as CR

internal fun AppCompatActivity.proxyAddHeaderDialog(onSave: (String) -> Unit) =
    showDialog(
        content = R.layout.bigbrother_dialog_add_header,
        title = getString(R.string.proxy_add_header_title),
        positiveButton = getString(CR.string.add) to add@{
            val name = it?.findViewById<TextInputEditText>(R.id.proxy_header_name)
            val value = it?.findViewById<TextInputEditText>(R.id.proxy_header_value)

            val nameText = name?.text.toString().trim()
            val valueText = value?.text.toString().trim().ifBlank { "*" }

            if (nameText.isBlank()) {
                name?.setInputLayoutError(getString(R.string.proxy_required_field))
                return@add
            }

            val newData = "$nameText=$valueText"
            onSave(newData)
            dismiss()
        },
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        onView = {
            findViewById<TextInputEditText>(R.id.proxy_header_name)?.run {
                doOnTextChanged { _, _, _, _ ->
                    setInputLayoutError(null)
                }
            }
        }
    )