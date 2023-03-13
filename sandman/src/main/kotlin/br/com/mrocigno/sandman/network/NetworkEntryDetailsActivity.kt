package br.com.mrocigno.sandman.network

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import br.com.mrocigno.sandman.OutOfDomain
import br.com.mrocigno.sandman.R
import br.com.mrocigno.sandman.json.JsonViewerActivity
import br.com.mrocigno.sandman.utils.getParcelableExtraCompat
import br.com.mrocigno.sandman.utils.statusBarHeight

@OutOfDomain
class NetworkEntryDetailsActivity : AppCompatActivity(R.layout.activity_network_entry) {

    private val toolbar: Toolbar by lazy { findViewById(R.id.net_entry_details_toolbar) }
    private val background: View by lazy { findViewById(R.id.net_entry_details_background) }
    private val statusCode: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_status_code) }
    private val method: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_method) }
    private val generalInfo: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_general_info) }

    private val requestHeader: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_request_headers) }
    private val requestBody: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_request_body) }

    private val responseHeader: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_response_headers) }
    private val responseBody: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_response_body) }

    private val model: NetworkEntryModel by lazy {
        intent.getParcelableExtraCompat(MODEL_ARG)
            ?: throw IllegalArgumentException("404 NetworkEntryModel not found")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupContent()
    }

    private fun setupToolbar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        toolbar.updateLayoutParams<ConstraintLayout.LayoutParams> {
            setMargins(0, statusBarHeight, 0, 0)
        }
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupContent() {
        background.byStatusCode(model.statusCode)
        statusCode.text = model.statusCode.toString()
        method.text = model.method
        generalInfo.text = model.formatInfo()

        requestHeader.text = model.request.formattedHeaders
        requestBody.text = model.request.formattedBody
        requestBody.setOnClickListener {
            startActivity(JsonViewerActivity.intent(this, requestBody.text.toString()))
        }

        responseHeader.text = model.response?.formattedHeaders
        responseBody.text = model.response?.formattedBody
        responseBody.setOnClickListener {
            startActivity(JsonViewerActivity.intent(this, responseBody.text.toString()))
        }
    }

    private fun NetworkEntryModel.formatInfo() = SpannableStringBuilder().apply {
        append(getString(R.string.network_hour_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        append("  ")
        append(hour)
        appendLine()
        append(getString(R.string.network_elapsed_time_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        append("  ")
        append(elapsedTime)
        appendLine()
        append(getString(R.string.network_url_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        append("  ")
        append(fullUrl)
    }

    companion object {

        private const val MODEL_ARG = "br.com.mrocigno.MODEL_ARG"

        fun intent(context: Context, model: NetworkEntryModel) =
            Intent(context, NetworkEntryDetailsActivity::class.java)
                .putExtra(MODEL_ARG, model)
    }
}
