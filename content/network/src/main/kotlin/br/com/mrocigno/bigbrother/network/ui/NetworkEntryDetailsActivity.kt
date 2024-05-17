package br.com.mrocigno.bigbrother.network.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.network.R
import br.com.mrocigno.bigbrother.network.byStatusCode
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

@OutOfDomain
class NetworkEntryDetailsActivity : AppCompatActivity(R.layout.bigbrother_activity_network_entry) {

    private val root: MotionLayout by lazy { findViewById(R.id.net_entry_details_root) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.net_entry_details_toolbar) }
    private val background: View by lazy { findViewById(R.id.net_entry_details_background) }
    private val statusCode: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_status_code) }
    private val method: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_method) }
    private val generalInfo: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_general_info) }
    private val copyAll: AppCompatTextView by lazy { findViewById(R.id.net_entry_details_copy_all) }
    private val loading: View by lazy { findViewById(R.id.net_entry_details_loading_container) }

    private val webView: WebView by lazy { findViewById(R.id.net_entry_details_web) }

    private val entryId: Long by lazy { intent.getLongExtra(ENTRY_ID_ARG, -1) }
    private val viewModel: NetworkEntryDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (entryId == -1L) { finish(); return }

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
        loading.isVisible = true
        viewModel.getNetworkEntry(entryId).observe(this) { model ->
            statusCode.text = model.statusCode.toString()
            method.text = model.method
            generalInfo.text = model.formatInfo()
            copyAll.setOnClickListener { copyToClipboard(model.toCURL()) }

            NetworkEntryTemplate(model).load(webView)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    loading.isVisible = false
                    background.byStatusCode(model.statusCode)
                }
            }
        }

        webView.setOnScrollChangeListener { _, _, currentY, _, _ ->
            val newProgress = currentY.toFloat() / webView.height
            root.progress = newProgress
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

        private const val ENTRY_ID_ARG = "br.com.mrocigno.ENTRY_ID_ARG"

        fun intent(context: Context, entryId: Long) =
            Intent(context, NetworkEntryDetailsActivity::class.java)
                .putExtra(ENTRY_ID_ARG, entryId)
    }
}
