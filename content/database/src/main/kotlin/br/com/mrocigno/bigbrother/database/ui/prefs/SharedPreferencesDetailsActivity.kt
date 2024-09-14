package br.com.mrocigno.bigbrother.database.ui.prefs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList.valueOf
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.common.utils.startsWith
import br.com.mrocigno.bigbrother.common.utils.trimExtension
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.database.R
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.min
import br.com.mrocigno.bigbrother.common.R as CR

@OutOfDomain
class SharedPreferencesDetailsActivity :
    AppCompatActivity(R.layout.bigbrother_activity_shared_preferences_details) {

    private val searchLayout: TextInputLayout by lazy { findViewById(CR.id.searchable_view_layout) }
    private val recycler: RecyclerView by lazy { findViewById(CR.id.searchable_recycler_view) }
    private val toolbar: Toolbar by lazy { findViewById(CR.id.searchable_toolbar) }
    private val adapter: SharedPreferencesItemAdapter get() = recycler.adapter as SharedPreferencesItemAdapter

    private val prefsName by lazy { checkNotNull(intent.getStringExtra(PREFS_NAME_ARG)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.title = prefsName

        val prefs = getSharedPreferences(prefsName.trimExtension(), MODE_PRIVATE)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = SharedPreferencesItemAdapter(prefs)
        prefs.registerOnSharedPreferenceChangeListener { sharedPreferences, _ ->
            adapter.list = sharedPreferences.all.toList()
        }
    }

    companion object {

        private const val PREFS_NAME_ARG = "bigbrother.PREFS_NAME_ARG"

        fun intent(context: Context, prefsName: String) =
            Intent(context, SharedPreferencesDetailsActivity::class.java)
                .putExtra(PREFS_NAME_ARG, prefsName)
    }
}

class SharedPreferencesItemAdapter(
    prefs: SharedPreferences
) : Adapter<SharedPreferencesItemViewHolder>() {

    var list: List<Pair<String, Any?>>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    private val differ = AsyncListDiffer(this, object : ItemCallback<Pair<String, Any?>>() {
        override fun areContentsTheSame(
            oldItem: Pair<String, Any?>,
            newItem: Pair<String, Any?>
        ) = oldItem == newItem

        override fun areItemsTheSame(
            oldItem: Pair<String, Any?>,
            newItem: Pair<String, Any?>
        ) = oldItem.first == newItem.first
    })

    init {
        list = prefs.all.toList()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedPreferencesItemViewHolder = SharedPreferencesItemViewHolder(parent)

    override fun onBindViewHolder(holder: SharedPreferencesItemViewHolder, position: Int) {
        val (key, value) = list[position]
        holder.bind(key, value)
    }

    override fun getItemCount(): Int = list.size
}

class SharedPreferencesItemViewHolder(
    parent: ViewGroup
) : ViewHolder(parent.inflate(R.layout.bigbrother_item_shared_preferences)) {

    private val title: AppCompatTextView by lazy { itemView.findViewById(R.id.prefs_item_title) }
    private val value: AppCompatTextView by lazy { itemView.findViewById(R.id.prefs_item_value) }
    private val type: AppCompatTextView by lazy { itemView.findViewById(R.id.prefs_item_type) }

    private val context: Context get() = itemView.context

    fun bind(key: String, data: Any?) {
        val valueString = data.toString().let { str ->
            str.substring(0, min(str.length, 200))
                .lines()
                .joinToString(" ") { it.trim() }
        }

        title.text = key
        value.text = valueString
        value.setTextColor(context.getColor(CR.color.text_paragraph))

        type.text = when (data) {
            is Boolean -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.boy_red))
                "B"
            }

            is HashSet<*> -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.beaver))
                "HS"
            }

            is Float -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.licorice))
                "F"
            }

            is Int -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.moss_green))
                "Int"
            }

            is Long -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.olive_drab))
                "L"
            }

            is String -> {
                if (data.trim().startsWith('{', ']')) {
                    type.backgroundTintList = valueOf(context.getColor(CR.color.text_hyperlink))
                    value.setTextColor(context.getColor(CR.color.text_hyperlink))
                    "JS"
                } else {
                    type.backgroundTintList = valueOf(context.getColor(CR.color.shadow))
                    "S"
                }
            }

            else -> {
                if (data != null) data::class.java.simpleName
                else "unknown"
            }
        }
    }
}
