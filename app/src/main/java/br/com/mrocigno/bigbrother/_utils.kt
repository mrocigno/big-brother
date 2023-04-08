package br.com.mrocigno.bigbrother

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

fun AppCompatActivity.launchOnCreated(action: suspend () -> Unit) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.CREATED) {
        action.invoke()
    }
}