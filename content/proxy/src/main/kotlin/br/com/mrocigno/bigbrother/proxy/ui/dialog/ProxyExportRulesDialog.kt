package br.com.mrocigno.bigbrother.proxy.ui.dialog

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyRuleModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import br.com.mrocigno.bigbrother.common.R as CR

internal fun Fragment.showImportRulesDialog(onSave: suspend (List<ProxyRuleModel>) -> Unit) =
    requireActivity().showDialog(
        content = R.layout.bigbrother_dialog_export_rules,
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        positiveButton = getString(CR.string.save) to click@{ view ->
            val txt = view?.findViewById<TextInputEditText>(R.id.edit_string_input) ?: run {
                dismiss()
                return@click
            }

            val list =
                runCatching { Json.decodeFromString<List<ProxyRuleModel>>(txt.text.toString().trim()) }
                    .onFailure {
                        txt.setInputLayoutError(getString(R.string.proxy_json_invalid, it.message))
                        txt.doOnTextChanged { _, _, _, _ -> txt.setInputLayoutError("") }
                        return@click
                    }.getOrThrow()

            lifecycleScope.launch {
                onSave(list)
                dismiss()
            }
        },
    ).show()

internal fun Fragment.showExportRulesDialog(json: String) =
    requireActivity().showDialog(
        content = R.layout.bigbrother_dialog_export_rules,
        positiveButton = getString(R.string.proxy_export_copy) to {
            requireContext().copyToClipboard(json)
            dismiss()
        },
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        onView = {
            findViewById<TextInputEditText>(R.id.edit_string_input)?.setText(json)
        }
    ).show()