package br.com.mrocigno.bigbrother.deeplink.ui

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkType
import br.com.mrocigno.bigbrother.deeplink.validateQuery
import com.google.android.material.textfield.TextInputEditText
import br.com.mrocigno.bigbrother.common.R as CR

internal fun Fragment.showNewLinkDialog(
    onPositiveClick: (DeeplinkEntry) -> Unit
) {
    requireActivity().showDialog(
        content = R.layout.bigbrother_dialog_new_deeplink,
        title = getString(R.string.bigbrother_deeplink_new_title),
        positiveButton = getString(R.string.bigbrother_deeplink_queries_launch) to {
            it ?: return@to
            val scheme: TextInputEditText = it.findViewById(R.id.deeplink_new_scheme)
            val query: TextInputEditText = it.findViewById(R.id.deeplink_new_queries)
            val finalPath: AppCompatTextView = it.findViewById(R.id.deeplink_new_final_path)

            if (validate(scheme, query)) {
                dismiss()
                onPositiveClick.invoke(
                    DeeplinkEntry(
                        exported = true,
                        type = DeeplinkType.UNKNOWN,
                        path = finalPath.text.toString()
                    )
                )
            }
        },
        negativeButton = getString(CR.string.cancel) to {
            dismiss()
        },
        onView = {
            val scheme: TextInputEditText = findViewById(R.id.deeplink_new_scheme)
            val host: TextInputEditText = findViewById(R.id.deeplink_new_host)
            val path: TextInputEditText = findViewById(R.id.deeplink_new_path)
            val query: TextInputEditText = findViewById(R.id.deeplink_new_queries)
            val finalPath: AppCompatTextView = findViewById(R.id.deeplink_new_final_path)

            fun updateFinalPath() {
                finalPath.text = buildString {
                    if (!scheme.text.isNullOrEmpty()) {
                        append(scheme.text.toString())
                        append("://")
                    }
                    if (!host.text.isNullOrEmpty()) {
                        append(host.text.toString())
                    }
                    if (!path.text.isNullOrEmpty()) {
                        append("/")
                        append(path.text.toString())
                    }
                    if (!query.text.isNullOrEmpty()) {
                        append("?")
                        append(query.text.toString())
                    }
                }
            }

            finalPath.text = "N/A"
            scheme.doOnTextChanged { _, _, _, _ -> updateFinalPath() }
            host.doOnTextChanged { _, _, _, _ -> updateFinalPath() }
            path.doOnTextChanged { _, _, _, _ -> updateFinalPath() }
            query.doOnTextChanged { _, _, _, _ -> updateFinalPath() }
        }
    )
}

private fun validate(
    scheme: TextInputEditText,
    query: TextInputEditText
): Boolean {
    val context = scheme.context
    val schemeError = buildString {
        if (scheme.text.isNullOrEmpty()) appendLine(context.getString(R.string.bigbrother_deeplink_error_scheme))
    }.takeIf { it.isNotBlank() }
    val queryError = query.text?.toString()?.takeIf { it.isNotBlank() }
        ?.let {
            validateQuery(context, it)
        }

    query.setInputLayoutError(queryError)
    scheme.setInputLayoutError(schemeError)

    return queryError == null && schemeError == null
}
