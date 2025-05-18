package br.com.mrocigno.bigbrother.network.ui

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.common.utils.copyToClipboard
import br.com.mrocigno.bigbrother.common.utils.isHtml
import br.com.mrocigno.bigbrother.common.utils.isJson
import br.com.mrocigno.bigbrother.common.utils.toHtml
import br.com.mrocigno.bigbrother.network.json.JsonViewerActivity
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel
import br.com.mrocigno.bigbrother.network.model.NetworkMultiPartModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class NetworkEntryTemplate(private val model: NetworkEntryModel) {

    private val fileName = "network-entry-details.html"

    private val requestHeaderParam = "%request_headers%"
    private val requestBodyParam = "%request_body%"
    private val responseHeaderParam = "%response_headers%"
    private val responseBodyParam = "%response_body%"
    private val textParam = "BBtextColor"
    private val textLinkParam = "BBtextLinkColor"
    private val backgroundParam = "BBbackgroundColor"
    private val backgroundSecondaryParam = "BBbackgroundSecondaryColor"
    private val backgroundTertiaryParam = "BBbackgroundTertiaryColor"

    private fun getHtml(context: Context): String {
        val hexText = Integer.toHexString(context.getColor(R.color.bb_text_title))
        val hexTextLink = Integer.toHexString(context.getColor(R.color.bb_text_hyperlink))
        val hexBackground = Integer.toHexString(context.getColor(R.color.bb_background))
        val hexSecondary = Integer.toHexString(context.getColor(R.color.bb_background_secondary))
        val hexTertiary = Integer.toHexString(context.getColor(R.color.bb_background_tertiary))

        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
                .replace(requestHeaderParam, model.request.headers.toHtml())
                .replace(requestBodyParam, model.request.formattedBody.bodyToHtml())
                .replace(responseHeaderParam, model.response?.headers.toHtml())
                .replace(responseBodyParam, model.response?.formattedBody.bodyToHtml())
                .replace(textParam, "${hexText.substring(2)}FF")
                .replace(textLinkParam, "${hexTextLink.substring(2)}FF")
                .replace(backgroundParam, "${hexBackground.substring(2)}FF")
                .replace(backgroundSecondaryParam, "${hexSecondary.substring(2)}FF")
                .replace(backgroundTertiaryParam, "${hexTertiary.substring(2)}FF")
                .replace("#", "%23")
        }
    }

    private fun String?.bodyToHtml() = when {
        this == null -> "empty"

        isHtml() -> """
            <iframe id="response-html" sandbox="allow-same-origin" srcdoc='${replace("'", "\"")}'></iframe>
        """.trimIndent()

        contains("bigBrotherIdentifier") -> {
            Json.decodeFromString<NetworkMultiPartModel>(this).toHtml()
        }

        else -> escapeHtml()
    }

    private fun String.escapeHtml(): String = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

    private fun String.fromHtml(): String = this
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")

    @SuppressLint("SetJavaScriptEnabled")
    fun load(webView: WebView) {
        webView.loadData(getHtml(webView.context), "text/html", "UTF-8")
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(object : Any() {

            @JavascriptInterface
            fun copyToClipboard(text: String) {
                webView.context.copyToClipboard(text.fromHtml())
            }

            @JavascriptInterface
            fun openJson(json: String) {
                if (!json.isJson()) return
                val context = webView.context
                context.startActivity(JsonViewerActivity.intent(context, json))
            }
        }, "NativeAndroid")
    }
}

