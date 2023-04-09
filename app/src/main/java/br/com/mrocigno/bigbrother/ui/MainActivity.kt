package br.com.mrocigno.bigbrother.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.AppCompatRadioButton
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.log.BBLog
import br.com.mrocigno.bigbrother.log.BBLog.Companion.tag
import br.com.mrocigno.bigbrother.ui.network.NetworkActivity
import br.com.mrocigno.bigbrother.ui.report.ReportActivity

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNetworkGroup()
        setupReportGroup()
        setupLogGroup()
    }

    override fun onResume() {
        super.onResume()
        setupLogcatToggle()
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

    private fun setupLogcatToggle() {
        val logEnabled = findViewById<AppCompatRadioButton>(R.id.logcat_enabled)
        val logDisabled = findViewById<AppCompatRadioButton>(R.id.logcat_disabled)

        logEnabled.isChecked = false
        logEnabled.isChecked = false

        if (BBLog.isLoggable) {
            logEnabled.isChecked = true
        } else {
            logDisabled.isChecked = true
        }

        logEnabled.setOnClickListener {
            logDisabled.isChecked = false
            BBLog.isLoggable = true
        }

        logDisabled.setOnClickListener {
            logEnabled.isChecked = false
            BBLog.isLoggable = false
        }
    }

    private fun setupNetworkGroup() {
        findViewById<View>(R.id.open_network_button).setOnClickListener {
            startActivity(Intent(this, NetworkActivity::class.java))
        }
    }

    private fun setupReportGroup() {
        findViewById<View>(R.id.open_report_button).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
    }

    private fun setupLogGroup() {
        findViewById<View>(R.id.log_debug).setOnClickListener {
            BigBrother.tag("custom tag name").d("This a debug log example", Exception("custom exception for debug"))
        }
        findViewById<View>(R.id.log_error).setOnClickListener {
            BigBrother.tag("custom tag name").e("This a error log example", Exception("custom exception for error"))
        }
        findViewById<View>(R.id.log_info).setOnClickListener {
            BigBrother.tag("custom tag name").i("This a info log example", Exception("custom exception for info"))
        }
        findViewById<View>(R.id.log_warn).setOnClickListener {
            BigBrother.tag("custom tag name").w("This a warn log example", Exception("custom exception for warn"))
        }
        findViewById<View>(R.id.log_verbose).setOnClickListener {
            BigBrother.tag("custom tag name").v("This a verbose log example", Exception("custom exception for verbose"))
        }
    }
}