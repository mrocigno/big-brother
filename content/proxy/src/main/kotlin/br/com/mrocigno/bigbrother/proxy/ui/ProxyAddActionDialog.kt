package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

internal const val BODY_PLACEHOLDER = "{\n\t\t\"placeholder\": \"placeholder\",\n\t\t\"placeholder\": \"placeholder\"\n}"

internal fun AppCompatActivity.proxyAddActionDialog(onResult: (ProxyActionModel) -> Unit) {
    val items = ProxyActions.values()
    showDialog(
        title = "Nova ação",
        content = R.layout.bigbrother_dialog_actions,
        negativeButton = "Cancelar" to { dismiss() },
        positiveButton = "Adicionar" to { view ->
            val spinner = view!!.findViewById<AutoCompleteTextView>(R.id.proxy_action_spinner)
            val name = view.findViewById<TextInputEditText>(R.id.proxy_action_name)
            val value = view.findViewById<TextInputEditText>(R.id.proxy_action_value)
            val body = view.findViewById<TextInputEditText>(R.id.proxy_action_body)

            val action = items[spinner.tag as Int]

            if (action.validate(name, value, body)) {
                onResult(
                    ProxyActionModel(
                        action = action,
                        name = name.text.toString().trim(),
                        value = value.text.toString().trim(),
                        body = body.text.toString().trim()
                            .takeIf { action == ProxyActions.SET_BODY }
                    )
                )
                dismiss()
            }
        },
        onView = {
            val spinnerLayout = findViewById<TextInputLayout>(R.id.proxy_action_spinner_layout)
            val spinner = findViewById<AutoCompleteTextView>(R.id.proxy_action_spinner)
            val body = findViewById<TextInputEditText>(R.id.proxy_action_body)
            val allGroups = findViewById<View>(R.id.proxy_action_all_groups)
            val data = items.map { getString(it.label) }
            val adapter = MaterialSpinnerAdapter(context, android.R.layout.simple_list_item_1, data.toTypedArray())

            fun setupAction(position: Int) {
                val action = items[position]
                allGroups.gone()
                spinner.tag = position
                spinner.setText(getString(action.label), false)
                spinnerLayout.helperText = getString(action.message)
                findViewById<View>(action.group)?.visible()
            }

            body.setText(BODY_PLACEHOLDER)
            body.doOnTextChanged { _, _, _, _ -> body.setInputLayoutError(null) }
            spinner.setAdapter(adapter)
            spinner.setOnItemClickListener { _, _, position, _ ->
                setupAction(position)
            }

            setupAction(0)
        }
    )
}

class MaterialSpinnerAdapter<T>(
    context: Context,
    layout: Int,
    var values: Array<T>
) : ArrayAdapter<T>(context, layout, values) {

    override fun getFilter() = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}