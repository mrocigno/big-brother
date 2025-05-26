package br.com.mrocigno.bigbrother.deeplink.ui

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.deeplink.R
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkEntry
import br.com.mrocigno.bigbrother.deeplink.validateQuery
import com.google.android.material.textfield.TextInputEditText
import br.com.mrocigno.bigbrother.common.R as CR

internal fun Fragment.showAppendQueryDialog(
    entry: DeeplinkEntry,
    onPositiveClick: (String) -> Unit
) {
    requireActivity().showDialog(
        content = R.layout.bigbrother_dialog_append_query,
        title = getString(R.string.bigbrother_deeplink_queries_title),
        positiveButton = getString(R.string.bigbrother_deeplink_queries_launch) to {
            it ?: return@to
            val query: TextInputEditText = it.findViewById(R.id.deeplink_append_queries)
            val finalPath: AppCompatTextView = it.findViewById(R.id.deeplink_append_final_path)

            if (validate(query)) {
                dismiss()
                onPositiveClick.invoke(finalPath.text.toString())
            }
        },
        negativeButton = getString(CR.string.cancel) to {
            dismiss()
        },
        onView = {
            val query: TextInputEditText = findViewById(R.id.deeplink_append_queries)
            val finalPath: AppCompatTextView = findViewById(R.id.deeplink_append_final_path)

            finalPath.text = entry.path
            query.doOnTextChanged { text, _, _, _ ->
                finalPath.text = buildString {
                    append(entry.path)
                    if (text.isNullOrEmpty()) return@doOnTextChanged
                    if (entry.path.contains("?")) append("&")
                    else append("?")
                    append(text.toString())
                }
            }
        }
    )
}

private fun validate(query: TextInputEditText): Boolean {
    val context = query.context
    val queryString = query.text?.toString().orEmpty()
        .takeIf { it.isNotBlank() } ?: return true

    val queryError = validateQuery(context, queryString)
    query.setInputLayoutError(queryError)
    return queryError == null
}
