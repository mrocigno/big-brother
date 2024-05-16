package br.com.mrocigno.bigbrother.network.ui

import android.content.Context
import android.webkit.WebView
import br.com.mrocigno.bigbrother.common.R
import br.com.mrocigno.bigbrother.network.model.NetworkEntryModel

class NetworkEntryTemplate(private val model: NetworkEntryModel) {

    private val fileName = "network-entry-details.html"

    private val requestHeaderParam = "%request_headers%"
    private val requestBodyParam = "%request_body%"
    private val responseHeaderParam = "%response_headers%"
    private val responseBodyParam = "%response_body%"
    private val textParam = "BBtextColor"
    private val backgroundParam = "BBbackgroundColor"
    private val backgroundSecondaryParam = "BBbackgroundSecondaryColor"
    private val backgroundTertiaryParam = "BBbackgroundTertiaryColor"

    fun getHtml(context: Context): String {
        val hexText = Integer.toHexString(context.getColor(R.color.text_title))
        val hexBackground = Integer.toHexString(context.getColor(R.color.background))
        val hexSecondary = Integer.toHexString(context.getColor(R.color.background_secondary))
        val hexTertiary = Integer.toHexString(context.getColor(R.color.background_tertiary))

        return context.assets.open(fileName).bufferedReader().use {
            it.readText()
                .replace(requestHeaderParam, model.request.headers.toHtml())
                .replace(requestBodyParam, model.request.formattedBody.toHtml())
                .replace(responseHeaderParam, model.response?.headers.toHtml())
                .replace(responseBodyParam, model.response?.formattedBody.toHtml())
                .replace(textParam, "%23${hexText.substring(2)}FF")
                .replace(backgroundParam, "%23${hexBackground.substring(2)}FF")
                .replace(backgroundSecondaryParam, "%23${hexSecondary.substring(2)}FF")
                .replace(backgroundTertiaryParam, "%23${hexTertiary.substring(2)}FF")
        }
    }

    private fun Map<String, List<String>>?.toHtml(): String {
        if (this.isNullOrEmpty()) return "empty"

        val builder = StringBuilder()
        this.keys.forEach {
            builder.append("<b>$it</b>")
            builder.append(": ")
            builder.append(this[it]?.joinToString(", "))
            builder.append("<br/>\n")
        }
        return builder.toString()
    }

    private fun String?.toHtml() = this
        ?.replace(" ", "&nbsp;")
        ?.replace("\n", "<br/>\n")
        .orEmpty()

    fun load(webView: WebView) {
        webView.loadData(
            getHtml(webView.context),
            "text/html",
            "UTF-8"
        )
    }
}