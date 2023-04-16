package br.com.mrocigno.bigbrother.ui.general

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.common.R as CR

class CustomPageActivity : AppCompatActivity(R.layout.custom_page_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val clazz = findViewById<AppCompatTextView>(R.id.fun_body1)
        val clazzSsb = SpannableStringBuilder(clazz.text)
        val clazzIndex = clazzSsb.indexOf("class")
        val clazzSpan = ForegroundColorSpan(getColor(CR.color.net_entry_put))
        clazzSsb.setSpan(clazzSpan, clazzIndex, clazzIndex + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        clazz.text = clazzSsb

        val string = findViewById<AppCompatTextView>(R.id.fun_body2)
        val stringSsb = SpannableStringBuilder(string.text)
        val stringIndex = stringSsb.indexOf("\"custom name\"")
        val stringSpan = ForegroundColorSpan(getColor(CR.color.net_entry_get))
        stringSsb.setSpan(stringSpan, stringIndex, stringIndex + 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        string.text = stringSsb
    }
}