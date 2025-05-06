package br.com.mrocigno.bigbrother.database.ui.prefs

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList.valueOf
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.inflate
import br.com.mrocigno.bigbrother.common.utils.isJson
import br.com.mrocigno.bigbrother.database.R
import kotlin.math.min
import br.com.mrocigno.bigbrother.common.R as CR

class SharedPreferencesItemAdapter(
    prefs: SharedPreferences,
    private val onItemClick: ((String, Any?) -> Unit)? = null
) : Adapter<SharedPreferencesItemViewHolder>() {

    var filter: String = ""
        set(value) {
            field = value
            list = fullList.filter { it.toString().contains(value) }
        }

    var fullList: List<Pair<String, Any?>> = prefs.all.toList()
        set(value) {
            list = value.filter { it.toString().contains(filter) }
            field = value
        }

    private var list: List<Pair<String, Any?>>
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
        differ.submitList(fullList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedPreferencesItemViewHolder = SharedPreferencesItemViewHolder(parent)

    override fun onBindViewHolder(holder: SharedPreferencesItemViewHolder, position: Int) {
        val (key, value) = list[position]
        holder.bind(key, value)
        holder.itemView.setOnClickListener { onItemClick?.invoke(key, value) }
        holder.itemView.setOnLongClickListener {
            it.context.copyToClipboard(
                value.toString(),
                it.context.getString(CR.string.copy_feedback, key)
            )
            true
        }
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
        value.setTextColor(context.getColor(CR.color.bb_text_paragraph))

        when (data) {
            is Boolean -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.bb_boy_red))
                type.text = "B"
            }
            is HashSet<*> -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.bb_beaver))
                type.text = "HS"
            }
            is Float -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.bb_licorice))
                type.text = "F"
            }
            is Int -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.bb_moss_green))
                type.text = "Int"
            }
            is Long -> {
                type.backgroundTintList = valueOf(context.getColor(CR.color.bb_olive_drab))
                type.text = "L"
            }
            is String -> {
                if (data.isJson()) {
                    type.backgroundTintList = valueOf(context.getColor(CR.color.bb_text_hyperlink))
                    value.setTextColor(context.getColor(CR.color.bb_text_hyperlink))
                    type.text =  "JS"
                } else {
                    type.backgroundTintList = valueOf(context.getColor(CR.color.bb_shadow))
                    type.text = "S"
                }
            }

            else -> {
                type.text =
                    if (data != null) data::class.java.simpleName
                    else "unknown"
            }
        }
    }
}
