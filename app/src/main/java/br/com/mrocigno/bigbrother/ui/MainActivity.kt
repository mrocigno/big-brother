package br.com.mrocigno.bigbrother.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.edit
import br.com.mrocigno.bigbrother.BuildConfig
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.ui.automator.PlaygroundActivity
import br.com.mrocigno.bigbrother.ui.compose.ComposableActivity
import br.com.mrocigno.bigbrother.ui.general.CustomPageActivity
import br.com.mrocigno.bigbrother.ui.general.OutOfDomainActivity
import br.com.mrocigno.bigbrother.ui.network.NetworkActivity
import br.com.mrocigno.bigbrother.ui.report.ReportActivity
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val version: AppCompatTextView by id(R.id.version)
    private val openNetworkButton: AppCompatButton by id(R.id.open_network_button)
    private val openReportButton: AppCompatButton by id(R.id.open_report_button)
    private val openAutomatorButton: AppCompatButton by id(R.id.open_automator_button)
    private val logDebug: AppCompatButton by id(R.id.log_debug)
    private val logError: AppCompatButton by id(R.id.log_error)
    private val logWarn: AppCompatButton by id(R.id.log_warn)
    private val logInfo: AppCompatButton by id(R.id.log_info)
    private val logAssert: AppCompatButton by id(R.id.log_assert)
    private val logVerbose: AppCompatButton by id(R.id.log_verbose)
    private val outOfDomain: AppCompatButton by id(R.id.out_of_domain)
    private val customPage: AppCompatButton by id(R.id.custom_page)
    private val composeActivity: AppCompatButton by id(R.id.compose_activity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupVersion()
        setupNetworkGroup()
        setupReportGroup()
        setupAutomatorGroup()
        setupLogGroup()
        setupGeneralGroup()

        createTestSharedPreferences()
    }

    private fun setupVersion() {
        version.text = BuildConfig.VERSION_NAME
    }

    private fun createTestSharedPreferences() {
        val pref = getSharedPreferences("filename", MODE_PRIVATE)
        if (pref.contains("int")) return
        pref.edit {
            putInt("int", 1)
            putBoolean("boolean", true)
            putLong("long", 1000L)
            putFloat("float", 0.1f)
            putString("string", "string")
            putString("json", """
                |{
                |    "string": "string",
                |    "int": 1,
                |    "boolean": true,
                |    "long": 1000,
                |    "float": 0.1,
                |    "array": [
                |        "string1",
                |        "string2"
                |    ]
                |}
                """.trimMargin()
            )
            putStringSet("stringSet", setOf("string1", "string2"))
        }
    }

    override fun onResume() {
        super.onResume()
        setupAppThemeToggle()
    }

    private fun setupAppThemeToggle() {
        val darkmode = findViewById<AppCompatRadioButton>(R.id.dark_mode_radio)
        val lightmode = findViewById<AppCompatRadioButton>(R.id.light_mode_radio)

        darkmode.isChecked = false
        lightmode.isChecked = false

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> darkmode.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> lightmode.isChecked = true
        }

        lightmode.setOnClickListener {
            darkmode.isChecked = false
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        darkmode.setOnClickListener {
            lightmode.isChecked = false
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
    }

    private fun setupNetworkGroup() {
        openNetworkButton.setOnClickListener {
            startActivity(Intent(this, NetworkActivity::class.java))
        }
    }

    private fun setupReportGroup() {
        openReportButton.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
    }

    private fun setupAutomatorGroup() {
        openAutomatorButton.setOnClickListener {
            startActivity(Intent(this, PlaygroundActivity::class.java))
        }
    }

    private fun setupLogGroup() {
        logDebug.setOnClickListener {
            Timber.tag("custom tag name").d("This a debug log example")
        }
        logDebug.setOnLongClickListener {
            Timber.tag("custom tag name").d("You just found an easter egg")
            true
        }
        logError.setOnClickListener {
            Timber.tag("custom tag name").e(Exception("custom exception for error"), "This a error log example")
        }
        logInfo.setOnClickListener {
            Timber.tag("custom tag name").i("This a info log example")
        }
        logWarn.setOnClickListener {
            Timber.tag("custom tag name").w(Exception("custom exception for warn"), "This a warn log example")
        }
        logVerbose.setOnClickListener {
            Timber.tag("custom tag name").v("This a verbose log example")
        }
        logAssert.setOnClickListener {
            Timber.tag("custom tag name").wtf(Exception("custom exception for assert"), "This a assert log example")
        }
    }

    private fun setupGeneralGroup() {
        outOfDomain.setOnClickListener {
            startActivity(Intent(this, OutOfDomainActivity::class.java))
        }

        customPage.setOnClickListener {
            startActivity(Intent(this, CustomPageActivity::class.java))
        }

        composeActivity.setOnClickListener {
            startActivity(Intent(this, ComposableActivity::class.java))
        }
    }
}