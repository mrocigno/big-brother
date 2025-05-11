package br.com.mrocigno.bigbrother.proxy.ui

import androidx.appcompat.app.AppCompatActivity
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText
import br.com.mrocigno.bigbrother.common.R as CR

fun AppCompatActivity.proxyAddHeaderDialog(onSave: (String) -> Unit) =
    showDialog(
        content = R.layout.bigbrother_dialog_add_header,
        title = getString(R.string.proxy_add_header_title),
        positiveButton = getString(CR.string.add) to {
            val name = it?.findViewById<TextInputEditText>(R.id.proxy_header_name)
                ?.text.toString().trim()
                .replace(" ", "-")
            val value = it?.findViewById<TextInputEditText?>(R.id.proxy_header_value)
                ?.text.toString().trim()

            val newData = "$name=$value"
            onSave(newData)
            dismiss()
        },
        negativeButton = getString(CR.string.cancel) to { dismiss() }
    )