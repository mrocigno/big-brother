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

    private val supervisor = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisor)

    override fun onCreate() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val databaseDir = context.databasesDir ?: run {
            Log.w("BIGBROTHER", "DatabaseDir not found")
            return false
        }

        list(databaseDir)
        super.onCreate()
    } else false

    private fun list(databaseDir: File) = databaseDir.forEachDatabase {
        databases[name] = this
    }

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
}