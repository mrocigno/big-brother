package br.com.mrocigno.bigbrother.network.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.route.checkIntent
import br.com.mrocigno.bigbrother.common.route.intentToProxyCreateRule
import br.com.mrocigno.bigbrother.common.route.intentToProxyListRules
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import br.com.mrocigno.bigbrother.common.utils.visible
import br.com.mrocigno.bigbrother.core.OutOfDomain
import br.com.mrocigno.bigbrother.network.R
import br.com.mrocigno.bigbrother.network.byStatusCode
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

@OutOfDomain
internal class NetworkEntryDetailsActivity : AppCompatActivity(R.layout.bigbrother_activity_network_entry) {

    private val root: MotionLayout by id(R.id.net_entry_details_root)
    private val toolbar: Toolbar by id(R.id.net_entry_details_toolbar)
    private val background: View by id(R.id.net_entry_details_background)
    private val statusCode: AppCompatTextView by id(R.id.net_entry_details_status_code)
    private val method: AppCompatTextView by id(R.id.net_entry_details_method)
    private val generalInfo: AppCompatTextView by id(R.id.net_entry_details_general_info)
    private val copyAll: AppCompatTextView by id(R.id.net_entry_details_copy_all)
    private val loading: View by id(R.id.net_entry_details_loading_container)
    private val proxyContainer: View by id(R.id.net_entry_details_proxy_container)
    private val proxyLabel: AppCompatTextView by id(R.id.net_entry_details_proxy_label)
    private val proxyRules: AppCompatButton by id(R.id.net_entry_details_proxy_rules)

    private val webView: WebView by id(R.id.net_entry_details_web)

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
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.updateLayoutParams<ConstraintLayout.LayoutParams> {
            setMargins(0, statusBarHeight, 0, 0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupContent() {
        loading.isVisible = true
        viewModel.getNetworkEntry(entryId).observe(this) { model ->
            statusCode.text = model.statusCode.toString()
            method.text = model.method
            generalInfo.text = model.formatInfo()
            copyAll.setOnClickListener { copyToClipboard(model.toCURL()) }
            setupProxyInfo(model)

            NetworkEntryTemplate(model).load(webView)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    loading.isVisible = false
                    background.byStatusCode(model.statusCode)
                }
            }

            var inverse = false
            var isScrolling = false
            var startY = -1f
            var height = 0
            background.doOnLayout { height = it.height }
            webView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = event.rawY
                        inverse = root.progress == 1f
                        isScrolling = webView.scrollY > 100
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isScrolling) return@setOnTouchListener false
                        root.progress =
                            if (inverse) 1f - ((event.rawY - startY) / height).coerceIn(0f, 1f)
                            else ((startY - event.rawY) / height).coerceIn(0f, 1f)

                        if (root.progress in 0.001f..0.999f) {
                            webView.scrollY = 0
                            webView.isVerticalScrollBarEnabled = false
                        } else {
                            webView.isVerticalScrollBarEnabled = true
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isScrolling && root.progress in 0.001f .. 0.999f) when {
                            event.rawY < startY -> root.transitionToEnd()
                            event.rawY > startY -> root.transitionToStart()
                        }
                        isScrolling = false
                        startY = -1f
                    }
                }
                false
            }
        }
    }

    private fun setupProxyInfo(model: NetworkEntryModel) {
        if (!checkIntent(intentToProxyListRules(null))) return
        val ids = model.proxyRules?.split(", ")
            ?.map { it.toLong() }
            ?.toLongArray()
            ?: longArrayOf()

        proxyContainer.visible()
        if (ids.isEmpty()) {
            proxyLabel.text = getString(R.string.bigbrother_net_entry_details_zero_proxy_rules)
            proxyRules.text = getString(R.string.bigbrother_net_entry_details_zero_proxy_button)
            proxyRules.setOnClickListener {
                intentToProxyCreateRule(model.method, model.fullUrl).run(::startActivity)
            }
        } else {
            proxyLabel.text = resources.getQuantityString(R.plurals.bigbrother_net_entry_details_proxy_rules, ids.size, ids.size)
            proxyRules.text = resources.getQuantityString(R.plurals.bigbrother_net_entry_details_proxy_button, ids.size)
            proxyRules.setOnClickListener {
                intentToProxyListRules(ids).run(::startActivity)
            }
        }
    }

    private fun NetworkEntryModel.formatInfo() = SpannableStringBuilder().apply {
        append(getString(R.string.bigbrother_network_hour_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        append("  ")
        append(hour)
        appendLine()
        append(getString(R.string.bigbrother_network_elapsed_time_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        append("  ")
        append(elapsedTime)
        appendLine()
        append(getString(R.string.bigbrother_network_url_info), StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
