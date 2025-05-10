package br.com.mrocigno.bigbrother.proxy.ui

import androidx.appcompat.app.AppCompatActivity
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText

fun AppCompatActivity.proxyAddHeaderDialog(onSave: (String) -> Unit) =
    showDialog(
        content = R.layout.bigbrother_dialog_add_header,
        title = "Adicionar Header",
        positiveButton = "Adicionar" to {
            val name = it?.findViewById<TextInputEditText>(R.id.proxy_header_name)
                ?.text.toString().trim()
                .replace(" ", "-")
            val value = it?.findViewById<TextInputEditText?>(R.id.proxy_header_value)
                ?.text.toString().trim()

            val newData = "$name=$value"
            onSave(newData)
            dismiss()
        },
        negativeButton = "Cancelar" to {
            dismiss()
        }
    )