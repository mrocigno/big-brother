package br.com.mrocigno.bigbrother.ui.network

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import br.com.mrocigno.bigbrother.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class NetworkActivity : AppCompatActivity(R.layout.network_activity) {

    private val root: MotionLayout by lazy { findViewById(R.id.root) }
    private val successLoadList: AppCompatButton by lazy { findViewById(R.id.success_load_list) }
    private val slowLoadList: AppCompatButton by lazy { findViewById(R.id.slow_load_list) }
    private val progressBar: LinearProgressIndicator by lazy { findViewById(R.id.progress_bar) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }

    private val viewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViews()

        viewModel.list.collect(lifecycleScope) {
            empty { Toast.makeText(this@NetworkActivity, "empty", Toast.LENGTH_SHORT).show() }
            error { Toast.makeText(this@NetworkActivity, "error", Toast.LENGTH_SHORT).show() }
            data { Toast.makeText(this@NetworkActivity, "success", Toast.LENGTH_SHORT).show() }
            loading { progressBar.isVisible = it }
        }
    }

    private fun setupViews() {
        toolbar.setNavigationOnClickListener { root.transitionToStart() }
        successLoadList.setOnClickListener { viewModel.fetchList() }
        slowLoadList.setOnClickListener { viewModel.fetchListSlowly() }
    }
}