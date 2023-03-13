package br.com.mrocigno.sandman.json

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.sandman.OutOfDomain
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.utils.disableChangeAnimation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

@OutOfDomain
class JsonViewerActivity : AppCompatActivity(R.layout.activity_json_viewer) {

    private val searchLayout: TextInputLayout by lazy { findViewById(R.id.json_viewer_search_view_layout) }
    private val searchView: TextInputEditText by lazy { findViewById(R.id.json_viewer_search_view) }
    private val recycler: RecyclerView by lazy { findViewById(R.id.json_viewer_recycler) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.json_viewer_toolbar) }

    private val adapter get() = recycler.adapter as JsonViewerAdapter

    private val json: List<JsonViewerModel> by lazy {
        val extra = intent.getStringExtra(JSON_ARG) ?: throw IllegalArgumentException("the JSON_ARG cannot be null")

        runCatching { JSONObject(extra) }.onSuccess { return@lazy it.toListModel(0) }
        runCatching { JSONArray(extra) }.onSuccess { return@lazy it.toListModel(0) }
        throw IllegalArgumentException("the string provided is not a JSON")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        searchLayout.setEndIconOnClickListener {
            Toast.makeText(this, "click!", Toast.LENGTH_LONG).show()
        }

        recycler.disableChangeAnimation()
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        try {
            recycler.adapter = JsonViewerAdapter(json.toMutableList())
        } catch (e: IllegalArgumentException) {
            finish()
        }

        searchView.addTextChangedListener {
            val search = it?.toString()?.trim() ?: return@addTextChangedListener
            adapter.setSearch(search)
        }
    }

    companion object {

        private const val JSON_ARG = "br.com.mrocigno.JSON_ARG"

        fun intent(context: Context, json: String) =
            Intent(context, JsonViewerActivity::class.java)
                .putExtra(JSON_ARG, json)
    }
}