package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

private const val BODY_PLACEHOLDER = "{\n\t\t\"placeholder\": \"placeholder\",\n\t\t\"placeholder\": \"placeholder\"\n}"

fun AppCompatActivity.proxyAddActionDialog() {
    val items = ProxyActions.values()
    showDialog(
        title = "Nova ação",
        content = R.layout.bigbrother_dialog_actions,
        negativeButton = "Cancelar" to { dismiss() },
        positiveButton = "Adicionar" to { dismiss() },
        onView = {
            val spinnerLayout = findViewById<TextInputLayout>(R.id.proxy_action_spinner_layout)
            val spinner = findViewById<AutoCompleteTextView>(R.id.proxy_action_spinner)
            val body = findViewById<TextInputEditText>(R.id.proxy_action_body)
            val allGroups = findViewById<View>(R.id.proxy_action_all_groups)
            val data = items.map { getString(it.label) }
            val adapter = MaterialSpinnerAdapter(context, android.R.layout.simple_list_item_1, data.toTypedArray())

            fun setupAction(action: ProxyActions) {
                allGroups.gone()
                spinner.setText(getString(action.label), false)
                spinnerLayout.helperText = getString(action.message)
                findViewById<View>(action.group)?.visible()
            }

            body.setText(BODY_PLACEHOLDER)
            spinner.setAdapter(adapter)
            spinner.setOnItemClickListener { _, _, position, _ ->
                setupAction(items[position])
            }

            setupAction(items.first())
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