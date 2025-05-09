package br.com.mrocigno.bigbrother.proxy.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.proxy.R
import br.com.mrocigno.bigbrother.proxy.randomName
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProxyActivity : AppCompatActivity(R.layout.bigbrother_activity_proxy) {

    private val ruleNameLayout: TextInputLayout by id(R.id.proxy_rule_name_layout)
    private val ruleName: TextInputEditText by id(R.id.proxy_rule_name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = getColor(br.com.mrocigno.bigbrother.common.R.color.bb_text_title_inverse)

        setupFormFields()
    }

    private fun setupFormFields() {
        ruleName.setText(randomName())
        ruleNameLayout.setEndIconOnClickListener {
            ruleName.setText(randomName())
        }
    }

    companion object {

        fun intent(context: Context) =
            android.content.Intent(context, ProxyActivity::class.java)
    }
}