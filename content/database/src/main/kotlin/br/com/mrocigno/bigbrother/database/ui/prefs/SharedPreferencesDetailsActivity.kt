package br.com.mrocigno.bigbrother.database.ui.prefs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.utils.showDialog
import br.com.mrocigno.bigbrother.common.utils.toIntRound
import br.com.mrocigno.bigbrother.common.utils.toLongRound
import br.com.mrocigno.bigbrother.common.utils.trimExtension
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.database.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import br.com.mrocigno.bigbrother.common.R as CR

@OutOfDomain
internal class SharedPreferencesDetailsActivity :
    AppCompatActivity(R.layout.bigbrother_activity_shared_preferences_details) {

    private val searchLayout: TextInputLayout by lazy { findViewById(CR.id.searchable_view_layout) }
    private val search: TextInputEditText by lazy { findViewById(R.id.property_search) }
    private val recycler: RecyclerView by lazy { findViewById(CR.id.searchable_recycler_view) }
    private val toolbar: Toolbar by lazy { findViewById(CR.id.searchable_toolbar) }
    private val adapter: SharedPreferencesItemAdapter get() = recycler.adapter as SharedPreferencesItemAdapter

    private val prefsName by lazy { checkNotNull(intent.getStringExtra(PREFS_NAME_ARG)) }
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(prefsName.trimExtension(), MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.title = prefsName

        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = SharedPreferencesItemAdapter(prefs, ::onItemClick)

        search.addTextChangedListener {
            adapter.filter = it.toString().trim()
        }
    }

    private fun onItemClick(key: String, data: Any?) = when (data) {
        is String -> editString(key, data)
        is Boolean -> editBoolean(key, data)
        is Float -> editNumeric(key, data) {
            prefs.edit { putFloat(key, it.toFloat()) }
        }
        is Long -> editNumeric(key, data) {
            prefs.edit { putLong(key, it.toLongRound()) }
        }
        is Int -> editNumeric(key, data) {
            prefs.edit { putInt(key, it.toIntRound()) }
        }
        else -> {
            Toast.makeText(this, "type not editable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun <T> editNumeric(key: String, data: T, save: (String) -> Unit) {
        showDialog(
            content = R.layout.bigbrother_dialog_edit_numeric,
            onView = {
                val input = findViewById<TextInputEditText>(R.id.edit_numeric_input)
                input.setText(data.toString())
            },
            title = key,
            negativeButton = getString(CR.string.close) to { dismiss() },
            positiveButton = getString(R.string.bigbrother_prefs_save) to {
                val input = it?.findViewById<TextInputEditText>(R.id.edit_numeric_input)
                save(input?.text.toString())
                adapter.fullList = prefs.all.toList()
                dismiss()
            }
        )
    }

    private fun editString(key: String, data: String) {
        showDialog(
            content = R.layout.bigbrother_dialog_edit_string,
            onView = {
                val input = findViewById<TextInputEditText>(R.id.edit_string_input)
                input.setText(data)
            },
            title = key,
            negativeButton = getString(CR.string.close) to { dismiss() },
            positiveButton = getString(R.string.bigbrother_prefs_save) to {
                val input = it?.findViewById<TextInputEditText>(R.id.edit_string_input)
                prefs.edit { putString(key, input?.text.toString()) }
                adapter.fullList = prefs.all.toList()
                dismiss()
            }
        )
    }

    private fun editBoolean(key: String, data: Boolean) {
        showDialog(
            content = R.layout.bigbrother_dialog_edit_boolean,
            onView = {
                val view = findViewById<AppCompatCheckBox>(R.id.edit_boolean_checkbox)
                view.setOnCheckedChangeListener { _, isChecked ->
                    view.text = isChecked.toString()
                }
                view.isChecked = data
                view.text = data.toString()
            },
            title = key,
            negativeButton = getString(CR.string.close) to { dismiss() },
            positiveButton = getString(R.string.bigbrother_prefs_save) to {
                val view = it?.findViewById<AppCompatCheckBox>(R.id.edit_boolean_checkbox)
                prefs.edit { putBoolean(key, view?.isChecked == true) }
                adapter.fullList = prefs.all.toList()
                dismiss()
            }
        )
    }

    companion object {

        private const val PREFS_NAME_ARG = "bigbrother.PREFS_NAME_ARG"

        fun intent(context: Context, prefsName: String) =
            Intent(context, SharedPreferencesDetailsActivity::class.java)
                .putExtra(PREFS_NAME_ARG, prefsName)
    }
}
