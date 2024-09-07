package br.com.mrocigno.bigbrother.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

internal class DatabaseTask : BigBrotherTask() {

    val databases: HashMap<String, DatabaseHelper> = hashMapOf()
    val sharedPreferences: HashMap<String, SharedPreferencesHelper> = hashMapOf()

    private val supervisor = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisor)

    override fun onCreate(): Boolean {
        listDefaultDatabases()
        listSharedPreferences()
        return super.onCreate()
    }

    fun listDefaultDatabases() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val databaseDir = context.databasesDir ?: run {
            Log.w("BIGBROTHER", "DatabaseDir not found")
            return null
        }

        databaseDir.forEachDatabase {
            databases[name] = this
        }
    } else Unit

    fun listSharedPreferences() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val sharedPreferencesDir = context?.sharedPreferencesDir ?: run {
            Log.w("BIGBROTHER", "SharedPreferencesDir not found")
            return null
        }

        sharedPreferencesDir.listFiles { file -> file.canWrite() }?.forEach { file ->
            sharedPreferences[file.name] = SharedPreferencesHelper(file)
        }
    } else Unit

    private fun File.forEachDatabase(block: suspend DatabaseHelper.() -> Unit) =
        listFiles { file -> file.canWrite() }?.forEach { file ->
            coroutineScope.launch {
                runCatching { DatabaseHelper(file) }
                    .getOrNull()
                    ?.block()
            }
        }

    private val Context?.databasesDir: File?
        @RequiresApi(Build.VERSION_CODES.N)
        get() = this?.dataDir
            ?.listFiles { file -> file.name == "databases" }
            ?.firstOrNull()

    private val Context?.sharedPreferencesDir: File?
        @RequiresApi(Build.VERSION_CODES.N)
        get() = this?.dataDir
            ?.listFiles { file -> file.name == "shared_prefs" }
            ?.firstOrNull()
}