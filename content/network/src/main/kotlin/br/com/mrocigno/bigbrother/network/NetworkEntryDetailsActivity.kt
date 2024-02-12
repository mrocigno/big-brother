package br.com.mrocigno.bigbrother.network

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.getSerializableExtraCompat
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.network.json.JsonViewerActivity

@OutOfDomain
class NetworkEntryDetailsActivity : AppCompatActivity(R.layout.bigbrother_activity_network_entry) {

    private val toolbar: Toolbar by lazy { findViewById(R.id.net_entry_details_toolbar) }
    private val background: View by lazy { findViewById(R.id.net_entry_details_background) }
    private val statusCode: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_status_code) }
    private val method: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_method) }
    private val generalInfo: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_general_info) }
    private val copyAll: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_copy_all) }

    private val copyRequestHeader: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_request_copy_headers) }
    private val requestHeader: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_request_headers) }
    private val copyRequestBody: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_request_copy_body) }
    private val searchRequestBody: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_request_search_body) }
    private val requestBody: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_request_body) }

    private val copyResponseHeader: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_response_copy_headers) }
    private val responseHeader: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_response_headers) }
    private val copyResponseBody: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_response_copy_body) }
    private val searchResponseBody: AppCompatImageView by lazy { findViewById(R.id.net_entry_details_response_search_body) }
    private val responseBody: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_response_body) }

    private val model: NetworkEntryModel by lazy {
        intent.getSerializableExtraCompat(MODEL_ARG)
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
        copyAll.setOnClickListener { copyToClipboard(model.toCURL()) }

        requestHeader.text = model.request.formattedHeaders
        requestBody.text = model.request.formattedBody
        copyRequestHeader.setOnClickListener { copyToClipboard(requestHeader.text.toString()) }
        copyRequestBody.setOnClickListener { copyToClipboard(requestBody.text.toString()) }
        if (model.request.isBodyFormatted) {
            searchRequestBody.isVisible = true
            searchRequestBody.setOnClickListener {
                startActivity(JsonViewerActivity.intent(this, requestBody.text.toString()))
            }
        }

        responseHeader.text = model.response?.formattedHeaders
        responseBody.text = model.response?.formattedBody
        copyResponseHeader.setOnClickListener { copyToClipboard(responseHeader.text.toString()) }
        copyResponseBody.setOnClickListener { copyToClipboard(responseBody.text.toString()) }
        if (model.response?.isBodyFormatted == true) {
            searchResponseBody.isVisible = true
            searchResponseBody.setOnClickListener {
                startActivity(JsonViewerActivity.intent(this, responseBody.text.toString()))
            }
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
