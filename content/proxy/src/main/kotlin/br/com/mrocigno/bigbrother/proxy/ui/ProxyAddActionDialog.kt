package br.com.mrocigno.bigbrother.proxy.ui

import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import br.com.mrocigno.bigbrother.common.helpers.SimpleSpinnerAdapter
import br.com.mrocigno.bigbrother.common.utils.DialogButtonClick
import br.com.mrocigno.bigbrother.common.utils.gone
import br.com.mrocigno.bigbrother.common.utils.setInputLayoutError
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.model.ProxyActionModel
import br.com.mrocigno.bigbrother.proxy.model.ProxyActions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR
import com.google.android.material.R as MR

private const val BODY_PLACEHOLDER = "{\n\t\t\"placeholder1\": \"placeholder\",\n\t\t\"placeholder2\": \"placeholder\"\n}"

internal fun AppCompatActivity.proxyAddActionDialog(
    onSave: (new: ProxyActionModel) -> Unit
) {
    val items = ProxyActions.values()
    showDialog(
        title = getString(R.string.proxy_add_action_title),
        content = R.layout.bigbrother_dialog_actions,
        negativeButton = getString(CR.string.cancel) to { dismiss() },
        positiveButton = getString(CR.string.add) to getPositiveButtonClick(items, onSave),
        onView = { setupDialog(null, items) }
    )
}

internal fun AppCompatActivity.proxyUpdateActionDialog(
    old: ProxyActionModel,
    onDelete: (old: ProxyActionModel) -> Unit,
    onSave: (old: ProxyActionModel, new: ProxyActionModel) -> Unit
) {
    val items = ProxyActions.values()
    showDialog(
        title = getString(R.string.proxy_edit_action_title),
        content = R.layout.bigbrother_dialog_actions,
        negativeButton = getString(CR.string.delete) to {
            onDelete.invoke(old)
            dismiss()
        },
        negativeButtonColor = MR.color.design_default_color_error,
        positiveButton = getString(CR.string.save) to getPositiveButtonClick(items) {
            onSave.invoke(old, it.copy(id = old.id))
        },
        onView = { setupDialog(old, items) }
    )
}

private fun View.setupDialog(old: ProxyActionModel?, items: Array<ProxyActions>) {
    val spinnerLayout = findViewById<TextInputLayout>(R.id.proxy_action_spinner_layout)
    val spinner = findViewById<AutoCompleteTextView>(R.id.proxy_action_spinner)
    val name = findViewById<TextInputEditText>(R.id.proxy_action_name)
    val value = findViewById<TextInputEditText>(R.id.proxy_action_value)
    val body = findViewById<TextInputEditText>(R.id.proxy_action_body)
    val allGroups = findViewById<View>(R.id.proxy_action_all_groups)
    val data = items.map { context.getString(it.label) }
    val adapter = SimpleSpinnerAdapter(context, data.toTypedArray())

    fun setupAction(position: Int) {
        val action = items[position]
        allGroups.gone()
        spinner.tag = position
        spinner.setText(context.getString(action.label), false)
        spinnerLayout.helperText = context.getString(action.message)
        findViewById<View>(action.group)?.visible()
    }

    name.setText(old?.name)
    name.doOnTextChanged { _, _, _, _ -> name.setInputLayoutError(null) }
    value.setText(old?.value)
    value.doOnTextChanged { _, _, _, _ -> value.setInputLayoutError(null) }
    body.setText(old?.body ?: BODY_PLACEHOLDER)
    body.doOnTextChanged { _, _, _, _ -> body.setInputLayoutError(null) }
    spinner.setAdapter(adapter)
    spinner.setOnItemClickListener { _, _, position, _ ->
        setupAction(position)
    }

    doOnLayout {
        val initial = old?.action?.ordinal ?: 0
        setupAction(initial)
    }
}

private fun getPositiveButtonClick(
    items: Array<ProxyActions>,
    onClick: (new: ProxyActionModel) -> Unit
): DialogButtonClick = { view ->
    val spinner = view!!.findViewById<AutoCompleteTextView>(R.id.proxy_action_spinner)
    val name = view.findViewById<TextInputEditText>(R.id.proxy_action_name)
    val value = view.findViewById<TextInputEditText>(R.id.proxy_action_value)
    val body = view.findViewById<TextInputEditText>(R.id.proxy_action_body)

    val action = items[spinner.tag as Int]

    if (action.validate(name, value, body)) {
        onClick(
            ProxyActionModel(
                action = action,
                name = name.text.toString().trim(),
                value = value.text.toString().trim(),
                body = body.text.toString().trim().takeIf { action == ProxyActions.SET_BODY }
            )
        )
        dismiss()
    }
}