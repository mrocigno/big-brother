package br.com.mrocigno.bigbrother.log

import android.util.Log
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherDatabaseTask.Companion.bbdb
import br.com.mrocigno.bigbrother.core.dao.LogDao
import br.com.mrocigno.bigbrother.core.utils.bbSessionId
import br.com.mrocigno.bigbrother.log.model.LogEntry
import br.com.mrocigno.bigbrother.log.model.LogEntryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BBLog(private val tag: String) {

    fun d(message: String) {
        addEntry(LogEntryType.DEBUG, tag, message)
        if (isLoggable) Log.d(tag, message)
    }

    fun d(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.DEBUG, tag, message, throwable)
        if (isLoggable) Log.d(tag, message, throwable)
    }

    fun i(message: String) {
        addEntry(LogEntryType.INFO, tag, message)
        if (isLoggable) Log.i(tag, message)
    }

    fun i(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.INFO, tag, message, throwable)
        if (isLoggable) Log.i(tag, message, throwable)
    }

    fun v(message: String) {
        addEntry(LogEntryType.VERBOSE, tag, message)
        if (isLoggable) Log.v(tag, message)
    }

    fun v(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.VERBOSE, tag, message, throwable)
        if (isLoggable) Log.v(tag, message, throwable)
    }

    fun e(message: String) {
        addEntry(LogEntryType.ERROR, tag, message)
        if (isLoggable) Log.e(tag, message)
    }

    fun e(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.ERROR, tag, message, throwable)
        if (isLoggable) Log.e(tag, message, throwable)
    }

    fun w(message: String) {
        addEntry(LogEntryType.WARN, tag, message)
        if (isLoggable) Log.w(tag, message)
    }

    fun w(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, message, throwable)
        if (isLoggable) Log.w(tag, message, throwable)
    }

    fun w(throwable: Throwable?) {
        addEntry(LogEntryType.WARN, tag, null, throwable)
        if (isLoggable) Log.w(tag, throwable)
    }

    fun wtf(message: String) {
        addEntry(LogEntryType.ASSERT, tag, message)
        if (isLoggable) Log.wtf(tag, message)
    }

    fun wtf(message: String?, throwable: Throwable?) {
        addEntry(LogEntryType.ASSERT, tag, message, throwable)
        if (isLoggable) Log.wtf(tag, message, throwable)
    }

    fun wtf(throwable: Throwable) {
        addEntry(LogEntryType.ASSERT, tag, null, throwable)
        if (isLoggable) Log.wtf(tag, throwable)
    }

    companion object {

        const val DEFAULT_TAG = "MINITRUE"

        var isLoggable = true

        private val job: Job = Job()
        private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + job)
        private val dao: LogDao? get() = bbdb?.logDao()

        fun addEntry(
            lvl: LogEntryType,
            tag: String,
            message: String? = null,
            throwable: Throwable? = null
        )  = scope.launch {
            val model = LogEntry(
                lvl = lvl,
                tag = tag,
                message = message,
                errorMessage = throwable?.message,
                errorStacktrace = throwable?.stackTraceToString()
            )
            model.track()
            dao?.insert(model.toEntity())
        }

        fun clear() = scope.launch {
            dao?.clearSession(bbSessionId)
        }

        internal fun getBySession(sessionId: Long) =
            dao?.getBySession(sessionId)?.map { data ->
                data.map { LogEntry(it) }
            }

        fun BigBrother.tag(tag: String = DEFAULT_TAG) = BBLog(tag)

        fun tag(tag: String = DEFAULT_TAG) = BBLog(tag)
    }
}